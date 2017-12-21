package com.qsr.sdk.component.filestorage.provider.alioss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.filestorage.FileMetadata;
import com.qsr.sdk.component.filestorage.FileStorage;
import com.qsr.sdk.component.filestorage.FileStorageProvider;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.util.IOUtils;
import com.qsr.sdk.util.Md5Util;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//import com.aliyun.oss.common.utils.IOUtils;

public class AliOss extends AbstractComponent implements FileStorage {

	private final String ENDPOINT;
	private final String ACCESS_ID;
	private final String ACCESS_KEY;
	private final String BUCKET_NAME;
	private final String BASEURL;
	public static final int PROVIDER_ID = 2;
	// private static final long multi_part_size = 5000000;
	private static final long PART_SIZE = 5 * 1024 * 1024L; // 每个Part的大小，最小为5MB
	private static final long READ_SIZE = 1024;
	private static final int CONCURRENCIES = 2; // 上传Part的并发线程数。
	final static Logger logger = LoggerFactory.getLogger(AliOss.class);
	private OSSClient client;

	private static String user_metadata_file_md5 = "filemd5";

	public AliOss(FileStorageProvider provider, Map<?, ?> config) {
		super(provider);
		//this.
		try {
			//Properties pp = new Properties();
			//pp.load(WorkingResourceUtil.getInputStream(propertiesFileName));
			Parameter p = new Parameter(config);
			BUCKET_NAME = p.s("bucket");
			ACCESS_ID = p.s("access_id");
			ACCESS_KEY = p.s("access_key");
			ENDPOINT = p.s("endpoint");
			BASEURL = p.s("baseurl");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		ClientConfiguration clientConfig = new ClientConfiguration();
		client = new OSSClient(ENDPOINT, ACCESS_ID, ACCESS_KEY, clientConfig);
	}

	private static class UploadPartThread implements Runnable {
		private File uploadFile;
		private String bucket;
		private String object;
		//private long start;
		//private long size;
		//private List<PartETag> eTags;
		//private int partId;
		private PartInfo partInfo;
		private OSSClient client;
		private String uploadId;

		UploadPartThread(OSSClient client, String bucket, String object,
				File uploadFile, String uploadId, PartInfo partInfo) {
			this.uploadFile = uploadFile;
			this.bucket = bucket;
			this.object = object;
			//this.start = start;
			//this.size = partSize;
			//this.eTags = eTags;
			//this.partId = partId;
			this.partInfo = partInfo;
			this.client = client;
			this.uploadId = uploadId;
		}

		@Override
		public void run() {

			InputStream in = null;
			try {
				in = new FileInputStream(uploadFile);
				in.skip(partInfo.start);

				UploadPartRequest uploadPartRequest = new UploadPartRequest();
				uploadPartRequest.setBucketName(bucket);
				uploadPartRequest.setKey(object);
				uploadPartRequest.setUploadId(uploadId);
				uploadPartRequest.setInputStream(in);
				uploadPartRequest.setPartSize(partInfo.size);
				uploadPartRequest.setPartNumber(partInfo.partIndex);

				UploadPartResult uploadPartResult = client
						.uploadPart(uploadPartRequest);
				if (!partInfo.partMd5.equalsIgnoreCase(uploadPartResult
						.getETag())) {
					throw new IOException("上传文件错误:etag not matched,"
							+ uploadFile.getPath());
				}
				partInfo.ETag = uploadPartResult.getETag();
				partInfo.partETag = uploadPartResult.getPartETag();

				//eTags.add(uploadPartResult.getPartETag());

			} catch (Exception e) {
				logger.error("UploadPartThread", e);
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
	}

	private static class BlockDownloadThread extends Thread {
		// 当前线程的下载开始位置
		private long startPos;

		// 当前线程的下载结束位置
		private long endPos;

		// 保存文件路径
		private String localFilePath;

		private String bucketName;
		private String fileKey;
		private List<String> eTags;
		private OSSClient client;

		public BlockDownloadThread(OSSClient client, long startPos,
				long endPos, String localFilePath, String bucketName,
				String fileKey, List<String> eTags) {
			this.client = client;
			this.startPos = startPos;
			this.endPos = endPos;
			this.localFilePath = localFilePath;
			this.bucketName = bucketName;
			this.fileKey = fileKey;
			this.eTags = eTags;
		}

		@Override
		public void run() {

			RandomAccessFile file = null;
			OSSObject ossObject = null;
			try {
				file = new RandomAccessFile(localFilePath, "rw");
				GetObjectRequest getObjectRequest = new GetObjectRequest(
						bucketName, fileKey);
				getObjectRequest.setRange(startPos, endPos);
				ossObject = client.getObject(getObjectRequest);
				file.seek(startPos);
				int bufSize = 1024;
				byte[] buffer = new byte[bufSize];
				int bytesRead;
				while ((bytesRead = ossObject.getObjectContent().read(buffer)) > -1) {
					file.write(buffer, 0, bytesRead);
				}
				eTags.add(ossObject.getObjectMetadata().getETag());
			} catch (IOException e) {
				logger.error("BlockDownloadThread", e);
			} catch (Exception e) {
				logger.error("BlockDownloadThread", e);
			} finally {
				IOUtils.closeQuietly(ossObject.getObjectContent());
				IOUtils.closeQuietly(file);
			}

		}
	}

	@Override
	public FileMetadata uploadFile(File file, String fileUri,
								   FileMetadata fileMetadata) throws IOException {
		// TODO Auto-generated method stub
		ObjectMetadata objectMeta = new ObjectMetadata();
		if (!StringUtil.isEmptyOrNull(fileMetadata.getFileName())) {
			objectMeta.setContentDisposition("attachment; filename=\""
					+ fileMetadata.getFileName() + "\"");
		}
		String fileMd5 = fileMetadata.getFileMd5();
		objectMeta.addUserMetadata(user_metadata_file_md5, fileMd5);
		//objectMeta.setContentMD5(fileMd5);
		String etag;
		if (file.length() > PART_SIZE) {
			etag = uploadBigFile(file, fileUri, objectMeta);
		} else {
			etag = uploadSmallFile(file, fileUri, objectMeta);
		}
		fileMetadata.setMine(objectMeta.getContentType());
		fileMetadata.setFileETag(etag);

		return fileMetadata;

	}

	private String uploadSmallFile(File file, String fileUri,
			ObjectMetadata objectMeta) throws IOException {
		PutObjectRequest req = new PutObjectRequest(BUCKET_NAME, fileUri, file);
		req.setMetadata(objectMeta);
		PutObjectResult result;
		try {
			result = client.putObject(req);
			return result.getETag();
		} catch (OSSException e) {
			throw new IOException(e);
		} catch (ClientException e) {
			throw new IOException(e);
		}

	}

	private static class PartInfo {
		int partIndex;
		long start;
		long size;
		String partMd5;
		PartETag partETag;
		String ETag;
	}

	// 根据文件的大小和每个Part的大小计算需要划分的Part个数。
	private static PartInfo[] calPartCount(File f) throws IOException {
		long filesize = f.length();
		int partCount = (int) (filesize / PART_SIZE);
		if (filesize % PART_SIZE != 0) {
			partCount++;
		}
		PartInfo[] parts = new PartInfo[partCount];
		FileInputStream input = null;
		try {
			input = new FileInputStream(f);
			byte[] readbuffer = new byte[1024];
			for (int i = 0; i < partCount; i++) {
				PartInfo p = new PartInfo();
				p.partIndex = i + 1;
				p.start = i * PART_SIZE;
				p.size = PART_SIZE < filesize - p.start ? PART_SIZE : filesize
						- p.start;

				int readsize = 0;
				MessageDigest digest;
				try {
					digest = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException(e);
				}
				while (readsize < p.size) {
					long ready = READ_SIZE < p.size - readsize ? READ_SIZE
							: p.size - readsize;
					int r = input.read(readbuffer, 0, (int) ready);
					if (r == -1) {
						throw new IllegalStateException("读取文件错误:" + f.getPath());
					}
					readsize += r;
					digest.update(readbuffer, 0, r);
				}
				byte[] byMd5 = digest.digest();
				p.partMd5 = StringUtil.toHexString(byMd5);
				parts[i] = p;

			}
		} finally {
			IOUtils.closeQuietly(input);
		}

		return parts;
	}

	// 初始化一个Multi-part upload请求。
	private static String initMultipartUpload(OSSClient client,
			String bucketName, String key, ObjectMetadata objectMeta)
			throws OSSException, ClientException {
		InitiateMultipartUploadRequest initUploadRequest = new InitiateMultipartUploadRequest(
				bucketName, key, objectMeta);
		InitiateMultipartUploadResult initResult = client
				.initiateMultipartUpload(initUploadRequest);
		String uploadId = initResult.getUploadId();
		return uploadId;
	}

	// 完成一个multi-part请求。
	private static CompleteMultipartUploadResult completeMultipartUpload(
			OSSClient client, String bucketName, String key, String uploadId,
			List<PartETag> eTags) throws OSSException, ClientException {
		// 为part按partnumber排序
		Collections.sort(eTags, new Comparator<PartETag>() {

			public int compare(PartETag arg0, PartETag arg1) {
				PartETag part1 = arg0;
				PartETag part2 = arg1;

				return part1.getPartNumber() - part2.getPartNumber();
			}
		});

		CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(
				bucketName, key, uploadId, eTags);

		return client.completeMultipartUpload(completeMultipartUploadRequest);
	}

	private String uploadBigFile(File file, String fileUrl,
			ObjectMetadata objectMeta) throws IOException {
		boolean complete = false;
		PartInfo[] partInfos = calPartCount(file);
		if (partInfos.length <= 1) {
			throw new IllegalArgumentException("要上传文件的大小必须大于一个Part的字节数："
					+ PART_SIZE);
		}
		String uploadId;
		try {
			uploadId = initMultipartUpload(client, BUCKET_NAME, fileUrl,
					objectMeta);
		} catch (OSSException e) {
			throw new IOException(e);
		} catch (ClientException e) {
			throw new IOException(e);
		}
		try {

			ExecutorService pool = Executors.newFixedThreadPool(CONCURRENCIES);

			for (int i = 0; i < partInfos.length; i++) {
				//long start = PART_SIZE * i;
				//long curPartSize = PART_SIZE < file.length() - start ? PART_SIZE
				//		: file.length() - start;

				pool.execute(new UploadPartThread(client, BUCKET_NAME, fileUrl,
						file, uploadId, partInfos[i]));
			}

			pool.shutdown();
			while (!pool.isTerminated()) {
				try {
					pool.awaitTermination(1, TimeUnit.SECONDS);
				} catch (InterruptedException e) {

				}
			}
			List<PartETag> eTags = new ArrayList<PartETag>();
			for (int i = 0; i < partInfos.length; i++) {
				if (partInfos[i].partETag == null) {
					throw new IllegalStateException("Multipart上传失败，有Part未上传成功。");
				}
				eTags.add(partInfos[i].partETag);
			}

			//			if (eTags.size() != partCount) {
			//
			//			}

			CompleteMultipartUploadResult result;
			try {
				result = completeMultipartUpload(client, BUCKET_NAME, fileUrl,
						uploadId, eTags);
			} catch (OSSException e) {
				throw new IOException(e);
			} catch (ClientException e) {
				throw new IOException(e);
			}

			complete = true;
			return result.getETag();

		} finally {
			if (!complete) {
				try {
					client.abortMultipartUpload(new AbortMultipartUploadRequest(
							BUCKET_NAME, fileUrl, uploadId));
				} catch (OSSException e) {
					logger.error("abortMultipartUpload", e);
				} catch (ClientException e) {
					logger.error("abortMultipartUpload", e);
				}
			}
		}
	}

	private static int calPartCount(long fileLength, long partSize) {
		int partCount = (int) (fileLength / partSize);
		if (fileLength % partSize != 0) {
			partCount++;
		}
		return partCount;
	}

	@Override
	public FileMetadata downloadFile(String fileUrl, String filePath)
			throws IOException {
		// TODO Auto-generated method stub

		ObjectMetadata objectMetadata = client.getObjectMetadata(BUCKET_NAME,
				fileUrl);
		long partSize = 1024 * 1024 * 5;
		long fileLength = objectMetadata.getContentLength();
		RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		file.setLength(fileLength);
		file.close();

		int partCount = calPartCount(fileLength, partSize);
		System.out.println("需要下载的文件分块数：" + partCount);
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		List<String> eTags = Collections
				.synchronizedList(new ArrayList<String>());

		for (int i = 0; i < partCount; i++) {
			final long startPos = partSize * i;
			final long endPos = partSize
					* i
					+ (partSize < (fileLength - startPos) ? partSize
							: (fileLength - startPos)) - 1;

			executorService.execute(new BlockDownloadThread(client, startPos,
					endPos, filePath, BUCKET_NAME, fileUrl, eTags));
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {
			try {
				executorService.awaitTermination(3, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
			}
		}

		if (eTags.size() != partCount) {
			throw new IllegalStateException("下载失败，有Part未下载成功。");
		}
		//		String fileMd5 = Md5Util.getFileMd5(new File(filePath));
		//		if(!fileMd5.equalsIgnoreCase(objectMetadata.getContentMD5())){
		//			
		//		}
		//objectMetadata.get
		String fileMd5 = Md5Util.getFileMd5(new File(filePath));
		return new FileMetadata(null, fileLength, fileMd5,
				objectMetadata.getContentType(), objectMetadata.getETag());
	}

	@Override
	public void deleteFile(String fileUrl) {

		try {
			client.deleteObject(BUCKET_NAME, fileUrl);
		} catch (OSSException e) {
			logger.error("delete file :" + fileUrl, e);
		} catch (ClientException e) {
			logger.error("delete file :" + fileUrl, e);
		}

	}

	@Override
	public String getFileUrl(String fileUri) {
		return BASEURL + fileUri;
	}

	protected void test() {
		String fileUri = "userfiles/3/idcard_photo1";
		// CopyObjectRequest cor = new CopyObjectRequest();
		// client.copyObject(copyObjectRequest);

		ObjectMetadata metadate = new ObjectMetadata();
		metadate.setExpirationTime(new Date());
		// client.
		client.putObject(BUCKET_NAME, fileUri, null, metadate);
	}

	public static void main(String[] args) throws IOException {
		//OSS oss = new OSS();
		//oss.test();
		// oss.uploadFile(new File("d:/apk/DTMFModem.apk"),
		// "aaa/DTMFModem.apk");
		// oss.downloadFile("aaa/1.zip", "d:/temp/xxx.zip");

	}

	@Override
	public boolean touchFile(String fileUri) throws IOException {
		CopyObjectRequest req = new CopyObjectRequest(BUCKET_NAME, fileUri,
				BUCKET_NAME, fileUri);
		try {
			CopyObjectResult result = client.copyObject(req);
		} catch (OSSException e) {
			throw new IOException(e);
		} catch (ClientException e) {
			throw new IOException(e);
		}
		return true;
	}

	@Override
	public boolean isFileExist(String fileUri) {
		try {
			return client.doesObjectExist(BUCKET_NAME, fileUri);
		} catch (OSSException e) {
			logger.error("isFileExist", e);
			//throw new IOException(e);
		} catch (ClientException e) {
			logger.error("isFileExist", e);
			//throw new IOException(e);
		}
		return false;
	}

	public Date getExpiredTime(Date expiredTime) {
		//		Date utcDate = DateUtils.addHours(expiredTime, -8);
		//		utcDate = DateUtils.ceiling(utcDate, Calendar.DATE);
		//		Date localDate = DateUtils.addHours(utcDate, 8);
		//
		//		return localDate;

		return expiredTime;
	}

	@Override
	public FileMetadata getFileMetadata(String fileUri) throws IOException {

		ObjectMetadata objectMetadata = null;

		try {
			objectMetadata = client.getObjectMetadata(BUCKET_NAME, fileUri);
		} catch (OSSException e) {
			throw new IOException(e);
		} catch (ClientException e) {
			throw new IOException(e);
		}

		String fileMd5 = objectMetadata.getUserMetadata().get(
				user_metadata_file_md5);
		FileMetadata result = new FileMetadata(null,
				objectMetadata.getContentLength(), fileMd5,
				objectMetadata.getContentType(), objectMetadata.getETag());

		return result;
	}

}

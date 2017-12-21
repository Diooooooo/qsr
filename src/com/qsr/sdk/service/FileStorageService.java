package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qsr.sdk.component.filestorage.FileMetadata;
import com.qsr.sdk.component.filestorage.FileStorage;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.jfinal.DbUtil;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.exception.ServiceException;
import com.qsr.sdk.service.helper.FileStorageHelper;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.Md5Util;
import com.qsr.sdk.util.ParameterUtil;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileStorageService extends Service {

	final static Logger logger = LoggerFactory
			.getLogger(FileStorageService.class);
	final static int max_try_times = 3;

	FileStorageService() {
		super();
		// TODO Auto-generated constructor stub
	}

	private FileStorage getFileStorage(int configId) throws ServiceException {

		try {
			return FileStorageHelper.getFileStorage(configId);
		} catch (ApiException e) {
			throw new ServiceException(getServiceName(), e);
		}
	}

	public String getFileUrl(int fileId) {

		String sql = "select concat(p.host_url,fs.filepath) as filepath from qsr_filestorage fs "
				+ "inner join qsr_filestorage_provider p on fs.provider_id=p.provider_id "
				+ "where fs.file_id=? ";
		Record record = Db.findFirst(sql, fileId);
		if (record == null) {
			return null;
		}
		return record.getStr("filepath");

	}

	public int uploadFile(int providerId, String fileUri, File file)
			throws ServiceException {
		return uploadFile(providerId, fileUri, file, null, 0, -1);
	}

	public int uploadFile(int providerId, String fileUri, File file,
			String fileName) throws ServiceException {
		return uploadFile(providerId, fileUri, file, fileName, 0, -1);
	}

	public String getFileMd5(File file) throws ServiceException {
		try {
			return Md5Util.getFileMd5(file);
		} catch (IOException e) {
			throw new ServiceException(getServiceName(),
					ErrorCode.IO_EXCEPTION, "文件访问错误", e);
		}
	}

	public String getFileMd5Uri(String fileUriPrefix, File file)
			throws ServiceException {
		String fileMd5;
		try {
			fileMd5 = Md5Util.getFileMd5(file);
		} catch (IOException e) {
			throw new ServiceException(getServiceName(),
					ErrorCode.IO_EXCEPTION, "文件访问错误", e);
		}
		String fileUri = fileUriPrefix + fileMd5.substring(0, 2) + "/"
				+ fileMd5.substring(2);
		return fileUri;
	}

	public int uploadFile(int providerId, String fileUri, File file,
			String fileName, int parentFileId, int expiredDays)
			throws ServiceException {
		FileStorage fileStorage = getFileStorage(providerId);
		try {
			String fileMd5 = Md5Util.getFileMd5(file);
			FileMetadata fileMetadata;
			if (fileStorage.isFileExist(fileUri)) {
				fileMetadata = fileStorage.getFileMetadata(fileUri);
				if (fileMetadata.getFileName() == null) {
					//fileMetadata.set
				}
			} else {
				fileMetadata = new FileMetadata(fileName, file.length(),
						fileMd5);
				int tryTimes = 1;
				boolean success = false;
				while (success == false) {
					try {
						fileMetadata = fileStorage.uploadFile(file, fileUri,
								fileMetadata);
						success = true;
					} catch (IOException e) {
						if (tryTimes >= max_try_times) {
							throw e;
						}
					}
					tryTimes++;
				}
			}
			int fileIds[] = { 0 };

			String sql2 = "select f.file_id from qsr_filestorage f where f.file_md5=?";
			fileIds[0] = ParameterUtil.i(Db.queryInt(sql2, fileMd5), 0);
			if (fileIds[0] == 0) {

				String sql3 = "insert qsr_filestorage(filepath,filename,mime,provider_id,parent_file_id,file_md5,file_size,file_createtime,file_expiredtime,etag) "
						+ "select i.filepath,i.filename,i.mime,i.provider_id,i.parent_file_id,i.file_md5,i.file_size,i.file_createtime,"
						+ "if(i.expired_days>0,now()+interval i.expired_days day ,'2099-12-31'),i.etag "
						+ "from (select ? as filepath,? as filename,? as mime,? as provider_id,"
						+ "? as parent_file_id,? as file_md5,? as file_size,now() as file_createtime,? as expired_days, ? as etag) i ";

				DbUtil.update(sql3, fileIds, fileUri,
						StringUtil.getEmptyString(fileName),
						StringUtil.getEmptyString(fileMetadata.getMine()),
						providerId, parentFileId, fileMetadata.getFileMd5(),
						fileMetadata.getFileSize(), expiredDays,
						fileMetadata.getFileETag());
			}

			return fileIds[0];
		} catch (IOException e) {
			throw new ServiceException(getServiceName(),
					ErrorCode.IO_EXCEPTION, "文件存储错误", e);
		}

	}

	private void deleteFile(Record record) {
		Parameter p = new Parameter(record2map(record));
		int fileId = p.i("file_id");
		int providerId = p.i("provider_id");
		String filePath = p.s("filepath");

		if (providerId > 0 && filePath != null) {

			try {
				FileStorage fileStorage = getFileStorage(providerId);
				fileStorage.deleteFile(filePath);
			} catch (Exception e) {
				logger.error("deleteFile:" + filePath + ",at " + providerId, e);
			}
		}
	}

	private String getFileKey(String fileUri) {
		return Md5Util.digest(fileUri);
	}


	public int deleteExpiredFiles() {
		int result = 0;
		synchronized (FileStorageService.class) {
			String sql = "select file_id,provider_id,filepath from  qsr_filestorage fs "
					+ "inner join (select now() as now) i "
					+ "where fs.deleted=0 and fs.file_expiredtime<i.now limit 20 ";
			while (true) {
				List<Record> list = Db.find(sql);
				if (list.size() == 0) {
					break;
				}
				for (Record record : list) {
					result++;
					deleteFile(record);
				}
			}

		}
		return result;
	}

}

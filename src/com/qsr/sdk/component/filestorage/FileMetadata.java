package com.qsr.sdk.component.filestorage;

public class FileMetadata {

	private final long fileSize;
	private final String fileMd5;
	private final String fileName;
	private String mine;
	private String fileETag;

	public FileMetadata(String fileName, long fileSize, String fileMd5) {
		this(fileName, fileSize, fileMd5, null, null);
	}

	public FileMetadata(String fileName, long fileSize, String fileMd5,
			String mine, String fileETag) {
		super();
		this.fileName = fileName;
		this.mine = mine;
		this.fileSize = fileSize;
		this.fileMd5 = fileMd5;
		this.fileETag = fileETag;
	}

	public String getFileName() {
		return fileName;
	}

	public String getMine() {
		return mine;
	}

	public long getFileSize() {
		return fileSize;
	}

	public String getFileMd5() {
		return fileMd5;
	}

	public String getFileETag() {
		return fileETag;
	}

	public void setMine(String mine) {
		this.mine = mine;
	}

	public void setFileETag(String fileETag) {
		this.fileETag = fileETag;
	}

}

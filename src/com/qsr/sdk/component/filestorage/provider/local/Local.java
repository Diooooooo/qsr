package com.qsr.sdk.component.filestorage.provider.local;

import com.qsr.sdk.component.AbstractComponent;
import com.qsr.sdk.component.filestorage.FileMetadata;
import com.qsr.sdk.component.filestorage.FileStorage;
import com.qsr.sdk.component.filestorage.FileStorageProvider;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Local extends AbstractComponent implements FileStorage {

	protected Local(FileStorageProvider provider) {
		super(provider);
	}

	public static final int PROVIDER_ID = 1;

	@Override
	public FileMetadata uploadFile(File file, String fileUrl,
                                   FileMetadata fileMetadata) {
		return null;
	}

	@Override
	public FileMetadata downloadFile(String fileUrl, String filePath) {
		return null;

	}

	@Override
	public void deleteFile(String fileUrl) {

	}

	@Override
	public String getFileUrl(String fileUri) {
		return null;
	}

	@Override
	public boolean touchFile(String fileUri) throws IOException {
		return false;
	}

	@Override
	public boolean isFileExist(String fileUri) {
		return false;
	}

	public Date getExpiredTime(Date expiredTime) {
		return null;
	}

	@Override
	public FileMetadata getFileMetadata(String fileUri) {
		return null;
	}

}

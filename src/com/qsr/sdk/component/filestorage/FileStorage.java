package com.qsr.sdk.component.filestorage;

import com.qsr.sdk.component.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public interface FileStorage extends Component {

	//public abstract Provider getProvider();

	public abstract FileMetadata uploadFile(File file, String fileUri,
			FileMetadata fileMetadata) throws IOException;

	public abstract FileMetadata getFileMetadata(String fileUri)
			throws IOException;

	public abstract FileMetadata downloadFile(String fileUri, String filePath)
			throws IOException;

	public abstract void deleteFile(String fileUri) throws IOException;

	public abstract String getFileUrl(String fileUri);

	public abstract boolean touchFile(String fileUri) throws IOException;

	public abstract boolean isFileExist(String fileUri);

	public abstract Date getExpiredTime(Date expiredTime);

}
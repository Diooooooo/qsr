package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.filestorage.FileStorage;
import com.qsr.sdk.component.filestorage.provider.alioss.AliOssProvider;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;

public class FileStorageHelper {

	public static FileStorage getFileStorage(int configId) throws ApiException {
		FileStorage fileStorage = ComponentProviderManager.getService(FileStorage.class,
				configId, configId);

		if (fileStorage == null) {
			throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER, "不存在的存储服务");
		}
		return fileStorage;
	}
}

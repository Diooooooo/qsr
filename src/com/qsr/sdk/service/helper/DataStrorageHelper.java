package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.datastorage.DataStorage;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;

public class DataStrorageHelper {

	public static DataStorage getDataStorage(int configId) throws ApiException {
		DataStorage dataStorage = ComponentProviderManager.getService(
				DataStorage.class, 1, configId);

		if (dataStorage == null) {
			throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER,
					"不存在的数据存储服务");
		}
		return dataStorage;
	}
}

package com.qsr.sdk.component.filestorage.provider.alioss;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.filestorage.FileStorage;
import com.qsr.sdk.component.filestorage.FileStorageProvider;

import java.util.Map;

public class AliOssProvider extends AbstractProvider<FileStorage> implements
        FileStorageProvider {

	public static final int PROVIDER_ID = 2;

	public AliOssProvider() {
		super(PROVIDER_ID);
	}

	@Override
	public FileStorage createComponent(int configId, Map<?, ?> config) {
		return new AliOss(this, config);
	}

}

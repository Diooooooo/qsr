package com.qsr.sdk.component.filestorage.provider.local;

import com.qsr.sdk.component.AbstractProvider;
import com.qsr.sdk.component.filestorage.FileStorage;
import com.qsr.sdk.component.filestorage.FileStorageProvider;

import java.util.Map;

public class LocalProvider extends AbstractProvider<FileStorage> implements FileStorageProvider {
    private static final int PROVIDER_ID = 1;
    public LocalProvider() {
        super(PROVIDER_ID);
    }

    @Override
    public FileStorage createComponent(int configId, Map<?, ?> config) {
        return new Local(this);
    }
}

package com.qsr.sdk.component.filestorage;

import com.qsr.sdk.component.Provider;

public interface FileStorageProvider extends Provider {

	FileStorage getComponent(int configId);

}

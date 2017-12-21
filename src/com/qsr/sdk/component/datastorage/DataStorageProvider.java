package com.qsr.sdk.component.datastorage;

import com.qsr.sdk.component.Provider;

public interface DataStorageProvider extends Provider {

	DataStorage getComponent(int configId);
}

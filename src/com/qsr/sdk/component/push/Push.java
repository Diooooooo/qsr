package com.qsr.sdk.component.push;

import com.qsr.sdk.component.Component;

public interface Push extends Component {

	public int pushSingleMessage(String userId, String message, int type)
			throws Exception;

	//public Provider getProvider();

}
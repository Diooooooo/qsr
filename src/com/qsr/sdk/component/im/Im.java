package com.qsr.sdk.component.im;

import com.qsr.sdk.component.Component;

public interface Im extends Component {

    void sendSignMessage(String targetId, String fromId, String message, int type);
}

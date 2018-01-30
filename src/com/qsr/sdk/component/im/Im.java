package com.qsr.sdk.component.im;

import com.qsr.sdk.component.Component;

public interface Im extends Component {

    void sendSignMessage(String targetId, String fromId, String message, int type);

    void registerUser(String name, String password, String avatar, String nickname);

    Long createChatRoom(String name, String desc, String owne);

    void addChatRoomMember(int roomId, String name);

    void deleteChatRoom(Long roomId);
}

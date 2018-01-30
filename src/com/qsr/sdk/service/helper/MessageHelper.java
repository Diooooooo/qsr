package com.qsr.sdk.service.helper;

import com.qsr.sdk.component.ComponentProviderManager;
import com.qsr.sdk.component.im.Im;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;

public class MessageHelper {
    private static final int PROVIDER_ID = 1;
    private static final int CONFIG = 1;

    public static void pushMessage(String from, String to, String message, int type) throws ApiException {
        Im im = ComponentProviderManager.getService(Im.class, PROVIDER_ID, CONFIG);
        if (null == im) {
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER, "没有找到对应的IM服务");
        }
        try {
            im.sendSignMessage(to, from, message, type);
        } catch (Throwable t) {
            throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, t.getMessage());
        }
    }

    public static void registerUser(String name, String password, String avatar, String nickname) throws ApiException {
        Im im = ComponentProviderManager.getService(Im.class, PROVIDER_ID, CONFIG);
        if (null == im) {
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER, "没有找到对应的IM服务");
        }
        try {
            im.registerUser(name, password, avatar, nickname);
        } catch (Throwable t) {
            throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, t.getMessage());
        }
    }

    public static Long createChatRoom(String name, String desc, String owner) throws ApiException {
        Im im = ComponentProviderManager.getService(Im.class, PROVIDER_ID, CONFIG);
        if (null == im) {
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER, "没有找到对应的IM服务");
        }
        try {
            return im.createChatRoom(name, desc, owner);
        } catch (Throwable t) {
            throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, t.getMessage());
        }
    }

    public static void addChatRoomMember(int roomId, String name) throws ApiException {
        Im im = ComponentProviderManager.getService(Im.class, PROVIDER_ID, CONFIG);
        if (null == im) {
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER, "没有找到对应的IM服务");
        }
        try {
            im.addChatRoomMember(roomId, name);
        } catch (Throwable t) {
            throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, t.getMessage());
        }
    }

    public static void deleteChatRoom(Long roomId) throws ApiException {
        Im im = ComponentProviderManager.getService(Im.class, PROVIDER_ID, CONFIG);
        if (null == im) {
            throw new ApiException(ErrorCode.NOT_EXIST_SERVICE_PROVIDER, "没有找到对应的IM服务器");
        }
        try {
            im.deleteChatRoom(roomId);
        } catch (Throwable t) {
            throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, t.getMessage());
        }
    }
}

package com.qsr.sdk.component.thirdaccount;

public class AccountInfo {

	private final int providerId;
	private final String domain;
	private final String userId;

	private final String nickName;
	private final String headImgUrl;

	public AccountInfo(int providerId, String domain, String userId,
			String nickName, String headImgUrl) {
		super();
		this.providerId = providerId;
		this.domain = domain;
		this.userId = userId;
		this.nickName = nickName;
		this.headImgUrl = headImgUrl;
	}

	public int getProviderId() {
		return providerId;
	}

	public String getDomain() {
		return domain;
	}

	public String getUserId() {
		return userId;
	}

	public String getNickName() {
		return nickName;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ThirdAccountInfo [providerId=");
		builder.append(providerId);
		builder.append(", domain=");
		builder.append(domain);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", nickName=");
		builder.append(nickName);
		builder.append(", headImgUrl=");
		builder.append(headImgUrl);
		builder.append("]");
		return builder.toString();
	}

}

package com.qsr.sdk.component.transfer.provider.wxtransfer;

import com.qsr.sdk.component.transfer.NotifyContent;
import com.qsr.sdk.component.transfer.TransferProvider;
import com.qsr.sdk.component.transfer.TransferRequest;
import com.qsr.sdk.component.transfer.TransferResponse;
import com.qsr.sdk.component.transfer.provider.AbstractTransfer;
import com.qsr.sdk.exception.ApiException;

import java.util.HashMap;
import java.util.Map;

public class WxTransfer extends AbstractTransfer {
    private static final String WX_GATEWAY_NEW = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    private static final String APP_ID = "";
    private static final String MCH_ID = "";
    private static final String[] SIGN_TYPE = {"MD5", "HMAC-SHA256"};
    private static final String TRADE_TYPE = "APP";
    private static final String NOTIFY_URL = "";
    private static final Map<String, String> ERROR_CODE_MAP = new HashMap<>();

    static {
        ERROR_CODE_MAP.put("NOAUTH", "商户无此接口权限");
        ERROR_CODE_MAP.put("NOTENOUGH", "余额不足");
        ERROR_CODE_MAP.put("ORDERPAID", "商户订单已支付");
        ERROR_CODE_MAP.put("ORDERCLOSED", "订单已关闭");
        ERROR_CODE_MAP.put("SYSTEMERROR", "系统错误");
        ERROR_CODE_MAP.put("APPID_NOT_EXIST", "APPID不存在");
        ERROR_CODE_MAP.put("MCHID_NOT_EXIST", "MCHID不存在");
        ERROR_CODE_MAP.put("APPID_MCHID_NOT_MATCH", "appid和mch_id不匹配");
        ERROR_CODE_MAP.put("LACK_PARAMS", "缺少参数");
        ERROR_CODE_MAP.put("OUT_TRADE_NO_USED", "商户订单号重复");
        ERROR_CODE_MAP.put("SIGNERROR", "签名错误");
        ERROR_CODE_MAP.put("XML_FORMAT_ERROR", "XML格式错误");
        ERROR_CODE_MAP.put("REQUIRE_POST_METHOD", "请使用post方法");
        ERROR_CODE_MAP.put("POST_DATA_EMPTY", "post数据为空");
        ERROR_CODE_MAP.put("NOT_UTF8", "编码格式错误");
    }

    protected WxTransfer(TransferProvider provider) {
        super(provider, "wx_transfer_seq");
    }

    @Override
    public int calcFee(int totalFee) throws ApiException {
        return 0;
    }

    @Override
    public TransferRequest transferRequest(String payeeAccount, String payeeAccountName, int actualFee) throws ApiException {
        return null;
    }

    @Override
    public void transfer(TransferRequest request) throws ApiException {

    }

    @Override
    public TransferResponse handleNotify(Map<String, String> notifyParams) throws ApiException {
        return null;
    }

    @Override
    public NotifyContent getNotifyContent(TransferResponse transferResponse) {
        return null;
    }
}

package com.qsr.sdk.component.payment.provider.weixin;

import com.qsr.sdk.component.payment.*;
import com.qsr.sdk.component.payment.provider.AbstractPayment;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WeixinPayment extends AbstractPayment {

	final static Logger logger = LoggerFactory.getLogger(WeixinPayment.class);
	public static final int PROVIDER_ID = 11;
	private static final String APPID = "wx6f24d4e313ed8d2e";
	private static final String MCH_ID = "1499166962";
	private static final String KEY = "key=c05bf0e0d1bfff509fa641fbe6321a72";
	private static final String ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	private static final String RETURN_CODE_SUCCESS = "SUCCESS";
	private static final String RETURN_CODE_FAIL = "FAIL";
	private static Map<String, String> codes = new HashMap<String, String>();

	static {
		codes.put("NOAUTH", "商户无此接口权限");
		codes.put("NOTENOUGH", "余额不足");
		codes.put("NOTENOUGH", "余额不足");
		codes.put("ORDERPAID", "商户订单已支付");
		codes.put("ORDERCLOSED", "订单已关闭");
		codes.put("SYSTEMERROR", "系统错误");
		codes.put("APPID_NOT_EXIST", "APPID不存在");
		codes.put("MCHID_NOT_EXIS", "MCHID不存在");
		codes.put("APPID_MCHID_NOT_MATCH", "appid和mch_id不匹配");
		codes.put("LACK_PARAMS", "少参数");
		codes.put("OUT_TRADE_NO_USED", "商户订单号重复");
		codes.put("SIGNERROR", "签名错误");
		codes.put("XML_FORMAT_ERROR", "XML格式错误");
		codes.put("REQUIRE_POST_METHOD", "请使用post方法");
		codes.put("POST_DATA_EMPTY", "post数据为空");
		codes.put("NOT_UTF8", "编码格式错误");
	}

	public WeixinPayment(PaymentProvider provider) {
		super("weixin_payment_seq", provider);
	}

	@Override
	public PaymentOrder request(String paymentType, int paymentFee,
                                String clientIp, Map<String, ?> req, String notifyUrl) throws PaymentException {

		PaymentOrder result = super.request(paymentType, paymentFee, clientIp, req, notifyUrl);

		String nonce_str = Md5Util.digest("" + System.currentTimeMillis());
		Map<String, Object> request = new HashMap<>();
		request.put("appid", APPID);
		request.put("mch_id", MCH_ID);
		request.put("nonce_str", nonce_str);
		request.put("body", req.get("purchase_Name"));
		request.put("out_trade_no", result.getOrderNumber());
		request.put("total_fee", paymentFee);
		request.put("spbill_create_ip", clientIp);
		request.put("notify_url", this.getNotifyUrl(result));
		request.put("trade_type", "APP");
        String serializationRequest = Md5Util.concat(request, KEY);
		String sign = Md5Util.digest(serializationRequest, Env.getCharset()).toUpperCase();
		request.put("sign", sign);
		String content = XmlUtil.map2xml(request);
		String responseString;
		try {
			responseString = HttpUtil.post(ORDER_URL, content);
		} catch (IOException e) {
			logger.error("Payment was error. the internet was error, exception = {} ", e);
			throw new PaymentException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "创建微信订单时出现网络错误", e);
		}
		Map<String, String> response = XmlUtil.xml2map(responseString);
		String returnCode = response.get("return_code");
		String returnMsg = response.get("return_msg");
		if (!RETURN_CODE_SUCCESS.equals(returnCode)) {
		    logger.error("create wxpay was error. the wxpay service is down");
			throw new PaymentException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
					"微信支付失败:" + returnCode + "," + returnMsg);
		}
		String paymentCode = response.get("prepay_id");
		result.setPaymentCode(paymentCode);
		sign = response.remove("sign");
		String sign2 = Md5Util.digest(Md5Util.concat(response, KEY)).toUpperCase();
		if (!sign2.equals(sign)) {
			logger.error("wxpay was error. the sign is not mime");
			throw new PaymentException(ErrorCode.SIGN_ERROE, "微信数据签名错误");
		}

		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		Map<String, Object> res = new HashMap<>();
		res.put("appid", APPID);
        res.put("partnerid", MCH_ID);
        res.put("prepayid", paymentCode);
        res.put("package", "Sign=WXPay");
		res.put("noncestr", nonce_str);
		res.put("timestamp", timestamp);
		String resSign = Md5Util.digest(Md5Util.concat(res, KEY), Env.getCharset()).toUpperCase();
		Map<String, String> conf = new HashMap<>();
		conf.put("sign", resSign);
		conf.put("body", serializationRequest);
		conf.put("detail", content);
		conf.put("nonce_str", nonce_str);
		conf.put("timestamp", timestamp);
		conf.put("prepay_id", paymentCode);
		result.setConf(conf);
		return result;
	}

    @Override
    public PaymentOrder reRequest(String paymentType, int fee, String clientIp, Map<String, ?> req, String notifyUrl) throws PaymentException {
	    long paymentCodeSeq = createPaymentSeq();
	    String orderNumber = (String) req.get("order_number");
        PaymentOrder result = new PaymentOrder(orderNumber, createPaymentCode(paymentCodeSeq, fee), fee, paymentCodeSeq, new Date(), notifyUrl);
        String nonce_str = Md5Util.digest("" + System.currentTimeMillis());
        Map<String, Object> request = new HashMap<>();
        request.put("appid", APPID);
        request.put("mch_id", MCH_ID);
        request.put("nonce_str", nonce_str);
        request.put("body", req.get("purchase_Name"));
        request.put("out_trade_no", orderNumber);
        request.put("total_fee", fee);
        request.put("spbill_create_ip", clientIp);
        request.put("notify_url", UrlUtil.getUrl(notifyUrl, StringUtil.NULL_STRING));
        request.put("trade_type", "APP");
        String serializationRequest = Md5Util.concat(request, KEY);
        String sign = Md5Util.digest(serializationRequest, Env.getCharset()).toUpperCase();
        request.put("sign", sign);
        String content = XmlUtil.map2xml(request);
        String responseString;
        try {
            responseString = HttpUtil.post(ORDER_URL, content);
        } catch (IOException e) {
            logger.error("Payment was error. the internet was error, exception = {} ", e);
            throw new PaymentException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "创建微信订单时出现网络错误", e);
        }
        Map<String, String> response = XmlUtil.xml2map(responseString);
        String returnCode = response.get("return_code");
        String returnMsg = response.get("return_msg");
        if (!RETURN_CODE_SUCCESS.equals(returnCode)) {
            logger.error("create wxpay was error. the wxpay service is down");
            throw new PaymentException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
                    "微信支付失败:" + returnCode + "," + returnMsg);
        }
        String paymentCode = response.get("prepay_id");
        result.setPaymentCode(paymentCode);
        sign = response.remove("sign");
        String sign2 = Md5Util.digest(Md5Util.concat(response, KEY)).toUpperCase();
        if (!sign2.equals(sign)) {
            logger.error("wxpay was error. the sign is not mime");
            throw new PaymentException(ErrorCode.SIGN_ERROE, "微信数据签名错误");
        }

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        Map<String, Object> res = new HashMap<>();
        res.put("appid", APPID);
        res.put("partnerid", MCH_ID);
        res.put("prepayid", paymentCode);
        res.put("package", "Sign=WXPay");
        res.put("noncestr", nonce_str);
        res.put("timestamp", timestamp);
        String resSign = Md5Util.digest(Md5Util.concat(res, KEY), Env.getCharset()).toUpperCase();
        Map<String, String> conf = new HashMap<>();
        conf.put("sign", resSign);
        conf.put("body", serializationRequest);
        conf.put("detail", content);
        conf.put("nonce_str", nonce_str);
        conf.put("timestamp", timestamp);
        conf.put("prepay_id", paymentCode);
        result.setConf(conf);
        return result;
    }

    @Override
    public PaymentOrder reRequest(String paymentType, int fee, String clientIp, Map<String, ?> req) throws PaymentException {
        return null;
    }

    public static final String resSign(Map<?, ?> maps) {
	    return Md5Util.digest(Md5Util.concat(maps, KEY), Env.getCharset()).toUpperCase();
    }

	@Override
	protected boolean verifyData(Map<String, ?> resp) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.putAll(resp);
		String sign = (String) data.remove("sign");
		String sign2 = Md5Util.sign(data, KEY).toUpperCase();

		return sign2.equals(sign);
	}

	@Override
	public Map<String, Object> getPaymentConfig(String paymentType, int fee, Map<String, ?> req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PaymentStatus getPaymentStatus(Object status) {
		return RETURN_CODE_SUCCESS.equals(status) ? PaymentStatus.PaySuccess : PaymentStatus.PayFailed;
	}

	@Override
	protected PaymentResponse fetchResponse(Map<String, ?> resp) throws PaymentException {
		Parameter p = new Parameter(resp);
		String returnCode;
		try {
			returnCode = p.s("return_code");
			String returnMsg = p.s("return_msg");

			if (!RETURN_CODE_SUCCESS.equals(returnCode)) {
				throw new PaymentException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
						"微信支付失败:" + returnCode + "," + returnMsg);
			}

			String orderNumber = p.s("out_trade_no");
			String paymentOrderNumber = p.s("transaction_id");

			String resultCode = p.s("result_code");

			String errCode = p.s("err_code", null);
			String errDesc = p.s("err_code_des", null);

			int paymentFee = p.i("total_fee");

			PaymentStatus paymentStatus = getPaymentStatus(resultCode);

			PaymentResponse response = new PaymentResponse(orderNumber,
					paymentOrderNumber, paymentStatus, paymentFee, errCode,
					errDesc);

			return response;
		} catch (ApiException e) {
			throw new PaymentException(e);
		}

	}

	@Override
	public NotifyContent getNotifyContent(PaymentResponse paymentResult) {
		// TODO Auto-generated method stub
		return null;
	}

}

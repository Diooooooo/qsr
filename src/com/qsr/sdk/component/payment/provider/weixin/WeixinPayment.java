package com.qsr.sdk.component.payment.provider.weixin;

import com.qsr.sdk.component.payment.*;
import com.qsr.sdk.component.payment.provider.AbstractPayment;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.exception.PaymentException;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.HttpUtil;
import com.qsr.sdk.util.Md5Util;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WeixinPayment extends AbstractPayment {

	final static Logger logger = LoggerFactory.getLogger(WeixinPayment.class);

	public static final int PROVIDER_ID = 11;

	/** 公众账号ID */
	private static final String appid = "appid";
	/** 商户号 */
	private static final String mch_id = "1226362502";

	private static final String key = "key=1111";

	/** 统一下单 */
	private static final String order_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	private static final String return_code_success = "SUCCESS";

	private static final String return_code_fail = "FAIL";

	// private static final String
	// order_url="https://api.mch.weixin.qq.com/pay/unifiedorder";
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

		PaymentOrder result = super.request(paymentType, paymentFee, clientIp,
				req, notifyUrl);

		Map<String, Object> request = new HashMap<String, Object>();
		request.put("appid", appid);
		request.put("mch_id", mch_id);
		request.put("nonce_str",
				Md5Util.digest("" + System.currentTimeMillis()));
		// 商品描述
		request.put("body", req.get("purchase_Name"));
		// 商品详情
		// request.put("detail", "");
		// 附加数据
		// request.put("attach", "");
		// 商户订单号
		request.put("out_trade_no", result.getOrderNumber());

		// 货币类型
		// request.put("fee_type", "CNY");

		// 总金额
		request.put("total_fee", paymentFee);

		// 终端IP
		request.put("spbill_create_ip", clientIp);

//		request.put("notify_url", this.getNotifyUrl());
		request.put("notify_url", this.getNotifyUrl(result));
//		request.put("notify_url", result.getNotifyUrl()+ File.separator + this.getProvider());
		// 交易类型
		request.put("trade_type", "APP");

		// 商品ID
		request.put("product_id", 1);

		String sign = Md5Util.sign(request, key).toUpperCase();

		request.put("sign", sign);

		String content = map2xml(request);
		String responseString;
		try {

			responseString = HttpUtil.post(order_url, content);

		} catch (IOException e) {
			throw new PaymentException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
					"创建微信订单时出现网络错误", e);
		}
		Map<String, String> response = xml2map(responseString);

		String returnCode = response.get("return_code");
		String returnMsg = response.get("return_msg");

		if (!return_code_success.equals(returnCode)) {
			throw new PaymentException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
					"微信支付失败:" + returnCode + "," + returnMsg);
		}
		String paymentCode = response.get("prepay_id");
		result.setPaymentCode(paymentCode);
		sign = response.remove("sign");
		String sign2 = Md5Util.sign(response, key).toUpperCase();
		if (!sign2.equals(sign)) {
			throw new PaymentException(ErrorCode.SIGN_ERROE, "微信数据签名错误");
		}

		return result;
	}

	private Map<String, String> xml2map(String xmlContent)
			throws PaymentException {
		Map<String, String> result = new HashMap<String, String>();

		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new StringReader(xmlContent));
			Element root = doc.getRootElement();
			for (Iterator<?> i = root.elementIterator(); i.hasNext();) {
				Element el = (Element) i.next();
				String name = el.getName();

				String text = el.getTextTrim();
				result.put(name, text);
			}
			return result;
		} catch (DocumentException e) {
			throw new PaymentException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
					"解析数据错误", e);
		}
	}

	private String map2xml(Map<String, ?> input) throws PaymentException {
		Document doc = DocumentHelper.createDocument();
		Element rootElement = DocumentHelper.createElement("xml");

		for (Map.Entry<String, ?> entry : input.entrySet()) {
			Element elm = rootElement.addElement(entry.getKey());
			if (entry.getValue() != null) {
				elm.setText(entry.getValue().toString());
			}
		}
		doc.setRootElement(rootElement);
		StringWriter sw = new StringWriter();
		XMLWriter xmlWriter = new XMLWriter(sw);
		try {
			xmlWriter.write(doc);

		} catch (IOException e) {
			throw new PaymentException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,
					"生成数据错误", e);
		}

		return sw.getBuffer().toString();

	}

	@Override
	protected boolean verifyData(Map<String, ?> resp) throws PaymentException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.putAll(resp);
		String sign = (String) data.remove("sign");
		String sign2 = Md5Util.sign(data, key).toUpperCase();

		return sign2.equals(sign);
	}

	@Override
	public Map<String, Object> getPaymentConfig(String paymentType, int fee,
			Map<String, ?> req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PaymentStatus getPaymentStatus(Object status) {
		return return_code_success.equals(status) ? PaymentStatus.PaySuccess
				: PaymentStatus.PayFailed;
	}

	@Override
	protected PaymentResponse fetchResponse(Map<String, ?> resp)
			throws PaymentException {

		Parameter p = new Parameter(resp);
		String returnCode;
		try {
			returnCode = p.s("return_code");
			String returnMsg = p.s("return_msg");

			if (!return_code_success.equals(returnCode)) {
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

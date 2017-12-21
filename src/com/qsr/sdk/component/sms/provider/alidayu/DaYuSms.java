package com.qsr.sdk.component.sms.provider.alidayu;

import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.sms.SendResult;
import com.qsr.sdk.component.sms.SmsSend;
import com.qsr.sdk.component.sms.VerifyResult;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.HttpUtil2;
import com.qsr.sdk.util.JsonUtil;
import com.qsr.sdk.util.ParameterUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yuan on 2016/1/26.
 */
public class DaYuSms implements SmsSend {
    private final Provider provider;
    private final Map<Object,Object> config=new HashMap<>();

    public DaYuSms(Provider provider, Map<?, ?> config) {
        this.provider = provider;
        this.config.put("template_code_1","SMS_4896067");
        this.config.put("template_sign_name_1","活动验证");

        this.config.put("template_code_2","SMS_6335432");
        this.config.put("template_sign_name_2","天行远景");

        this.config.put("template_code_3","SMS_6360544");
        this.config.put("template_sign_name_3","天行远景");

        this.config.put("template_code_4", "SMS_44410176");
        this.config.put("template_sign_name_4", "天行远景");

        this.config.put("template_code_5", "SMS_45880001");
        this.config.put("template_sign_name_5", "天行远景");

        this.config.put("template_code_6", "SMS_46735120");
        this.config.put("template_sign_name_6", "天行远景");

        this.config.put("app_key","23304533");
        this.config.put("app_secret","a19c2da7b97a886f42f1fa695adaa8ae");
        this.config.putAll(config);

    }



    @Override
    public SendResult send(String phoneNumber, String template, Map<String, String> templateParams) throws ApiException {
        Map<String,String> headers=new LinkedHashMap<>();
        Map<String,String> urlParams=new LinkedHashMap<>();

        headers.put("X-Ca-Key", (String) config.get("app_key"));
        headers.put("X-Ca-Secret", (String) config.get("app_secret"));

        if(phoneNumber.length()>11){
            phoneNumber=phoneNumber.substring(phoneNumber.length()-11);
        }
        String templateCodeKey="template_code_"+template;
        if(!config.containsKey(templateCodeKey)){
            throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,"缺少模版["+templateCodeKey+"] 的配置信息");
        }
        String templateCodeSignName="template_sign_name_"+template;

        if(!config.containsKey(templateCodeKey)){
            throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,"缺少模版["+templateCodeSignName+"] 的配置信息");
        }


        urlParams.put("rec_num",phoneNumber);
        urlParams.put("sms_template_code", (String) config.get(templateCodeKey));
        urlParams.put("sms_free_sign_name",(String) config.get(templateCodeSignName));

        urlParams.put("sms_type","normal");
//        urlParams.put("extend",);
        Map<String, String> fields = new LinkedHashMap<>();

        fields.put("sms_param", JsonUtil.toJson(templateParams));
        try {
            String s= HttpUtil2.postForm("https://ca.aliyuncs.com/gw/alidayu/sendSms", urlParams, headers,fields);
            Map<String, Object> jsonMap = JsonUtil.fromJsonToMap(s);
            String code = ParameterUtil.s(jsonMap.get("code"), null);
            String model = ParameterUtil.s(jsonMap.get("model"), null);
            boolean success = ParameterUtil.b(jsonMap.get("success"));
            String msg = ParameterUtil.s(jsonMap.get("msg"), null);
            Map<String, String> attributes = new HashMap<>();
            attributes.put("model",model);
            return new SendResult(success,code,msg,attributes);

        } catch (Exception e) {
            throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,"短信发送失败",e);
        }
    }

    @Override
    public VerifyResult verify(String phoneNumber, String verifyCode) {
        return null;
    }
    @Override
    public Provider getProvider() {
        return provider;
    }
}

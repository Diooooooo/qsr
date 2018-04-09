package com.qsr.sdk.component.sms.provider.alidayu;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.gson.JsonObject;
import com.qsr.sdk.component.Provider;
import com.qsr.sdk.component.sms.SendResult;
import com.qsr.sdk.component.sms.SmsSend;
import com.qsr.sdk.component.sms.VerifyResult;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.util.ErrorCode;
import com.qsr.sdk.util.MapUtil;
import net.sf.json.JSONObject;
import org.codehaus.jackson.map.util.JSONPObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuan on 2016/1/26.
 */
public class DaYuSms implements SmsSend {
    private final Provider provider;
    private final Map<Object,Object> config=new HashMap<>();

    public DaYuSms(Provider provider, Map<?, ?> config) {
        this.provider = provider;
        this.config.put("template_code_1","SMS_123668153");
        this.config.put("template_sign_name_1", "量球匠");

        this.config.put("template_code_2", "SMS_125029719");
        this.config.put("template_sign_name_2","量球匠");

        this.config.put("app_key","LTAIsMJIfy0E1jfH");
        this.config.put("app_secret","qr5Lfhup2XVOctAivpugQAuiw9h9jG");

        this.config.put("product", "Dysmsapi");
        this.config.put("domain", "dysmsapi.aliyuncs.com");
        this.config.putAll(config);

    }



    @Override
    public SendResult send(String phoneNumber, String template, Map<String, String> templateParams) throws ApiException {
//        Map<String,String> headers=new LinkedHashMap<>();
//        Map<String,String> urlParams=new LinkedHashMap<>();
//
//        headers.put("X-Ca-Key", (String) config.get("app_key"));
//        headers.put("X-Ca-Secret", (String) config.get("app_secret"));
//
//        if(phoneNumber.length()>11){
//            phoneNumber=phoneNumber.substring(phoneNumber.length()-11);
//        }
//
//
//        urlParams.put("rec_num",phoneNumber);
//        urlParams.put("sms_template_code", (String) config.get(templateCodeKey));
//        urlParams.put("sms_free_sign_name",(String) config.get(templateCodeSignName));
//
//        urlParams.put("sms_type","normal");
////        urlParams.put("extend",);
//        Map<String, String> fields = new LinkedHashMap<>();
//
//        fields.put("sms_param", JsonUtil.toJson(templateParams));
//        try {
//            String s= HttpUtil2.postForm("https://ca.aliyuncs.com/gw/alidayu/sendSms", urlParams, headers,fields);
//            Map<String, Object> jsonMap = JsonUtil.fromJsonToMap(s);
//            String code = ParameterUtil.s(jsonMap.get("code"), null);
//            String model = ParameterUtil.s(jsonMap.get("model"), null);
//            boolean success = ParameterUtil.b(jsonMap.get("success"));
//            String msg = ParameterUtil.s(jsonMap.get("msg"), null);
//            Map<String, String> attributes = new HashMap<>();
//            attributes.put("model",model);
//            return new SendResult(success,code,msg,attributes);
//
//        } catch (Exception e) {
//            throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,"短信发送失败",e);
//        }
        try {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", (String) config.get("app_key"), (String) config.get("app_secret"));
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", (String) config.get("product"), (String) config.get("domain"));
            IAcsClient acsClient = new DefaultAcsClient(profile);
            String templateCodeKey="template_code_"+template;
            if(!config.containsKey(templateCodeKey)){
                throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,"缺少模版["+templateCodeKey+"] 的配置信息");
            }
            String templateCodeSignName="template_sign_name_"+template;

            if(!config.containsKey(templateCodeKey)){
                throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN,"缺少模版["+templateCodeSignName+"] 的配置信息");
            }

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers(phoneNumber);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName((String) config.get(templateCodeSignName));
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode((String) config.get(templateCodeKey));
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
//            request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            String expand = templateParams.remove("expand");
            if (null != expand)
                request.setOutId(expand);
            Map<String, String> t = new HashMap<>();
            t.put("code", templateParams.get("code"));
            request.setTemplateParam(buildTemplateParam(t));

            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");

            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            return new SendResult("OK".equalsIgnoreCase(sendSmsResponse.getCode()), sendSmsResponse.getCode(), sendSmsResponse.getMessage(), null);
        } catch (ClientException e) {
            throw new ApiException(ErrorCode.THIRD_SERVICE_EXCEPTIOIN, "短信服务不可用", e);
        }
    }

    private String buildTemplateParam(Map<String, String> templateParam) {
        List<String> keys = new ArrayList<>();
        for (String s: templateParam.keySet()) {
            keys.add(s);
        }
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (String s: keys) {
            sb.append("\""+ s + "\":\"" + templateParam.get(s) + "\",");
        }
        String template = sb.toString().substring(0, sb.toString().length() - 1) + "}";
        return template;
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

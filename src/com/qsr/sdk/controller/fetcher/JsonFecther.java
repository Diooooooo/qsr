package com.qsr.sdk.controller.fetcher;

import com.jfinal.core.Controller;
import com.qsr.sdk.util.JsonUtil;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by fc on 2016/4/26.
 *
 * 此Fetcher是为了解决form为text/html形式的参数获取
 */
public class JsonFecther extends Fetcher {

    private final static int maxLength = 4096;

    public JsonFecther(Controller controller) {
        super(controller);
    }

    @Override
    protected Map<String, Object> buildParameterMap() {
        Map<String, Object> map = null;
        try {
            ServletInputStream servletRequest = this.getController().getRequest().getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] read = new byte[1024];
            int len = -1;
            while ((len = servletRequest.read(read)) != -1) {
                bos.write(read, 0, len);
                if (bos.size() > maxLength) {
                    throw new IllegalArgumentException("the request body is to long. please input validity request body. at:" + getController().getRequest().getRequestURI());
                }
            }
            bos.close();
            if (null != bos.toByteArray()) {
                map = JsonUtil.fromJsonToMap(new String(bos.toByteArray()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("获取网络数据异常, at:"  + getController().getRequest().getRequestURI(), e);
        }
        return map;
    }
}

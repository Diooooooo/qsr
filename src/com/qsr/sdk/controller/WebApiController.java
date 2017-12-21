package com.qsr.sdk.controller;

import com.jfinal.core.Controller;
import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.controller.fetcher.MultipartFetcher;
import com.qsr.sdk.controller.fetcher.UrlEncodeFetcher;
import com.qsr.sdk.controller.render.DataRender;
import com.qsr.sdk.exception.ApiException;
import com.qsr.sdk.service.Service;
import com.qsr.sdk.service.ServiceManager;
import com.qsr.sdk.util.Env;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class WebApiController extends Controller {

    private Map<String, String> headers = null;
    private Fetcher fetcher;
    private Logger logger;

    public WebApiController(Logger logger) {
        this.logger = logger;
    }

    protected Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new LinkedHashMap<>();
            HttpServletRequest httpServletRequest = this.getRequest();
            Enumeration<String> names1 = httpServletRequest.getHeaderNames();

            while (names1.hasMoreElements()) {
                String name = names1.nextElement();
                headers.put(name, httpServletRequest.getHeader(name));
            }
        }
        return headers;
    }

    public String getRemoteAddr() {
        HttpServletRequest httpServletRequest = this.getRequest();
        return httpServletRequest.getRemoteAddr();
    }

    protected String getRealRemoteAddr() {

        String remoteAddr = null;
        for (String i : Env.getRealIpHeaders()) {

            String header = this.getHeader(i);
            if (!StringUtil.isEmptyOrNull(header)) {
                remoteAddr = header;
                break;
            }
        }

        if (remoteAddr == null) {
            remoteAddr = getRemoteAddr();

        }
        if (remoteAddr == null) {
            remoteAddr = "0.0.0.0";
        }
        return remoteAddr;

    }

    protected String getHeader(String name) {
        return this.getRequest().getHeader(name);
    }

    protected String getUserAgent() {
        return getHeader("user-agent");
    }

    public void renderData() {
        this.renderData(null, null);
    }

    public void renderData(String message) {
        this.renderData(null, message);
    }
    public void renderData(Object object) {
        this.renderData(object, null);
    }

    public void renderData(Object object, String statusMessage) {
        this.render(new DataRender(object, statusMessage));
    }

    public Fetcher fetch(Fetcher fetcher) {
        this.fetcher = fetcher;
        fetcher.fetch();
        return this.fetcher;
    }

    public Fetcher fetch() {
        HttpServletRequest request = this.getRequest();
        Fetcher fetcher;
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("multipart/form-data")) {
            fetcher = new MultipartFetcher(this, Env.getFileUploadDir(), Env.getFileUploadSize());
        } else {
            fetcher = new UrlEncodeFetcher(this);
        }
        return fetch(fetcher);
    }

    protected void renderException(String message, Throwable t) {
        if (logger != null) {
            Map<?, ?> parameterMap = null;

            if (this.fetcher != null) {
                parameterMap = this.fetcher.getParameterMap();
            }

            if (t instanceof IllegalArgumentException
                    || t instanceof ApiException) {
                logger.warn("{},params={},exception={}", message, parameterMap,
                        t.getMessage());
            } else if (t instanceof Throwable) {
                message = message + ",params=" + parameterMap;
                logger.error(message, t);
            }
        }
        renderData(t, null);
    }

    protected <T extends Service> T getService(Class<T> clazz) {
        return ServiceManager.getService(clazz);
    }
}

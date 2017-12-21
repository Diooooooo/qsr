package com.qsr.sdk.controller;

import com.jfinal.render.JsonRender;
import com.qsr.sdk.controller.fetcher.Fetcher;
import com.qsr.sdk.controller.render.OAuth2Render;
import com.qsr.sdk.service.OAuth2Service;
import com.qsr.sdk.service.UserService;
import com.qsr.sdk.service.exception.OAuth2ServiceException;
import com.qsr.sdk.util.HttpUtil;
import com.qsr.sdk.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class OAuth2Controller extends WebApiController {

    final static Logger logger = LoggerFactory
            .getLogger(OAuth2Controller.class);

    public OAuth2Controller() {
        super(logger);
    }

    private static Map<String, Object> getExceptionData(
            IllegalArgumentException e) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("error", "invalid_request");
        data.put("error_description", e.getMessage());
        return data;

    }

    protected void render(String url, Map<?, ?> params, boolean json,
                          boolean fragment) {
        this.render(new OAuth2Render(url, params, json, fragment));
    }

    protected void render(OAuth2ServiceException e) {
        this.render(new OAuth2Render(e));
    }

    //	public void requestAuth() {
    //		try {
    //
    //			Fetcher f = this.fetch();
    //			String responseType = f.s("response_type");
    //			String clientId = f.s("client_id");
    //			String redirectUri = f.s("redirect_uri");
    //
    //			String state = f.s("state", null);
    //			String scope = f.s("scope", null);
    //
    //
    //
    //
    //			OAuth2Service oauth2Service = this.getComponent(OAuth2Service.class);
    //
    //			Map<String, Object> data = oauth2Service.authorizationRequest(
    //					responseType, clientId, redirectUri, state, scope);
    //
    //			this.renderJson(data);
    //		} catch (OAuth2ServiceException ex) {
    //			this.renderError(400, new JsonRender(ex.getParameters()));
    //
    //		} catch (IllegalArgumentException ex) {
    //			this.renderError(400, new JsonRender(getExceptionData(ex)));
    //		} catch (Throwable t) {
    //			this.renderError(500);
    //		}
    //	}

    public void requestCode() {
        String redirectUri = null;
        Fetcher f = this.fetch();
        boolean json = f.i("_json", 0) > 0;
        boolean fragment = f.i("_fragment", 0) > 0;
        try {
            logger.debug("requestCode,params={}", f);
            //result.put("_fragment", true);
            redirectUri = f.s("redirect_uri");
            String responseType = f.s("response_type");
            String clientId = f.s("client_id");

            String state = f.s("state", null);
            String scope = f.s("scope", null);

            UserService userService = this.getService(UserService.class);

            String resourcekey = f.s("resourcekey", null);

            if (StringUtil.isEmpty(resourcekey)) {
                String sessionkey = f.s("sessionkey");
                int userId = userService.getUserIdBySessionKey(sessionkey);
                resourcekey = "" + userId;
            }

            String approvalPrompt = f.s("approval_prompt", null);
            String accessType = f.s("access_type", null);
            String clientIp = this.getRemoteAddr();

//			int openUserId = openUserService
//					.getOpenUserIdBySessionkey(sessionkey);

            OAuth2Service oauth2Service = this.getService(OAuth2Service.class);

            Map<String, Object> data = oauth2Service.authorizationRequest(
                    resourcekey, responseType, clientId, redirectUri, state,
                    scope, approvalPrompt, accessType, clientIp);
            redirectUri = (String) data.remove("redirect_uri");

            this.render(redirectUri, data, json, fragment);
            //this.renderJson(data);
        } catch (OAuth2ServiceException ex) {

            logger.warn("requestCode {},params={}", ex.getMessage(),
                    f.getParameterMap());
            this.render(redirectUri, ex.getParameters(), json, fragment);

        } catch (IllegalArgumentException ex) {
            logger.warn("requestCode {},params={}", ex.getMessage(),
                    f.getParameterMap());
            this.render(redirectUri, getExceptionData(ex), json, fragment);
        } catch (Throwable t) {
            logger.error("requestCode", t);
            this.renderError(500);
        }
    }

    public void callbackCode() {
        try {
            Fetcher f = this.fetch();
            logger.debug("callbackCode,params={}", f);
            //String accessCode = f.s("access_code");
            //			String clientId = f.s("client_id");

            //			OAuth2Service oauth2Service = this.getComponent(OAuth2Service.class);
            //			String authorization = this.getHeader("Authorization");
            //
            //			Map<String, Object> data = oauth2Service.accessTokenRequest(
            //					grantType, authorization, f.getParameterMap());
            Map<String, Object> data = new HashMap<String, Object>();
            //data.put("access_token", "aaaa");
            //this.renderJson(data);
            //this.renderText(f.s("code"));

            data.put("code", f.s("code"));

            String url = "http://127.0.0.1:8080/api/oauth2/requestToken";

            data.put("grant_type", "authorization_code");
            data.put("redirect_uri", url);
            data.put("client_id", "6bee1fe29a685d67b8e4af3d48479a95");
            data.put("client_secret", "6654643469d9442f1ad5ba1713d422ca");

            this.redirect(HttpUtil.urlencoded(url, data));

        } catch (IllegalArgumentException ex) {
            this.renderError(400, new JsonRender(getExceptionData(ex)));
        } catch (Throwable t) {
            this.renderError(500);
        }
    }

    public void requestToken() {

        Fetcher f = this.fetch();
        try {
            logger.debug("requestToken,params={}", f);
            OAuth2Service oauth2Service = this.getService(OAuth2Service.class);
            String grantType = f.s("grant_type");
            //String clientId = f.s("client_id");

            String authorization = this.getHeader("Authorization");

            Map<String, Object> data = oauth2Service.accessTokenRequest(
                    grantType, authorization, f.getParameterMap());

            this.renderJson(data);

        } catch (OAuth2ServiceException ex) {
            logger.warn("requestToken {},params={}", ex.getMessage(),
                    f.getParameterMap());
            this.render(ex);
        } catch (IllegalArgumentException ex1) {
            logger.warn("requestToken {},params={}", ex1.getMessage(),
                    f.getParameterMap());
            OAuth2ServiceException ex = new OAuth2ServiceException(
                    "invalid_request", ex1);
            this.render(ex);
        } catch (Throwable t) {
            logger.error("requestToken", t);
            this.renderError(500);
        }
    }

    //
    //	public void refreshToken() {
    //		this.grantReqLinkToOAuthGrantRequest();
    //	}
}

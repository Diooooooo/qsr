package com.qsr.sdk.service;

import com.jfinal.plugin.activerecord.Db;
import com.qsr.sdk.lang.Parameter;
import com.qsr.sdk.service.exception.OAuth2ServiceException;
import com.qsr.sdk.service.serviceproxy.annotation.CacheAdd;
import com.qsr.sdk.util.OAuthUtils;
import com.qsr.sdk.util.ParameterUtil;
import com.qsr.sdk.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class OAuth2Service extends Service {

	final static Logger logger = LoggerFactory.getLogger(OAuth2Service.class);

	//error response params
	public static final String OAUTH_ERROR = "error";
	public static final String OAUTH_ERROR_DESCRIPTION = "error_description";
	public static final String OAUTH_ERROR_URI = "error_uri";

	public static final class CodeResponse {
		/**
		 * The request is missing a required parameter, includes an unsupported
		 * parameter value, or is otherwise malformed.
		 */
		public static final String INVALID_REQUEST = "invalid_request";

		/**
		 * The client is not authorized to request an authorization code using
		 * this method.
		 */
		public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";

		/**
		 * The resource owner or authorization server denied the request.
		 */
		public static final String ACCESS_DENIED = "access_denied";

		/**
		 * The authorization server does not support obtaining an authorization
		 * code using this method.
		 */
		public static final String UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";

		/**
		 * The requested scope is invalid, unknown, or malformed.
		 */
		public static final String INVALID_SCOPE = "invalid_scope";

		/**
		 * The authorization server encountered an unexpected condition which
		 * prevented it from fulfilling the request.
		 */
		public static final String SERVER_ERROR = "server_error";

		/**
		 * The authorization server is currently unable to handle the request
		 * due to a temporary overloading or maintenance of the server.
		 */
		public static final String TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";

	}

	public static final class TokenResponse {
		/**
		 * The request is missing a required parameter, includes an unsupported
		 * parameter value, repeats a parameter, includes multiple credentials,
		 * utilizes more than one mechanism for authenticating the client, or is
		 * otherwise malformed.
		 */
		public static final String INVALID_REQUEST = "invalid_request";
		/**
		 * Client authentication failed (e.g. unknown client, no client
		 * authentication included, or unsupported authentication method). The
		 * authorization server MAY return an HTTP 401 (Unauthorized) status
		 * code to indicate which HTTP authentication schemes are supported. If
		 * the client attempted to authenticate via the "Authorization" request
		 * header field, the authorization server MUST respond with an HTTP 401
		 * (Unauthorized) status code, and include the "WWW-Authenticate"
		 * response header field matching the authentication scheme used by the
		 * client.
		 */
		public static final String INVALID_CLIENT = "invalid_client";

		/**
		 * The provided authorization grant (e.g. authorization code, resource
		 * owner credentials, client credentials) is invalid, expired, revoked,
		 * does not match the redirection URI used in the authorization request,
		 * or was issued to another client.
		 */
		public static final String INVALID_GRANT = "invalid_grant";

		/**
		 * The authenticated client is not authorized to use this authorization
		 * grant type.
		 */
		public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";

		/**
		 * The authorization grant type is not supported by the authorization
		 * server.
		 */
		public static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";

		/**
		 * The requested scope is invalid, unknown, malformed, or exceeds the
		 * scope granted by the resource owner.
		 */
		public static final String INVALID_SCOPE = "invalid_scope";
	}

	public static final class ResourceResponse {
		/**
		 * The request is missing a required parameter, includes an unsupported
		 * parameter value, repeats a parameter, includes multiple credentials,
		 * utilizes more than one mechanism for authenticating the client, or is
		 * otherwise malformed.
		 */
		public static final String INVALID_REQUEST = "invalid_request";

		public static final String EXPIRED_TOKEN = "expired_token";

		/**
		 * The request requires higher privileges than provided by the access
		 * token.
		 */
		public static final String INSUFFICIENT_SCOPE = "insufficient_scope";

		/**
		 * The access token provided is expired, revoked, malformed, or invalid
		 * for other reasons.
		 */
		public static final String INVALID_TOKEN = "invalid_token";
	}

	public static final class HttpMethod {
		public static final String POST = "POST";
		public static final String GET = "GET";
		public static final String DELETE = "DELETE";
		public static final String PUT = "PUT";
	}

	public static final class HeaderType {
		public static final String CONTENT_TYPE = "Content-Type";
		public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
		public static final String AUTHORIZATION = "Authorization";
	}

	public static final class WWWAuthHeader {
		public static final String REALM = "realm";
	}

	public static final class ContentType {
		public static final String URL_ENCODED = "application/x-www-form-urlencoded";
		public static final String JSON = "application/json";
	}

	public static final String OAUTH_RESPONSE_TYPE = "response_type";
	public static final String OAUTH_CLIENT_ID = "client_id";
	public static final String OAUTH_CLIENT_SECRET = "client_secret";
	public static final String OAUTH_REDIRECT_URI = "redirect_uri";
	public static final String OAUTH_USERNAME = "username";
	public static final String OAUTH_PASSWORD = "password";
	public static final String OAUTH_ASSERTION_TYPE = "assertion_type";
	public static final String OAUTH_ASSERTION = "assertion";
	public static final String OAUTH_SCOPE = "scope";
	public static final String OAUTH_STATE = "state";
	public static final String OAUTH_GRANT_TYPE = "grant_type";

	public static final String OAUTH_HEADER_NAME = "Bearer";

	//Authorization response params
	public static final String OAUTH_CODE = "code";
	public static final String OAUTH_ACCESS_TOKEN = "access_token";
	public static final String OAUTH_EXPIRES_IN = "expires_in";
	public static final String OAUTH_REFRESH_TOKEN = "refresh_token";

	public static final String OAUTH_TOKEN_TYPE = "token_type";

	public static final String OAUTH_TOKEN = "oauth_token";

	public static final String OAUTH_TOKEN_DRAFT_0 = "access_token";
	public static final String OAUTH_BEARER_TOKEN = "access_token";

	//	public static final ParameterStyle DEFAULT_PARAMETER_STYLE = ParameterStyle.HEADER;
	//	public static final TokenType DEFAULT_TOKEN_TYPE = TokenType.BEARER;

	public static final String OAUTH_VERSION_DIFFER = "oauth_signature_method";

	public static final String ASSERTION = "assertion";

	protected final String RESPONSE_TYPE_CODE = "code";
	protected final String RESPONSE_TYPE_TOKEN = "token";
	//public static final String OAUTH_CODE = "code";
	//public static final String OAUTH_HEADER_NAME = "Bearer";

	protected final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";//授权码模式（authorization code）
	protected final String GRANT_TYPE_PASSWORD = "password";//密码模式（resource owner password credentials）
															//简化模式（implicit）
	protected final String GRANT_TYPE_REFRESH = "refresh_token";
	protected final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";//客户端模式（client credentials）

	//	enum ResponseType {
	//		CODE, TAKEN;
	//		ResponseType() {
	//		}
	//	}

	//	public void validateRequest(OAuthRequest request) {
	//		String redirectUri = request.getRedirectURI();
	//		String clientId = request.getClientId();
	//		//String state=request.getParam("state")
	//		//String responseType = request.getParam("response_type");
	//		Set<String> scopes = request.getScopes();
	//	}

	@CacheAdd
	protected Map<String, Object> getClient(String clientkey,
			String redirectUri, String secretKey) throws OAuth2ServiceException {

		String sql = "select client_id, clientkey,secretkey,redirect_uri,expires_in from razor_oauth2_client  where enabled=1 and clientkey=?";
		Map<String, Object> result = record2map(Db.findFirst(sql, clientkey));
		if (result == null) {
			OAuth2ServiceException e = new OAuth2ServiceException(
					this.getServiceName(), 401, "unauthorized_client",
					"client not exist");

			e.addHeader("WWW-Authenticate",
					OAuthUtils.encodeOAuthHeader(e.getParameters()));
			throw e;

		}
		if (secretKey != null && !secretKey.equals(result.get("secretkey"))) {
			OAuth2ServiceException e = new OAuth2ServiceException(
					this.getServiceName(), 401, "unauthorized_client",
					"secretkey isn't matched");

			e.addHeader("WWW-Authenticate",
					OAuthUtils.encodeOAuthHeader(e.getParameters()));
			throw e;
		}
		if(!redirectUri.startsWith((String)result.get("redirect_uri"))){
			OAuth2ServiceException e = new OAuth2ServiceException(
					this.getServiceName(), 401, "unauthorized_client",
					"redirect_uri is illegal");

			e.addHeader("WWW-Authenticate",
					OAuthUtils.encodeOAuthHeader(e.getParameters()));
			throw e;
		}
		return result;

	}

	@CacheAdd
	public int getResourceType(String scope) throws OAuth2ServiceException {

		String sql = "select resource_type_id from razor_oauth2_resource_type where scope=? ";

		int resourceTypeId = ParameterUtil.i(Db.queryNumber(sql, scope), 0);
		if (resourceTypeId == 0) {
			OAuth2ServiceException ex = new OAuth2ServiceException(
					this.getServiceName(), 401, "invalid_scope",
					"invalid_scope");
			throw ex;
		}
		return resourceTypeId;
	}

	public Map<String, Object> authorizationRequest(String resourcekey,
			String responseType, String clientkey, String redirectUri,
			String state, String scope, String approvalPrompt,
			String accessType, String clientIp) throws OAuth2ServiceException {

		//String approvalPrompt;
		//String accessType;
		String sql0 = "insert razor_oauth2_rawdata_code(resourcekey,response_type,clientkey,redirect_uri,state,scope, approval_prompt,access_type,client_ip) "
				+ "values(?,?,?,?,?,?,?,?,?) ";
		Db.update(sql0, resourcekey, responseType, clientkey, redirectUri,
				state, scope, approvalPrompt, accessType, clientIp);

		Map<String, Object> m = getClient(clientkey, redirectUri, null);

		Parameter client = new Parameter(m);
		//	String newRedirectUri = client.s("redirect_uri");
		//String newRedirectUri = redirectUri;// client.s("redirect_uri");
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		if (state != null) {
			result.put("state", state);
		}
		result.put("redirect_uri", redirectUri);

		int resourceType = getResourceType(scope);
		//int resourceId = resourceOwnerId;
		if (RESPONSE_TYPE_CODE.equals(responseType)) {
			String code = TokenUtil.generate(responseType, clientkey, "code",resourcekey,scope);
			result.put("code", code);
			//result.put("approval_prompt", "force");
			//result.put("access_type", "offline");

			String sql = "insert razor_oauth2_authorization_code(resourcekey,resource_type_id,client_id,authorization_code,expiretime) "
					+ "values (?,?,?,?,now()+interval ? second) ";
			Db.update(sql, resourcekey, resourceType, client.i("client_id"),
					code, client.i("expires_in"));

		} else if (RESPONSE_TYPE_TOKEN.equals(responseType)) {
			//简化模式（implicit）
			String accessToken = TokenUtil.generate(responseType, clientkey,
					"accessToken",resourcekey,scope);
			String refreshToken = "";
			int expiresIn = client.i("expires_in", 3600);

			result.put("access_token", accessToken);
			//	result.put("refresh_token", refreshToken);
			result.put("expires_in", expiresIn);

			String sql = "insert razor_oauth2_access_token(resourcekey,resource_type_id,client_id,access_token,refresh_token,expiretime) "
					+ "values (?,?,?,?,?,now()+interval ? second)";

			Db.update(sql, resourcekey, resourceType, client.i("client_id"),
					accessToken, refreshToken, client.i("expires_in"));

		} else {
			OAuth2ServiceException e = new OAuth2ServiceException(
					this.getServiceName(), 401, "invalid_request",
					"not supported response_code");

			e.addHeader("WWW-Authenticate",
					OAuthUtils.encodeOAuthHeader(e.getParameters()));
			throw e;
		}
		return result;

	}

	public Map<String, Object> accessTokenRequest(String grantType,
			String authorization, Map<?, ?> params)
			throws OAuth2ServiceException {

		Parameter p = new Parameter(params);

		String sql0 = "insert razor_oauth2_rawdata_token(grant_type,authorization,code,clientkey,clientsecret,redirect_uri,username,password,scope,refresh_token)"
				+ "values(?,?,?,?,?,?,?,?,?,?) ";

		Db.update(sql0, grantType, authorization, p.s("code", null),
				p.s("client_id", null), p.s("client_secret", null),
				p.s("redirect_uri", null), p.s("username", null),
				p.s("password", null), p.s("scope", null),
				p.s("refresh_token", null));

		Map<String, Object> result = new LinkedHashMap<String, Object>();
		String[] creds = OAuthUtils
				.decodeClientAuthenticationHeader(authorization);
		String clientkey = null;
		String clientSecret = null;
		if (creds != null) {
			clientkey = creds[0];
			clientSecret = creds[1];
		}

		String accessToken = "";
		String refreshToken = "";
		String redirectUri = null;
		int expiresIn = 3600;

		if (GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType)) {
			//授权码模式（authorization code）
			String code = p.s("code");
			if (!TokenUtil.verifyToken(code)) {
				OAuth2ServiceException ex = new OAuth2ServiceException(
						"invalid_request", "code is illegal");
				throw ex;
			}
			if (clientkey == null) {
				clientkey = p.s("client_id");
			}
			if (clientSecret == null) {
				clientSecret = p.s("client_secret");
			}
			redirectUri = p.s("redirect_uri");

			Parameter client = new Parameter(getClient(clientkey, redirectUri,
					clientSecret));

			int clientId = client.i("client_id");

			String sql = "select code_id,resourcekey,scope,client_id from razor_oauth2_authorization_code "
					+ "where authorization_code=? and expiretime>now() and used=0";
			Parameter p2 = record2param(Db.findFirst(sql, code));
			if (p2 == null) {
				OAuth2ServiceException ex = new OAuth2ServiceException(
						"invalid_request", "code is illegal");
				throw ex;
			}
			int codeId = p2.i("code_id");

			int clientId2 = p2.i("client_id");
			if (clientId2 != clientId) {
				OAuth2ServiceException e = new OAuth2ServiceException(
						this.getServiceName(), 401, "unauthorized_client",
						"client_id isn't matched");

				e.addHeader("WWW-Authenticate",
						OAuthUtils.encodeOAuthHeader(e.getParameters()));
			}

			String resourcekey = p2.s("resourcekey");
			String scope=p2.s("scope","");
			accessToken = TokenUtil.generate(grantType, clientkey, codeId,
					"accessToken",resourcekey,scope);
			refreshToken = TokenUtil.generate(grantType, clientkey, codeId,
					"refreshToken",resourcekey,scope);

			result.put("token_type", "Bearer");
			result.put("access_token", accessToken);
			result.put("refresh_token", refreshToken);
			result.put("expires_in", expiresIn);
			String sql2 = "insert razor_oauth2_access_token(resourcekey,client_id,authorization_code_id,access_token,refresh_token,expiretime) "
					+ "values (?,?,?,?,?,now()+interval ? second)";

			Db.update(sql2, resourcekey, client.i("client_id"), codeId,
					accessToken, refreshToken, client.i("expires_in"));

			String sql3 = "update razor_oauth2_authorization_code set used=1 where code_id=?";
			Db.update(sql3, codeId);

		} else if (GRANT_TYPE_PASSWORD.equals(grantType)) {

			//密码模式（resource owner password credentials）
			if (clientkey == null) {
				clientkey = p.s("client_id");
			}
			if (clientSecret == null) {
				clientSecret = p.s("client_secret");
			}
			Parameter client = new Parameter(getClient(clientkey, null,
					clientSecret));

			int clientId = client.i("client_id");

			String userName = p.s("username");
			String password = p.s("password");
			String scope = p.s("scope", null);
			//不支持的
			OAuth2ServiceException ex = new OAuth2ServiceException(
					this.getServiceName(), 401, "unsupported_grant_type",
					"unsupported_grant_type");
			throw ex;

		} else if (GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType)) {

			if (clientkey == null) {
				clientkey = p.s("client_id");
			}
			if (clientSecret == null) {
				clientSecret = p.s("client_secret");
			}
			redirectUri = p.s("redirect_uri");

			Parameter client = new Parameter(getClient(clientkey, redirectUri,
					clientSecret));

			result.put("access_token", accessToken);
			//result.put("refresh_token", refreshToken);
			result.put("expires_in", expiresIn);
			//不支持的
			OAuth2ServiceException ex = new OAuth2ServiceException(
					this.getServiceName(), 401, "unsupported_grant_type",
					"unsupported_grant_type");
			throw ex;

		} else if (GRANT_TYPE_REFRESH.equals(grantType)) {

			if (clientkey == null) {
				clientkey = p.s("client_id");
			}
			if (clientSecret == null) {
				clientSecret = p.s("client_secret");
			}

			Parameter client = new Parameter(getClient(clientkey, redirectUri,
					clientSecret));

			refreshToken = p.s("refresh_token");

			if (!TokenUtil.verifyToken(refreshToken)) {
				OAuth2ServiceException ex = new OAuth2ServiceException(
						this.getServiceName(), 401, "invalid_token",
						"refresh_token is illegal");
				throw ex;
			}

			expiresIn = 3600 * 24;
			String sql = "select token_id,  from razor_oauth2_access_token where refresh_token=? and refreshed=0 ";

			int tokenId = ParameterUtil.i(Db.queryNumber(sql, refreshToken), 0);
			if (tokenId == 0) {

			}
			accessToken = TokenUtil.generate(grantType, clientkey,
					"accessToken",tokenId);
			result.put("access_token", accessToken);
			result.put("expires_in", expiresIn);
			String sql2 = "update razor_oauth2_access_token "
					+ "set refreshed=1, access_token=? ,expiretime=now()+interval ? second "
					+ "where  token_id=?";
			Db.update(sql2, accessToken, expiresIn, tokenId);
		} else {
			OAuth2ServiceException ex = new OAuth2ServiceException(
					"invalid_grant", "invalid_grant");
			throw ex;
		}
		return result;
	}

	public String getResourcekey(String accessToken) throws OAuth2ServiceException {
		String sql = "select resourcekey from razor_oauth2_access_token a "
				+ "where a.access_token=? and a.expiretime> now() ";
		String resourcekey = Db.queryStr(sql, accessToken);
		if (resourcekey == null) {

			OAuth2ServiceException e = new OAuth2ServiceException(
					this.getServiceName(), 401, "invalid_token",
					"The access token expired");

			e.addHeader("WWW-Authenticate",
					OAuthUtils.encodeOAuthHeader(e.getParameters()));
			throw e;
		}
		return resourcekey;
	}
}

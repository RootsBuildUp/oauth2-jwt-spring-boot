//package com.RootBuildUp.oauth2jwtspringboot.security;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.oauth2.provider.*;
//import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpSession;
//import java.util.Map;
//
//public class CustomOauth2RequestFactory extends DefaultOAuth2RequestFactory {
//
//	@Autowired
//	private TokenStore tokenStore;
//
//	@Autowired
//	private UserDetailsService userDetailsService;
//
//	private static final Logger LOG = LoggerFactory.getLogger(CustomOauth2RequestFactory.class);
//
//	public static final String SAVED_AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE_NAME = "savedAuthorizationRequest";
//
//	public CustomOauth2RequestFactory(ClientDetailsService clientDetailsService) {
//		super(clientDetailsService);
//	}
//
//
//	/**
//	 * Create token request.
//	 */
//	@Override
//	public TokenRequest createTokenRequest(Map<String, String> requestParameters,
//			ClientDetails authenticatedClient) {
//		System.out.println("----------create token request-----------");
//
//		requestParameters.put("scope","read+write+trust");
//		System.out.println(requestParameters);
//		System.out.println(authenticatedClient);
//				if (requestParameters.get("grant_type").equals("password")) {
//			SecurityContextHolder.getContext()
//					.setAuthentication(new UsernamePasswordAuthenticationToken(requestParameters.get("username"), null,
//							userDetailsService.loadUserByUsername(requestParameters.get("username")).getAuthorities()));
//		}
//		return super.createTokenRequest(requestParameters, authenticatedClient);
//	}
//
//	@Override
//	public AuthorizationRequest createAuthorizationRequest(Map<String, String> authorizationParameters) {
//
//		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//		HttpSession session = attr.getRequest().getSession(false);
//		if (session != null) {
//			AuthorizationRequest authorizationRequest = (AuthorizationRequest) session.getAttribute(SAVED_AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE_NAME);
//			if (authorizationRequest != null) {
//				session.removeAttribute(SAVED_AUTHORIZATION_REQUEST_SESSION_ATTRIBUTE_NAME);
//
//
//				LOG.debug("createAuthorizationRequest(): return saved copy.");
//
//				return authorizationRequest;
//
//
//			}
//		}
//
//		LOG.debug("createAuthorizationRequest(): create");
//		return super.createAuthorizationRequest(authorizationParameters);
//	}
//
//}

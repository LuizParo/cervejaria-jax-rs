package br.com.geladaonline.services;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glassfish.jersey.client.oauth1.AccessToken;

@WebFilter("/services/twitter/*")
public class TwitterLoginFilter implements Filter {
    public static final String OAUTH_VERIFIER = "oauth_verifier";
    public static final String ACCESS_TOKEN_KEY = "AccessToken";
    public static final String TOKEN_COOKIE = "TwitterAccessToken";
    public static final String TOKEN_COOKIE_SECRET = "TwitterAccessTokenSecret";
    public static final String EMPTY_COOKIE = "empty";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        Optional<AccessToken> accessToken = this.recuperaAccessTokenDosCookies(req);
        if(!accessToken.isPresent()) {
            // Estes parâmetros serão fornecidos caso a requisição seja o callback.
            String verifier = req.getParameter(OAUTH_VERIFIER);
            String token = req.getParameter(TwitterOAuthFlowService.OAUTH_TOKEN_FIELD);
            
            if (verifier != null && token != null) {
                // Ainda resta criar o método de verificação
                this.ajustaCookiesNaResposta(resp, TwitterOAuthFlowService.verify(token, verifier, req));
            } else {
                //Redirecionamos o usuário para URL fornecida pelo Twitter
                String twitterAuthUri = TwitterOAuthFlowService.init(req);
                resp.sendRedirect(twitterAuthUri);
                
                return;
            }
        }
        
        //Ajusta o Access Token na requisição para que possamos utilizá-lo posteriormente
        req.setAttribute(ACCESS_TOKEN_KEY, accessToken.get());
        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
        
    }
    
    private Optional<AccessToken> recuperaAccessTokenDosCookies(HttpServletRequest req) {
        String accessToken = null;
        String accessTokenSecret = null;

        Cookie[] cookies = req.getCookies();
        if(cookies == null) {
            return Optional.empty();
        }
        
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(TOKEN_COOKIE)) {
                if (!cookie.getValue().equals(EMPTY_COOKIE)) {
                    accessToken = cookie.getValue();
                }
            } else if (cookie.getName().equals(TOKEN_COOKIE_SECRET)) {
                if (!cookie.getValue().equals(EMPTY_COOKIE)) {
                    accessTokenSecret = cookie.getValue();
                }
            }
        }

        if (accessToken != null && accessTokenSecret != null) {
            return Optional.of(new AccessToken(accessToken, accessTokenSecret));
        }
        return Optional.empty();
    }
    
    private void ajustaCookiesNaResposta(HttpServletResponse resp, AccessToken accessToken) {
        Cookie accessTokenCookie = new Cookie(TOKEN_COOKIE, accessToken.getToken());
        accessTokenCookie.setPath("/");
        
        Cookie accessTokenSecretCookie = new Cookie(TOKEN_COOKIE_SECRET, accessToken.getAccessTokenSecret());
        accessTokenSecretCookie.setPath("/");
        
        resp.addCookie(accessTokenCookie);
        resp.addCookie(accessTokenSecretCookie);
    }
}
package br.com.geladaonline.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider;
import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider.Token;

@Path("/authorize")
public class AuthorizeOAuth {
    private static final String AUTHORIZE_FORM;
    
    @Context
    private DefaultOAuth1Provider provider;
    
    @Context
    private SecurityContext context;
    
    static {
        try {
            AUTHORIZE_FORM = IOUtils.toString(AuthorizeOAuth.class.getResourceAsStream("/login.html"));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String beginAuthorization(@QueryParam("oauth_token") String token) throws URISyntaxException {
        return AUTHORIZE_FORM.replace("$$$OAUTH_TOKEN$$$", token);
    }
    
    @POST
    public Response authorizeApp(@FormParam("oauth_token") String token,
                                 @FormParam("username") String usuario,
                                 @FormParam("password") String senha) throws URISyntaxException {
        final Token requestToken = this.provider.getRequestToken(token);
        Set<String> roles = new HashSet<>();
        roles.add("Leitura");
        
        String verifier = this.provider.authorizeToken(requestToken, new Principal() {
            
            @Override
            public String getName() {
                return provider.getConsumer(requestToken.getConsumer().getKey()).getOwner();
            }
        }, roles);
        
        StringBuilder callbackUrl = new StringBuilder(requestToken.getCallbackUrl());
        if (callbackUrl.toString().contains("?")) {
            callbackUrl.append("&");
        }
        else {
            callbackUrl.append("?");
        }
        callbackUrl.append("oauth_verifier=").append(verifier).append("&");
        callbackUrl.append("oauth_token=").append(token);
        return Response.status(Status.FOUND).location(new URI(callbackUrl.toString())).build();
    }
}
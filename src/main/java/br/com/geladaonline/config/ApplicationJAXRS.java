package br.com.geladaonline.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.jettison.JettisonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.oauth1.DefaultOAuth1Provider;
import org.glassfish.jersey.server.oauth1.OAuth1ServerFeature;
import org.glassfish.jersey.server.oauth1.OAuth1ServerProperties;

import br.com.geladaonline.services.WADLConfig;
import br.com.geladaonline.services.interceptor.CacheInterceptor;

public class ApplicationJAXRS extends Application {

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("jersey.config.server.provider.packages", "br.com.geladaonline.services");
        properties.put("jersey.config.server.wadl.generatorConfig", WADLConfig.class.getCanonicalName());
        properties.put(OAuth1ServerProperties.ENABLE_TOKEN_RESOURCES, Boolean.TRUE);
        
        return properties;
    }
    
    @Override
    public Set<Object> getSingletons() {
        DefaultOAuth1Provider provider = new DefaultOAuth1Provider();
        String idDoConsumidor = "App consumidora";
        String consumerKey = "123";
        String consumerSecret = "123";
        
        provider.registerConsumer(idDoConsumidor, consumerKey, consumerSecret, new MultivaluedStringMap());
        
        Set<Object> singletons = new HashSet<>();
        singletons.add(new JettisonFeature());
        singletons.add(new OAuth1ServerFeature(provider));
        singletons.add(new MultiPartFeature());
        singletons.add(new CacheInterceptor());
        
        return singletons;
    }
}
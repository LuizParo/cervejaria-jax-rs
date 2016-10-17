package br.com.geladaonline.services.interceptor;

import java.io.IOException;
import java.util.Date;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.message.internal.DateProvider;

import br.com.geladaonline.services.Cached;
import br.com.geladaonline.services.EntityCache;

@Cached
public class CacheInterceptor implements ContainerRequestFilter, ContainerResponseFilter {
    private EntityCache entityCache = new EntityCache();
    private DateProvider dateProvider = new DateProvider();
    
    // Invocado na requisição
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(requestContext.getMethod().equals("GET")) {
            String unparsedDate = requestContext.getHeaderString("If-Modified-Since");
            
            if(StringUtils.isNotEmpty(unparsedDate)) {
                Date date = this.dateProvider.fromString(unparsedDate);
                String path = requestContext.getUriInfo().getPath();
                
                if(!this.entityCache.isUpdated(path, date)) {
                    requestContext.abortWith(Response.status(Status.NOT_MODIFIED).build());
                }
            }
        }
    }

    // Invocado na resposta
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Object entity = responseContext.getEntity();
        String path = requestContext.getUriInfo().getPath();
        this.entityCache.put(path, entity);
    }
}
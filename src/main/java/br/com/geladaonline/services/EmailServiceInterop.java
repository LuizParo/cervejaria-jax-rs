package br.com.geladaonline.services;

import java.net.URI;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import br.com.geladaonline.model.Email;

@Path("/emailInterop")
public class EmailServiceInterop {
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    private static final Map<String, Email> EMAILS = new ConcurrentHashMap<>();
    
    @GET
    public Response recuperarEmails() {
        final String emailId = UUID.randomUUID().toString();
        
        THREAD_POOL.execute(() -> {
            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            
            Email email = new Email().withAssunto("Email recebido");
            EMAILS.put(emailId, email);
        });
        
        Calendar calendar = Calendar.getInstance();
        
        //Tempo do Thread.sleep + tempo para gerar o email
        calendar.add(Calendar.SECOND, 21);
        
        URI uri = UriBuilder
                .fromPath("/cervejaria/services")
                    .path(EmailServiceInterop.class)
                    .path("/{id}")
                .build(emailId);
        
        Link link = Link.fromUri(uri).build();
        
        return Response
                .accepted()
                .header("Location", link.getUri())
                .header("Expires", calendar.getTime())
                .build();
    }
    
    @Path("/{id}")
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response recuperarEmail(@PathParam("id") String id) {
        Email email = EMAILS.get(id);
        
        if(email == null) {
            return Response.noContent().build();
        }
        
        EMAILS.remove(id);
        return Response.ok(email).build();
    }
}
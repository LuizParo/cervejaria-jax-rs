package br.com.geladaonline.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;

import br.com.geladaonline.model.Anexo;
import br.com.geladaonline.model.Email;

@Path("/email")
public class EmailService {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public void recuperarEmails(@Suspended final AsyncResponse asyncResponse) {
        EXECUTOR_SERVICE.execute(() -> {
            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            
            Email email = new Email().withAssunto("Email recebido");
            asyncResponse.resume(email);
        });
    }
    
    @POST
    @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
    public void enviarEmailSimples(@HeaderParam("To") String para,
                                   @HeaderParam("Cc") String comCopia,
                                   @HeaderParam("Bcc") String comCopiaOculta,
                                   @HeaderParam("Subject") String assunto,
                                   String mensagem,
                                   @Context HttpHeaders httpHeaders) {
        Email email = new Email()
                .withDestinatario(para)
                .withCopia(comCopia)
                .withCopiaOculta(comCopiaOculta)
                .withAssunto(assunto)
                .withMensagem(mensagem, httpHeaders.getMediaType().toString());
        
        email.enviar();
    }
    
    @POST
    @Consumes("multipart/mixed")
    public void enviarEmailAnexos(@HeaderParam("To") String para,
                                  @HeaderParam("Cc") String comCopia,
                                  @HeaderParam("Bcc") String comCopiaOculta,
                                  @HeaderParam("Subject") String assunto,
                                  MultiPart multiPart) {
        Email email = new Email()
                .withDestinatario(para)
                .withAssunto(assunto)
                .withCopia(comCopia)
                .withCopiaOculta(comCopiaOculta);
        
        for (BodyPart bodyPart : multiPart.getBodyParts()) {
            String mediaType = bodyPart.getMediaType().toString();
            if(mediaType.startsWith("text/plain") || mediaType.startsWith("text/html")) {
                email = email.withMensagem(bodyPart.getEntityAs(String.class), mediaType);
            } else {
                Anexo anexo = new Anexo(bodyPart.getEntityAs(byte[].class), bodyPart.getMediaType().toString());
                email = email.withAnexo(anexo);
            }
        }
        
        email.enviar();
    }
}
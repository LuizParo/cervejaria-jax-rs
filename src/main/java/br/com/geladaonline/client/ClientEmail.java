package br.com.geladaonline.client;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jettison.JettisonFeature;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.message.internal.DateProvider;

import br.com.geladaonline.model.Email;

public class ClientEmail {
    
    public static void main(String[] args) throws URISyntaxException, InterruptedException, ExecutionException {
        enviaEmailSimples();
        enviaEmailComAnexos();
        recuperaEmailAssincrono();
        recuperaEmailComExpires();
    }

    private static void recuperaEmailComExpires() throws InterruptedException {
        Response response = ClientBuilder
                .newClient()
                .target("http://localhost:8080/cervejaria/services/emailInterop")
                .request()
                .get();
        
        System.out.println("Status da resposta:");
        System.out.println(response.getStatus());
        
        String expires = response.getHeaderString("Expires");
        System.out.println("Retornar a requisição em " + expires);
        System.out.println("link recuperado: " + response.getHeaderString("Location"));
        
        Date date = new DateProvider().fromString(expires);
        
        long now = System.currentTimeMillis();
        long waitUntil = date.getTime() - now;
        
        System.out.println("Esperar " + (waitUntil ) + " millissegundos");
        Thread.sleep(waitUntil);
        
        System.out.println("Ressubmetendo a requisição...");
        response = ClientBuilder.newClient()
            .target(response.getHeaderString("Location"))
            .register(JettisonFeature.class)
            .request()
            .get();
        
        System.out.println("Lido:");
        System.out.println(response.readEntity(Email.class));
    }

    private static void enviaEmailComAnexos() throws URISyntaxException {
        URI uriDoArquivo = ClientEmail.class.getResource("/Erdinger Weissbier.jpg").toURI();
        File arquivo = new File(uriDoArquivo);
        
        MultiPart multiPart = new MultiPart();
        multiPart.bodyPart("Mensagem com anexos", MediaType.TEXT_PLAIN_TYPE)
            .bodyPart(new FileDataBodyPart("imagem", arquivo));
        
        ClientBuilder.newClient()
            .register(MultiPartFeature.class)
            .target("http://localhost:8080/cervejaria/services/email")
                .request()
                    .header("To", "alesaudate@gmail.com")
                    .header("Cc", "fake@fake.com")
                    .header("Bcc", "fake2@fake.com")
                    .header("Subject", "Olá, mundo!")
                .post(Entity.entity(multiPart, multiPart.getMediaType()));
    }

    private static void enviaEmailSimples() {
        ClientBuilder.newClient()
            .target("http://localhost:8080/cervejaria/services/email")
                .request()
                    .header("To", "alesaudate@gmail.com")
                    .header("Cc", "fake@fake.com")
                    .header("Bcc", "fake2@fake.com")
                    .header("Subject", "Olá, mundo!")
                .post(Entity.text("Teste de envio de mensagens de email utilizando JAX-RS"));
    }
    
    private static void recuperaEmailAssincrono() throws InterruptedException, ExecutionException {
        Future<Response> futureResponse = ClientBuilder.newClient()
            .target("http://localhost:8080/cervejaria/services/email")
                .request()
                .async()
                .get();
        
        System.out.println("Requisição submetida. Aguardando resposta...");
        
        Response response = futureResponse.get();
        System.out.println("Resposta:");
        System.out.println(response.getStatus());
        System.out.println(response.readEntity(Email.class));
    }
}
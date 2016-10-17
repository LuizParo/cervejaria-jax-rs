package br.com.geladaonline.services;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jettison.JettisonFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import br.com.geladaonline.config.ApplicationJAXRS;
import br.com.geladaonline.model.Cerveja;
import br.com.geladaonline.model.Cervejas;

public class CervejaServiceTest extends JerseyTest {
    private static final String PATH_TO_SERVICE = "http://localhost:8080/cervejaria/services/";

    @Override
    protected Application configure() {
        return new ApplicationJAXRS();
    }

    @Test
    public void testeCenarioFeliz() throws UnsupportedEncodingException {
        Cervejas cervejas = this.target("/cervejas")
            .register(JettisonFeature.class)
            .request()
            .get(Cervejas.class);
        
        Assert.assertNotNull(cervejas);
        Assert.assertNotNull(cervejas.getLinks());
        Assert.assertFalse(cervejas.getLinks().isEmpty());
        Assert.assertEquals(2, cervejas.getLinks().size());
        
        Link link1 = cervejas.getLinks().get(0);
        Link link2 = cervejas.getLinks().get(1);
        
        Assert.assertEquals(PATH_TO_SERVICE + "cervejas/Erdinger Weissbier", this.decodeURI(link1.getUri()));
        Assert.assertEquals(PATH_TO_SERVICE + "cervejas/Stella Artois", this.decodeURI(link2.getUri()));
        Assert.assertEquals("cerveja", link1.getRel());
        Assert.assertEquals("cerveja", link2.getRel());
        Assert.assertEquals("Erdinger Weissbier", link1.getTitle());
        Assert.assertEquals("Stella Artois", link2.getTitle());
        
        Cerveja erdinger = this.target(this.extractResourceFromLink(link1))
                .register(JettisonFeature.class)
                .request(MediaType.APPLICATION_XML)
                .get(Cerveja.class);
        
        Assert.assertNotNull(erdinger);
        Assert.assertEquals("Erdinger Weissbier", erdinger.getNome());
        Assert.assertEquals("Erdinger Weissbräu", erdinger.getCervejaria());
        Assert.assertEquals("Cerveja de trigo alemã", erdinger.getDescricao());
        Assert.assertEquals(Cerveja.Tipo.WEIZEN, erdinger.getTipo());
        
        Cerveja stella = this.target(this.extractResourceFromLink(link2))
                .register(JettisonFeature.class)
                .request(MediaType.APPLICATION_XML)
                .get(Cerveja.class);
        
        Assert.assertNotNull(stella);
        Assert.assertEquals("Stella Artois", stella.getNome());
        Assert.assertEquals("Artois", stella.getCervejaria());
        Assert.assertEquals("A cerveja belga mais francesa do mundo :)", stella.getDescricao());
        Assert.assertEquals(Cerveja.Tipo.LAGER, stella.getTipo());
    }
    
    @Test
    public void testInsertCerveja() throws UnsupportedEncodingException {
        Cerveja skol = new Cerveja("Skol", "Cerveja dinamarquesa abrasileirada", "Ambev", Cerveja.Tipo.PILSEN);
        
        Response response = this.target("/cervejas")
            .request()
            .post(Entity.xml(skol));
        
        Assert.assertNotNull(response);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertNotNull(response.getLocation());
        Assert.assertTrue(this.decodeURI(response.getLocation()).endsWith("/cervejas/Skol"));
        
        Cerveja skolRecuperada = this.target("/cervejas/Skol")
                .request(MediaType.APPLICATION_XML)
                .get(Cerveja.class);
        
        Assert.assertEquals("Skol", skolRecuperada.getNome());
        Assert.assertEquals("Cerveja dinamarquesa abrasileirada", skolRecuperada.getDescricao());
        Assert.assertEquals(Cerveja.Tipo.PILSEN, skolRecuperada.getTipo());
        Assert.assertEquals("Ambev", skolRecuperada.getCervejaria());
    }

    private String decodeURI(URI uri) throws UnsupportedEncodingException {
        return URLDecoder.decode(uri.toASCIIString(), "UTF-8");
    }
    
    private String extractResourceFromLink(Link link) throws UnsupportedEncodingException {
        return this.decodeURI(link.getUri()).replace(PATH_TO_SERVICE, "");
    }
}
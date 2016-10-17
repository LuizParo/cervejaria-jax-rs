package br.com.geladaonline.services;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;

import br.com.geladaonline.exception.CervejaJaExisteException;
import br.com.geladaonline.model.Cerveja;
import br.com.geladaonline.model.Estoque;
import br.com.geladaonline.model.Imagem;
import br.com.geladaonline.model.rest.Cervejas;
import br.com.geladaonline.util.Hash;

@Path("/cervejas")
@Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Cached
public class CervejaService {
    private static final Estoque ESTOQUE = new Estoque();
    public static final int TAMANHO_PAGINA = 20;
    
    @Context
    private SecurityContext context;
    
    @Context
    private UriInfo uriInfo;
    
    @GET
    public Cervejas listeTodasAsCervejas(@QueryParam("pagina") int pagina) {
        MultivaluedMap<String, String> queryMap = this.uriInfo.getQueryParameters();
        List<Cerveja> cervejas = ESTOQUE.listarCervejasPorExemplos(pagina, TAMANHO_PAGINA, queryMap);
        
        return new Cervejas(cervejas);
    }
    
    @GET
    @Path("{nome}")
    public Response encontrarCerveja(@PathParam("nome") String nomeDaCerveja) {
        Principal principal = this.context.getUserPrincipal();
        
        if(principal != null) {
            System.out.println("Quem está acessando? " + principal.getName());
        }
        
        Cerveja cerveja = ESTOQUE.recuperarCervejaPeloNome(nomeDaCerveja);
        if(cerveja == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok(cerveja).tag(Hash.hash(cerveja)).build();
    }
    
    @GET
    @Path("{nome}")
    @Produces("image/*")
    public Response recuperaImagem(@PathParam("nome") String nomeDaCerveja) {
        // Check ImageProducer.java
        Cerveja cerveja = new Cerveja(nomeDaCerveja, null, null, null);
        return Response.ok(cerveja).type("image/jpg").build();
    }
    
    @POST
    public Response criarCerveja(Cerveja cerveja) {
        try {
            ESTOQUE.adicionarCerveja(cerveja);
        } catch (CervejaJaExisteException e) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        }
        URI location = UriBuilder.fromPath("cervejas/{nome}").build(cerveja.getNome());
        return Response.created(location).entity(cerveja).build();
    }
    
    @POST
    @Path("{nome}")
    @Consumes("image/*")
    public Response criaImagem(Imagem imagem) {
        imagem.salvar(System.getProperty("user.home"));
        return Response.ok().build();
    }
    
    @PUT
    @Path("{nome}")
    public void atualizaCerveja(@PathParam("nome") String nome,
                                @PathParam("If-Match") String eTag,
                                Cerveja cerveja) {
        testaETag(eTag, nome);
        this.encontrarCerveja(nome);
        cerveja.setNome(nome);
        ESTOQUE.atualizarCerveja(cerveja);
    }
    
    @DELETE
    @Path("{nome}")
    public void apagarCerveja(@PathParam("nome") String nome) {
        ESTOQUE.apagarCerveja(nome);
    }
    
    private void testaETag(String eTag, String nomeDaCerveja) {
        Response responseEncontrarCerveja = this.encontrarCerveja(nomeDaCerveja);
        
        if(StringUtils.isNotEmpty(eTag) && !responseEncontrarCerveja.getEntityTag().getValue().equals(eTag)) {
            throw new WebApplicationException(Status.CONFLICT);
        }
    }
}
package br.com.geladaonline.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import br.com.geladaonline.exception.CervejaJaExisteException;
import br.com.geladaonline.exception.CervejaNaoEncontradaException;

public class Estoque {
    private Map<String, Cerveja> cervejas = new HashMap<>();

    public Estoque() {
        Cerveja primeiraCerveja = new Cerveja("Stella Artois", "A cerveja belga mais francesa do mundo :)", "Artois", Cerveja.Tipo.LAGER);
        Cerveja segundaCerveja = new Cerveja("Erdinger Weissbier", "Cerveja de trigo alemã", "Erdinger Weissbräu", Cerveja.Tipo.WEIZEN);

        this.cervejas.put(primeiraCerveja.getNome(), primeiraCerveja);
        this.cervejas.put(segundaCerveja.getNome(), segundaCerveja);
    }

    public List<Cerveja> listarCervejas() {
        return new ArrayList<>(this.cervejas.values());
    }   

    public List<Cerveja> listarCervejas(int numeroPagina, int tamanhoPagina) {
        return this.filtrarPaginacao(numeroPagina, tamanhoPagina, this.listarCervejas());
    }
    
    private List<Cerveja> filtrarPaginacao(int numeroPagina, int tamanhoPagina, List<Cerveja> cervejas) {
        int indiceInicial = numeroPagina * tamanhoPagina;
        int indiceFinal = indiceInicial + tamanhoPagina;

        if (cervejas.size() > indiceInicial) {
            if (cervejas.size() > indiceFinal) {
                cervejas = cervejas.subList(indiceInicial, indiceFinal);
            } else {
                cervejas = cervejas.subList(indiceInicial, cervejas.size());
            }
        } else {
            cervejas = new ArrayList<>();
        }
        return cervejas;
    }

    public void adicionarCerveja(Cerveja cerveja) {
        if (this.cervejas.containsKey(cerveja.getNome())) {
            throw new CervejaJaExisteException("Cerveja " + cerveja.getNome() + " já existe!");
        }
        this.cervejas.put(cerveja.getNome(), cerveja);
    }

    public void atualizarCerveja(Cerveja cerveja) {
        if (!this.cervejas.containsKey(cerveja.getNome())) {
            throw new CervejaNaoEncontradaException("Cerveja " + cerveja.getNome() + " não encontrada!");
        }
        this.cervejas.put(cerveja.getNome(), cerveja);
    }

    public Cerveja recuperarCervejaPeloNome(String nome) {
        return this.cervejas.get(nome);
    }

    public void apagarCerveja(String nome) {
        this.cervejas.remove(nome);
    }
    
    public List<Cerveja> listarCervejasPorExemplos(int numeroPagina, int tamanhoPagina, MultivaluedMap<String, String> exemplos) {
        List<Cerveja> resultados = new ArrayList<>();
        
        Cerveja exemplo = Cerveja.builder()
            .withNome(exemplos.getFirst("nome"))
            .withCervejaria(exemplos.getFirst("cervejaria"))
            .withDescricao(exemplos.getFirst("descricao"))
            .withTipo(exemplos.getFirst("tipo"))
            .build();
        
        this.listarCervejas().forEach(cerveja -> {
            if(exemplo.matchExemplo(cerveja)) {
                resultados.add(cerveja);
            }
        });
        
        return this.filtrarPaginacao(numeroPagina, tamanhoPagina, resultados);
    }
}
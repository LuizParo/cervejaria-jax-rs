package br.com.geladaonline.model;

import java.util.Arrays;

public class Anexo {
    private byte[] conteudo;
    private String tipoDeConteudo;
    
    public Anexo() {
        // default constructor
    }
    
    public Anexo(byte[] conteudo, String tipoDeConteudo) {
        this.conteudo = conteudo;
        this.tipoDeConteudo = tipoDeConteudo;
    }

    @Override
    public String toString() {
        return "Anexo [conteudo=" + Arrays.toString(conteudo) + ", tipoDeConteudo=" + tipoDeConteudo + "]";
    }
}
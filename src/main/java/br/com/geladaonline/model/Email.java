package br.com.geladaonline.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Email {
    public static final String FORMATO_PADRAO = "text/plain";
    public static final String SEPARADOR_ENDERECOS = ",";

    private String mensagem = "";
    private String formatoMensagem = FORMATO_PADRAO;
    private String assunto = "";
    private List<String> destinatarios = new ArrayList<>();
    private List<String> comCopia = new ArrayList<>();
    private List<String> comCopiaOculta = new ArrayList<>();
    private List<Anexo> anexos = new ArrayList<>();

    public Email withMensagem(String mensagem, String formato) {
        if (mensagem != null && formato != null) {
            this.mensagem = mensagem;
            this.formatoMensagem = formato;
        }
        return this;
    }

    public Email withAssunto(String assunto) {
        if (assunto != null) {
            this.assunto = assunto;
        }
        return this;
    }

    public Email withDestinatario(String destinatario) {
        if (destinatario != null) {
            this.withDestinatarios(destinatario.split(SEPARADOR_ENDERECOS));
        }
        return this;
    }

    public Email withDestinatarios(String... destinatarios) {
        if (destinatarios != null) {
            this.destinatarios.addAll(Arrays.asList(destinatarios));
        }
        return this;
    }

    public Email withCopia(String comCopia) {
        if (comCopia != null) {
            this.withCopias(comCopia.split(SEPARADOR_ENDERECOS));
        }
        return this;
    }

    public Email withCopias(String... comCopias) {
        if (comCopias != null) {
            this.comCopia.addAll(Arrays.asList(comCopias));
        }
        return this;
    }

    public Email withCopiaOculta(String comCopiaOculta) {
        if (comCopiaOculta != null) {
            this.withCopiasOcultas(comCopiaOculta.split(SEPARADOR_ENDERECOS));
        }
        return this;
    }

    public Email withCopiasOcultas(String... copiasOcultas) {
        if (copiasOcultas != null) {
            this.comCopiaOculta.addAll(Arrays.asList(copiasOcultas));
        }
        return this;
    }
    
    public Email withAnexo(Anexo anexo) {
        if(anexo != null) {
            this.anexos.add(anexo);
        }
        return this;
    }

    public void enviar() {
        System.out.println("Enviando email...: " + this.toString());
    }

    @Override
    public String toString() {
        return "Email [mensagem=" + mensagem + ", formatoMensagem=" + formatoMensagem + ", assunto=" + assunto
                + ", destinatarios=" + destinatarios + ", comCopia=" + comCopia + ", comCopiaOculta=" + comCopiaOculta
                + ", anexos=" + anexos + "]";
    }
}
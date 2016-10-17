package br.com.geladaonline.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;

public class Imagem {
    private static final Map<String, String> EXTENSOES;
    
    private final byte[] dados;
    private final String nome;
    private final MediaType mediaType;
    
    static {
        EXTENSOES = new HashMap<>();
        EXTENSOES.put("image/jpg", ".jpg");
    }
    
    public Imagem(byte[] dados, String nome, MediaType mediaType) {
        this.dados = dados;
        this.nome = nome;
        this.mediaType = mediaType;
    }
    
    public static boolean containsMediaType(MediaType mediaType) {
        if (!EXTENSOES.containsKey(mediaType.toString())) {
            return false;
        }
        return true;
    }

    public void salvar(String caminho) {
        try (OutputStream output = new FileOutputStream(caminho + File.separator + nome + EXTENSOES.get(this.mediaType.getType()))) {
            IOUtils.write(this.dados, output);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
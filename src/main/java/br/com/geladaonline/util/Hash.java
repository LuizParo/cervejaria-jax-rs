package br.com.geladaonline.util;

import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.builder.HashCodeBuilder;

public final class Hash {
    
    private Hash() {}
    
    public static String hash(Object entity) {
        int hashCode = HashCodeBuilder.reflectionHashCode(entity);
        
        // Converte o int em array de bytes
        byte[] array = ByteBuffer.allocate(4).putInt(hashCode).array();
        
        // Converte o array em hexadecimal
        return Hex.encodeHexString(array);
    }
}
package br.com.geladaonline.services.producer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;

import br.com.geladaonline.model.Cerveja;

@Provider
public class ImageProducer implements MessageBodyWriter<Cerveja> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isAssignableFrom(Cerveja.class) && mediaType.getType().equals("image");
    }

    @Override
    public long getSize(Cerveja t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }

    @Override
    public void writeTo(Cerveja t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        InputStream stream = ImageProducer.class.getResourceAsStream("/" + t.getNome() + ".jpg");
        
        if(stream == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        
        IOUtils.copy(stream, entityStream);
        IOUtils.closeQuietly(stream);
    }
}
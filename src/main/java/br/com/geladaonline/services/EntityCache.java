package br.com.geladaonline.services;

import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityCache {
    private static final long INTERVALO_CEGO = 1000L;
    private Map<String, SoftReference<EntityDatePair>> objectCache = new ConcurrentHashMap<>();
    
    public void put(String path, Object entity) {
        EntityDatePair pair = new EntityDatePair(new Date());
        SoftReference<EntityDatePair> sr = new SoftReference<>(pair);
        this.objectCache.put(path, sr);
    }
    
    public boolean isUpdated(String path, Date since) {
        SoftReference<EntityDatePair> sr = this.objectCache.get(path);
        
        if(sr != null) { // a referência não foi inserida no mapa
            EntityDatePair pair = sr.get();
            if(pair == null) { // se a referência foi limpa pelo GC
                this.objectCache.remove(path);
                return true;
            }
            
            long tempoArmazenado = pair.getDate().getTime() / INTERVALO_CEGO;
            long tempoFornecido = since.getTime() / INTERVALO_CEGO;

            // se a data armazenada é posterior à data passada como parâmetro,
            // significa que o objeto foi alterado
            return tempoArmazenado > tempoFornecido;
        }
        
        return true;
    }
    
    private static class EntityDatePair {
        private Date date;
        
        public EntityDatePair(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return date;
        }
    }
}
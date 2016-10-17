package br.com.geladaonline.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Cervejas {
    
    @XmlElement(name = "link")
    private List<CustomLink> links = new ArrayList<>();
    
    public Cervejas() {
        // default constructor
    }
    
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<>();
        
        this.links.forEach(customLink -> {
            Link link = Link.fromUri(Constants.HOST + customLink.getHref())
                .rel(customLink.getRel())
                .title(customLink.getTitle())
                .build();
            
            links.add(link);
        });
        
        return links;
    }
}
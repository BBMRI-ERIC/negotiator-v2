package de.samply.bbmri.negotiator.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.samply.bbmri.negotiator.model.Query;

@ManagedBean
@SessionScoped
public class SessionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Query> queries;
    
    public List<Query> getQueries() {
        return queries;
    }

    @PostConstruct
    private void init() {
        queries = new ArrayList<>();
        queries.add(new Query(1, "A query to find them all", new Date(), new Date(), 3, "Bla bla blaaaaaa"));
        queries.add(new Query(2, "This one does lie", new Date(), new Date(), 0, "Noch mehr Text"));
        
    }
    
    
}

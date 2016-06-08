package de.samply.bbmri.negotiator.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.samply.bbmri.negotiator.model.TestQuery;

@ManagedBean
@SessionScoped
public class SessionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<TestQuery> queries = null;
    private List<String> filters = null;
    private String filterToAdd = null;

    public List<TestQuery> getQueries() {
        if (queries == null) {
            queries = new ArrayList<>();
            queries.add(new TestQuery(1, "A query to find them all", "Bla bla blaaaaaa", 3, new Date(), new Date(), "Hans Meiser"));
            queries.add(new TestQuery(2, "Lorem ipsum?",
                    "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
                    0, new Date(), new Date(), "William Dafoe"));
        }

        return queries;
    }

    public void addFilter() {
        if (filters == null)
            filters = new ArrayList<String>();

        if (filterToAdd == null || "".equals(filterToAdd))
            return;

        if (filters.contains(filterToAdd)) {
            filterToAdd = "";
            return;
        }

        filters.add(filterToAdd);
        filterToAdd = "";
    }

    public void removeFilter(String arg) {
        if (filters == null)
            return;

        filters.remove(arg);
    }

    public List<String> getFilters() {
        if (filters == null)
            filters = new ArrayList<String>();

        return filters;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
    }

    public String getFilterToAdd() {
        return filterToAdd;
    }

    public void setFilterToAdd(String filterToAdd) {
        this.filterToAdd = filterToAdd;
    }

}

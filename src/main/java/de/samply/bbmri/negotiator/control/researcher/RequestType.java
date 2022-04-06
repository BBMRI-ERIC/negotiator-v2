package de.samply.bbmri.negotiator.control.researcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestType {

    private Integer id;

    private String label;
    private String type;
    private static final Logger logger = LogManager.getLogger(RequestType.class);

    public RequestType() {
    }

    public RequestType(Integer id, String label, String type) {
        this.setId( id);
        this.label = label;
        this.type = type;
    }

    // Getter and Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return id + ": " + label + " " + type;
    }
}



package de.samply.bbmri.negotiator.control.component;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.FileUtil;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryRecord;
import de.samply.bbmri.negotiator.model.QueryAttachmentDTO;
import org.apache.commons.codec.digest.DigestUtils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean
@ViewScoped
public class FileUploadBean implements Serializable {

    List<FacesMessage> msgs = null;

    Negotiator negotiator = NegotiatorConfig.get().getNegotiator();

    /**
     * Query attachment upload.
     */
    private Part file;

    /**
     * Inits the state.
     */
    @PostConstruct
    public void init() {
    }

    public void validateFile(FacesContext ctx, UIComponent comp, Object value) throws IOException {
        //clear message list every time a new file is selected for validation
        FileUtil fileUtil = new FileUtil();
        Part file = (Part)value;

        msgs = fileUtil.validateFile(file, negotiator.getMaxUploadFileSize());

        if (msgs != null && !msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
    }

    /*
     * Setter and Getter
     */

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }
}

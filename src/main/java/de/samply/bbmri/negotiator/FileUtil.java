/**
 * Copyright (C) 2016 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.bbmri.negotiator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;

import de.samply.bbmri.negotiator.helper.MessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.string.util.StringUtil;
import fi.solita.clamav.ClamAVClient;

public class FileUtil {


    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * ClamAV timeout in milliseconds. Set to 20 seconds.
     *
     */
    private static final int DEFAULT_TIMEOUT = 20000;

    /**
     * Copies file to query attachments directory
     * @param file file
     * @return name of persisted file
     */
    public String saveQueryAttachment(Part file, String fileName) {
        String uploadPath = NegotiatorConfig.get().getNegotiator().getAttachmentPath();
        File uploadDir = new File(uploadPath);
        uploadDir.mkdirs();
        
        try (InputStream input = file.getInputStream()) {
            
            Files.copy(input, new File(uploadDir, fileName).toPath());
            return fileName;
            
        }
        catch (IOException e) {
            logger.error("Couldn't save attachment ", e);
            return null;
        }
    }
    
    /**
     * Retrieve the file name of a javax.servlet.http.Part
     * @param filePart
     * @return
     */
    public String getOriginalFileNameFromPart(Part filePart) {
        String header = filePart.getHeader("content-disposition");
        for(String headerPart : header.split(";")) {
            if(headerPart.trim().startsWith("filename")){
                return headerPart.substring(headerPart.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    public String getStorageFileName(Integer queryId, Integer fileId, String fileName) {
        String[] splitFilename = fileName.split("\\.");
        String fileExtension = splitFilename[splitFilename.length - 1];
        return "query_" + queryId + "_file_" + fileId + "." + fileExtension;
    }

    public Pattern getStorageNamePattern() {
        return Pattern.compile("^query_(\\d*)_file_(\\d*)\\.(\\w*)_salt_(.*)");
    }

    /**
     * Checks the given file for a virus using clamav. Returns
     * @param config the negotiator configuration
     * @param inputStream the fileinputstream that will be checked for viruses
     * @return true, if a virus has been found. False otherwise
     */
    public boolean checkVirusClamAV(Negotiator config, InputStream inputStream) throws IOException {
        /**
         * If there is no clamav configured, return false and do not check for viruses
         */
        if(StringUtil.isEmpty(config.getClamavHost()) || config.getClamavPort() == 0) {
            logger.warn("Negotiator not configured for ClamAV. Skipping the check for viruses. This is dangerous!");
            return false;
        }

        ClamAVClient cl = new ClamAVClient(config.getClamavHost(), config.getClamavPort(), DEFAULT_TIMEOUT);

        /**
         * ClamAV has been configured, but once an error occurs, we assume
         * something is very wrong and do not continue.
         */
        try {
            byte[] reply = cl.scan(inputStream);
            return !ClamAVClient.isCleanReply(reply);
        } catch (Exception e) {
            logger.error("Error while scanning file: ", e);
            throw e;
        }
    }

    public List<FacesMessage> validateFile(Part file, int max_upload_size) throws ValidatorException {
        if(file != null) {
            List<FacesMessage> msgs = new ArrayList<FacesMessage>();
            if (file.getSize() > max_upload_size) {
                msgs.addAll(MessageHelper.generateValidateFileMessages(max_upload_size));
            }
            try {
                if (checkVirusClamAV(NegotiatorConfig.get().getNegotiator(), file.getInputStream())) {
                    msgs.addAll(MessageHelper.generateValidateFileMessages("checkVirusClamAVTriggeredVirusWarning"));
                }
            } catch (Exception e) {
                msgs.addAll(MessageHelper.generateValidateFileMessages(e.getMessage()));
            }
            return msgs;
        }
        return null;
    }
}

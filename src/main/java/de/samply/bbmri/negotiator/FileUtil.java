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

import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {


    private static Logger logger = LoggerFactory.getLogger(MailUtil.class);
    
    /**
     * Copies file to query attachments directory
     * @param uploaded file
     * @return name of persisted file
     */
    public static String saveQueryAttachment(Part file) {
        String uploadPath = NegotiatorConfig.getConfig().getString("attachments.path");
        File uploadDir = new File(uploadPath);
        uploadDir.mkdirs();
        
        try (InputStream input = file.getInputStream()) {
            String filename = getFileName(file);
            
            Files.copy(input, new File(uploadDir, filename).toPath());
            return filename;
            
        }
        catch (IOException e) {
            logger.error("Couldn't save attachment ", e);
        }
        return null;
    }
    
    /**
     * Retrieve the file name of a javax.servlet.http.Part
     * @param filePart
     * @return
     */
    public static String getFileName(Part filePart) {
        String header = filePart.getHeader("content-disposition");
        for(String headerPart : header.split(";")) {
            if(headerPart.trim().startsWith("filename")){
                return headerPart.substring(headerPart.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}

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

import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {


    private static Logger logger = LoggerFactory.getLogger(MailUtil.class);
    private static final String UPLOAD_DIR = "uploads";
    
    /**
     * Copies the uploaded file to the UPLOAD_DIR directory, which is relative to the web application dir
     * @param part
     * @param fileName
     * @return
     */
    public static File getUploadAsFile(Part part, String fileName) {
        String absoluteWebPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
        String uploadDirPath = absoluteWebPath + UPLOAD_DIR;
        
        File uploadDir = new File(uploadDirPath);
        uploadDir.mkdirs();
         
        try  {
            File uploadedFile = new File(uploadDirPath + File.separator + fileName);
            FileUtils.copyInputStreamToFile(part.getInputStream(), uploadedFile);
            return uploadedFile;
        } 
        catch (IOException e) {
            logger.error("Couldn't load file content " + e.getMessage());
            return null;
        }
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

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
import de.samply.bbmri.negotiator.config.Negotiator;
import de.samply.bbmri.negotiator.jooq.Tables;
import de.samply.bbmri.negotiator.jooq.tables.records.QueryAttachmentRecord;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* The File servlet for serving from absolute path. */

public class FileServlet extends HttpServlet {

    /* Constant buffer size for the files. */

    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

    /* property for path of the files. */
    private String filePath;

    /* init function for the servlet. */

    @Override
    public void init() throws ServletException {

        /* Base path for the file.  */
        this.filePath = NegotiatorConfig.get().getNegotiator().getAttachmentPath();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        /* Get requested file by path info.  */
        String requestedFile = request.getPathInfo();

        if(requestedFile == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST); // 400
            return;
        }

        // drop the starting slash from the string and decode

        Base64 decoder = new Base64(true);
        byte[] decodedBytes = decoder.decode(requestedFile.replaceAll("^/", ""));
        requestedFile = new String(decodedBytes);

        // We provide for the fileservlet a concatted name with queryID and fileID in it
        // and here now cut it in pieces again. The reason for that is "security", so
        // you cannot by simple guess download files of other queries.

        //XXX: this pattern needs to match QueryBean.uploadAttachment() and ResearcherQueriesDetailBean.getAttachmentMap
        // patterngrops 1: queryID, 2: fileID, 3: fileName
        Pattern pattern = Pattern.compile("^query_(\\d*)_file_(\\d*)\\.(\\w*)_salt_(.*)\\.download$");
        Matcher matcher = pattern.matcher(requestedFile);
        String filenameSalt = null;

        if(matcher.find()) {
            filenameSalt = matcher.group(4);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        /* Check if file is actually supplied to the request URI.  */
        if (requestedFile == null || filenameSalt == null || "".equals(filenameSalt)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        String queryId = matcher.group(1);
        String fileId = matcher.group(2);
        String fileExtension = matcher.group(3);
        Negotiator negotiatorConfig = NegotiatorConfig.get().getNegotiator();

        // check if the salt fits
        String trueFileName = FileUtil.getStorageFileName(Integer.parseInt(queryId), Integer.parseInt(fileId), fileExtension);
        String saltCheck = DigestUtils.sha256Hex(negotiatorConfig.getUploadFileSalt() +
                trueFileName);

        if(!saltCheck.equalsIgnoreCase(filenameSalt)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST); // 400
            return;
        }

        /* Decode the file name (might contain spaces and on) and prepare file object.  */
        File file = new File(filePath, URLDecoder.decode(trueFileName, "UTF-8"));

        /* Check if file actually exists in filesystem.  */
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        String downloadFileName = null;
        try(Config config = ConfigFactory.get()) {
            QueryAttachmentRecord attachmentRecord = config.dsl().selectFrom(Tables.QUERY_ATTACHMENT).where(Tables.QUERY_ATTACHMENT
                    .ID.eq(Integer.parseInt(fileId)))
                    .fetchOneInto(Tables.QUERY_ATTACHMENT);
            downloadFileName = attachmentRecord.getAttachment();
        } catch(SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            return;
        }

        /* Get content type by filename.  */
        String contentType = getServletContext().getMimeType(file.getName());

        /* If content type is unknown, then set the default value.  */
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        /* Init servlet response.  */
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType(contentType);
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName + "\"");

        /* Prepare streams.  */
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            /* Open streams.  */
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            /* Write file contents to response.  */
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } finally {
            /* Close streams. */
            close(output);
            close(input);
        }
    }

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
package de.samply.bbmri.negotiator.test;

import de.samply.bbmri.negotiator.MailUtil;
import de.samply.bbmri.negotiator.NegotiatorConfig;
import de.samply.common.mailing.EmailBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

/**
 * Copyright (C) 2017 Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 * <p>
 * Additional permission under GNU GPL version 3 section 7:
 * <p>
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */
public class EmailBuilderTest {
    @Test
    public void emailtest() {
        File resourcesPath = FileUtils.getFile("src", "main", "webapp", "resources", "emailTemplates");

        System.out.println("Reading email soys from path "+resourcesPath.getAbsolutePath());

        for(File fileEntry: resourcesPath.listFiles()) {
            if(fileEntry.isDirectory())
                continue;

            // ignore main and footer as they are used anyways
            if(fileEntry.getName().equals("main.soy"))
                continue;

            if(fileEntry.getName().equals("Footer.soy"))
                continue;

            System.out.println("Email-Soy: "+fileEntry.getName());
            System.out.println("================== BEGIN  ===================== ");

            EmailBuilder builder = new EmailBuilder(resourcesPath.getAbsolutePath(),false);
            builder.addTemplateFile("main.soy", null);
            builder.addTemplateFile("Footer.soy", "Footer");
            builder.addTemplateFile(fileEntry.getName(), "Notification");

            HashMap<String, String> parameters = new HashMap();
            parameters.put("queryName", "A long blabla test query name string");
            parameters.put("link", "http://this.goes.nowhere/really.nowhere.html");

            System.out.println(builder.getText(parameters));

            System.out.println("================== END ===================== ");
        }
    }
}

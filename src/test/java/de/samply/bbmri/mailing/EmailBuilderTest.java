package de.samply.bbmri.mailing;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EmailBuilderTest {

    File resourcesPath_;

    @BeforeEach
    void setUp() {
        resourcesPath_ = FileUtils.getFile("src", "main", "webapp", "resources", "emailTemplates");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getText() {

        for(File fileEntry: resourcesPath_.listFiles()) {
            if(fileEntry.isDirectory())
                continue;

            // ignore main and footer as they are used anyways
            if(fileEntry.getName().equals("main.soy"))
                continue;

            if(fileEntry.getName().equals("Footer.soy"))
                continue;

            System.out.println("Email-Soy: "+fileEntry.getName());
            System.out.println("================== BEGIN  ===================== ");

            EmailBuilder builder = new EmailBuilder(resourcesPath_.getAbsolutePath(),false);
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
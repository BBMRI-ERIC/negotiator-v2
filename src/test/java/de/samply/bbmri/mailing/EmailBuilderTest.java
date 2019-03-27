package de.samply.bbmri.mailing;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Email building from templates")
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
    @DisplayName("Test getText for mail creation from template files")
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

    @Test
    @DisplayName("Test getText for template emailTest.soy")
    void testGetTextForEmailTestSoy() {
        String text = "Dear Sir or Madam,\n" +
                "\n" +
                "This is a test email sent by the BBMRI Negotiator.\n" +
                "\n" +
                "Yours sincerely\n" +
                "The BBMRI Team" +
                "\n";

        HashMap<String, String> parameters = new HashMap();
        parameters.put("queryName", "A long blabla test query name string");
        parameters.put("url", "http://this.goes.nowhere/really.nowhere.html");

        assertEquals(text, getTextHelper(FileUtils.getFile(resourcesPath_, "emailTest.soy"), parameters));
    }

    @Test
    @DisplayName("Test getText for template NewCommentNotification.soy with missing parameters")
    void testGetTextForNewCommentNotificationSoyParameterMissing() {
        String text = "Dear Sir or Madam,\n" +
                "\n" +
                "A new comment has been added to the query \"A long blabla test query name string\".\n" +
                "Comment made is: \"null\"\n" +
                "To view or add comments, please use the following link:\n" +
                "\n" +
                "null\n" +
                "\n" +
                "Yours sincerely\n" +
                "The BBMRI Team" +
                "\n";

        HashMap<String, String> parameters = new HashMap();
        parameters.put("queryName", "A long blabla test query name string");

        assertEquals(text, getTextHelper(FileUtils.getFile(resourcesPath_, "NewCommentNotification.soy"), parameters));
    }

    @Test
    @DisplayName("Test getText for template NewCommentNotification.soy")
    void testGetTextForNewCommentNotificationSoy() {
        String text = "Dear Sir or Madam,\n" +
                "\n" +
                "A new comment has been added to the query \"A long blabla test query name string\".\n" +
                "Comment made is: \"Comment\"\n" +
                "To view or add comments, please use the following link:\n" +
                "\n" +
                "http://this.goes.nowhere/really.nowhere.html\n" +
                "\n" +
                "Yours sincerely\n" +
                "The BBMRI Team" +
                "\n";

        HashMap<String, String> parameters = new HashMap();
        parameters.put("queryName", "A long blabla test query name string");
        parameters.put("url", "http://this.goes.nowhere/really.nowhere.html");
        parameters.put("comment", "Comment");

        assertEquals(text, getTextHelper(FileUtils.getFile(resourcesPath_, "NewCommentNotification.soy"), parameters));
    }

    @Test
    @DisplayName("Test getText for template NewOfferNotification.soy")
    void testGetTextForNewOfferNotificationSoy() {
        String text = "Dear Sir or Madam,\n" +
                "\n" +
                "A biobank has shown samply availability for the query \"A long blabla test query name string\".\n" +
                "To start negotiating, please use the following link:\n" +
                "\n" +
                "http://this.goes.nowhere/really.nowhere.html\n" +
                "\n" +
                "Yours sincerely\n" +
                "The BBMRI Team" +
                "\n";

        HashMap<String, String> parameters = new HashMap();
        parameters.put("queryName", "A long blabla test query name string");
        parameters.put("url", "http://this.goes.nowhere/really.nowhere.html");

        assertEquals(text, getTextHelper(FileUtils.getFile(resourcesPath_, "NewOfferNotification.soy"), parameters));
    }

    @Test
    @DisplayName("Test getText for template NewQueryNotification.soy")
    void testGetTextForNewQueryNotificationSoy() {
        String text = "Dear Sir or Madam,\n" +
                "\n" +
                "A new query has been added.\n" +
                "The title of the query is: A long blabla test query name string\n" +
                "\n" +
                "To start negotiating, please use the following link: \n" +
                "\n" +
                "http://this.goes.nowhere/really.nowhere.html\n" +
                "\n" +
                "Yours sincerely\n" +
                "The BBMRI Team" +
                "\n";

        HashMap<String, String> parameters = new HashMap();
        parameters.put("queryName", "A long blabla test query name string");
        parameters.put("url", "http://this.goes.nowhere/really.nowhere.html");

        assertEquals(text, getTextHelper(FileUtils.getFile(resourcesPath_, "NewQueryNotification.soy"), parameters));
    }

    String getTextHelper(File fileEntry, HashMap<String, String> parameters) {
        System.out.println("Email-Soy: "+fileEntry.getName());
        System.out.println("================== BEGIN  ===================== ");

        EmailBuilder builder = new EmailBuilder(resourcesPath_.getAbsolutePath(),false);
        builder.addTemplateFile("main.soy", null);
        builder.addTemplateFile("Footer.soy", "Footer");
        builder.addTemplateFile(fileEntry.getName(), "Notification");

        String text = builder.getText(parameters);
        System.out.println(text);

        System.out.println("================== END ===================== ");
        return text;
    }
}
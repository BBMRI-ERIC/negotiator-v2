package de.samply.bbmri.mailing;

import com.google.common.io.CharSource;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * This class is used to generate email texts from a main template and
 * additional template files that contain "Google Closure Templates".
 * It passes the parameters that are set in an {@link OutgoingEmail} to the
 * templates.
 */
public class EmailBuilder {
    private final ArrayList<File> templateFiles;
    private final HashSet<String> delegatePackageNames;
    public static final String TEMPLATE_NAMESPACE = "samply.mailing";
    private final String templateFolder;

    private final boolean includeMainTemplate;

    private static final String MAIN_MAIL_TEMPLATE_FILENAME = "mailTemplate/MainMailTemplate.soy";

    /**
     * Creates a new {@code EmailBuilder} instance.
     * This constructor enforces that all the template files are in one folder.
     * @param templateFolder the folder containing all template files
     */
    public EmailBuilder(String templateFolder) {
        this(templateFolder, true);
    }

    /**
     * Creates a new {@code EmailBuilder} instance. This constructor enforces, that all template
     * files are in one folder. If the includeMainTemplate parameter is true, the main template
     * is included automatically.
     * @param templateFolder the folder containing all template files
     * @param includeMainTemplate If true, the main template is included automatically
     */
    public EmailBuilder(String templateFolder, boolean includeMainTemplate) {
        this.templateFolder = templateFolder;
        this.includeMainTemplate = includeMainTemplate;
        templateFiles = new ArrayList<>();
        delegatePackageNames = new HashSet<>();
    }

    /**
     * Adds an additional Soy file that is used for processing.
     * If such a file has the namespace {@link #TEMPLATE_NAMESPACE}, it can
     * override the templates "greeting",
     * "maincontent" and "footer" in the main mail template
     * to customize the greeting or set the content and footer of the mail.
     * The parameter {@link OutgoingEmail#LOCALE_KEY}
     * can be used for localization of the text in all templates.
     * The added file must be a
     * <a href="https://developers.google.com/closure/templates/">
     * "Google Closure Templates"</a>-template file.
     *
     * @param fileName Name or relative path of the template file, relative to
     * the template folder
     * @param delegatePackageName Delegate package name if the Soy file is
     * a delegate package (i. e. there is
     * {@code {delpackage delegatePackageName}} in its first line)
     */
    public void addTemplateFile(String fileName, String delegatePackageName) {
        addTemplateFile(new File(templateFolder, fileName), delegatePackageName);
    }

    /**
     * Adds an additional Soy file that is used for processing.
     * If such a file has the namespace {@link #TEMPLATE_NAMESPACE}, it can
     * override the templates "greeting",
     * "maincontent" and "footer" in the main mail template
     * to customize the greeting or set the content and footer of the mail.
     * The parameter {@link OutgoingEmail#LOCALE_KEY}
     * can be used for localization of the text in all templates.
     * The added file must be a
     * <a href="https://developers.google.com/closure/templates/">
     * "Google Closure Templates"</a>-template file.
     *
     * @param file the template file
     * @param delegatePackageName Delegate package name if the Soy file is
     * a delegate package (i. e. there is
     * {@code {delpackage delegatePackageName}} in its first line)
     */
    public void addTemplateFile(File file, String delegatePackageName) {
        templateFiles.add(file);
        if (delegatePackageName != null) {
            delegatePackageNames.add(delegatePackageName);
        }
    }

    /**
     * Constructs the text by using the main mail template and the additional
     * templates supplied by
     * {@link #addTemplateFile(java.lang.String, java.lang.String) }.
     * @param parameters parameters of mail
     * @return text for mail
     */
    public String getText(Map<String, String> parameters) {

        // Bundle the Soy files for your project into a SoyFileSet.
        SoyFileSet.Builder builder = SoyFileSet.builder();

        if(includeMainTemplate) {
            String mainTemplateJarPath = MAIN_MAIL_TEMPLATE_FILENAME;

            builder.add(new de.samply.bbmri.mailing.EmailBuilder.JarFileCharSource(mainTemplateJarPath),
                    getClass().getResource(mainTemplateJarPath).getPath());
        }

        for (File file : templateFiles) {
            builder.add(file);
        }

        SoyFileSet sfs = builder.build();

        // Compile the template into a SoyTofu object.
        SoyTofu tofu = sfs.compileToTofu();

        SoyTofu.Renderer renderer = tofu.newRenderer(TEMPLATE_NAMESPACE + ".mail");
        // For ease of use, delpackage names must be the same as the file names

        renderer.setActiveDelegatePackageNames(delegatePackageNames);
        renderer.setData(parameters);

        return renderer.render();
    }

    /**
     * A Character source for files packed in the jar.
     * This is needed because files cannot be accessed by path when they are
     * inside a jar file.
     */
    private class JarFileCharSource extends CharSource {

        private final String relativePath;

        /**
         * Creates a CharSource.
         * @param relativePath path to file in jar
         */
        JarFileCharSource(String relativePath) {
            this.relativePath = relativePath;
        }

        @Override
        public Reader openStream() throws IOException {
            return new InputStreamReader(getClass()
                    .getResourceAsStream(relativePath));
        }
    }
}

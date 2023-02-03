package eu.bbmri.eric.csit.service.negotiator.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static final Properties properties = new Properties();


    public static void loadProperties() {
        try (InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream("global.properties")) {

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        }
         catch (IOException ex) {
            ex.printStackTrace();
        }

}

    public static String getProperty(String prop){
        loadProperties();
        return properties.getProperty(prop);
    }

}

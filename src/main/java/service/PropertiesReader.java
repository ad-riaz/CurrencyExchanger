package service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    public static String getProperty(String key) {
        try {
            // Opening the input stream for application.properties file
            InputStream inputStream = PropertiesReader.class.getClassLoader()
                    .getResourceAsStream("application.properties");

            if (inputStream != null) {
                // Creation of Properties object
                Properties properties = new Properties();
                // Loading data from application.properties file
                properties.load(inputStream);
                // Closing input stream
                inputStream.close();

                // Returning value of property by key
                return properties.getProperty(key);
            } else {
                System.err.println("Файл application.properties не найден.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
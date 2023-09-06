package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropsReader {
//    private static String propsFilePath = "src/main/resources/application.properties";
//    FIXME: Fix the absolute path
    private static String propsFilePath = "/Users/alexr/IdeaProjects/CurrencyExchanger/src/main/resources/application.properties";
    private static Properties props = new Properties();

    static {
        try {
            props.load(new FileInputStream(propsFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String property) {
        return props.getProperty(property);
    }
}

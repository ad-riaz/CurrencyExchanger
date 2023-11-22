package service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    public static String getProperty(String key) {
        try {
            // Открытие потока чтения для файла application.properties
            InputStream inputStream = PropertiesReader.class.getClassLoader()
                    .getResourceAsStream("application.properties");

            if (inputStream != null) {
                // Создание объекта Properties
                Properties properties = new Properties();
                // Загрузка данных из файла application.properties
                properties.load(inputStream);
                // Закрытие потока чтения
                inputStream.close();

                // Возврат значения настройки по ключу
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
package service;

public class DbDriverLoader {
    private static final String driver = PropsReader.getProperty("dbDriver");

    public static void load() throws ClassNotFoundException {
        Class.forName(driver);
    }

}

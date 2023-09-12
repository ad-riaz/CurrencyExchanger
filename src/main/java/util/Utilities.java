package util;

import service.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Utilities {

    public static boolean areValidCurrenciesFields(String code, String name, String sign) {
//        FIXME: Reuse first two lines in a separate method
        return (code != null && name != null && sign != null &&
                !code.isEmpty() && !name.isEmpty() && !sign.isEmpty() &&
                code.length() <= 3 && name.length() <= 70 && sign.length() <= 3);
    }

    public static boolean areValidExchangeRatesFields(String baseCurrencyCode, String targetCurrencyCode, String rate) {
//        FIXME: Reuse first two lines in a separate method
        return (baseCurrencyCode != null && targetCurrencyCode != null && rate != null &&
                !baseCurrencyCode.isEmpty() && !targetCurrencyCode.isEmpty() && !rate.isEmpty() &&
                baseCurrencyCode.length() <= 3 && targetCurrencyCode.length() <= 3);
    }

    public static void deleteEntityById(String query, Long id, DatabaseManager dbManager) {
        try {
            dbManager.openConnection();
            Connection connection = dbManager.connection;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            dbManager.closeConnection();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

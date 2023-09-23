package util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import enums.ResponseMessage;
import model.Currency;
import service.DatabaseManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Collectors;

public class Utilities {

    public static boolean areValidCurrenciesFields(String code, String name, String sign) {
//        FIXME: Reuse first two lines in a separate method with StringUtils.isEmpty()
        return (code != null && name != null && sign != null &&
                !code.isEmpty() && !name.isEmpty() && !sign.isEmpty() &&
                code.length() <= 3 && name.length() <= 70 && sign.length() <= 3);
    }

    public static boolean areValidExchangeRatesFields(String baseCurrencyCode, String targetCurrencyCode, String rate) {
//        FIXME: Reuse first two lines in a separate method with StringUtils.isEmpty()
        return (baseCurrencyCode != null && targetCurrencyCode != null && rate != null &&
                !baseCurrencyCode.isEmpty() && !targetCurrencyCode.isEmpty() && !rate.isEmpty() &&
                baseCurrencyCode.length() <= 3 && targetCurrencyCode.length() <= 3);
    }

    public static void deleteEntityById(String query, Long id, DatabaseManager dbManager) {
        try {
            Connection connection = dbManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            dbManager.closeConnection(connection);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isStringDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String getParameterValue(String parameterKey, HttpServletRequest request) {
        try {
            Part part = request.getPart(parameterKey);
            return getStringFromInputStream(part.getInputStream());
        } catch (Exception e) {
//            FIXME: Fix sout
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static String getStringFromInputStream(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
//            FIXME: Fix sout
            System.out.println("Error closing input stream: " + e.getMessage());
            return null;
        }
    }

    public static String getExchangeErrorJson(ResponseMessage message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", message.getMessage());
        return new Gson().toJson(jsonObject);
    }
}

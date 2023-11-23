package util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import enums.ResponseMessage;
import service.DatabaseManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class Utilities {
    public static boolean areEmpty(String ... values) {
        boolean areEmpty = false;

        for (String value : values) {
            if (StringUtils.isBlank(value)) {
                areEmpty = true;
                break;
            }
        }

        return areEmpty;
    }

    public static boolean areValidCurrencyFields(String code, String name, String sign) {
        return (!areEmpty(code, name, sign) &&
                code.length() <= 3 &&
                name.length() <= 70 &&
                sign.length() <= 3
        );
    }

    public static boolean areValidExchangeRatesFields(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        return (
            !areEmpty(baseCurrencyCode, targetCurrencyCode, rate) &&
            baseCurrencyCode.length() <= 3 && 
            targetCurrencyCode.length() <= 3
        );
    }

    public static boolean isValidExchangeRatePath(String pathInfo) {
        return (!areEmpty(pathInfo) || !pathInfo.equals("/") || pathInfo.length() == 7);
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

    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String getParameterValue(String parameterKey, HttpServletRequest request) throws IOException, ServletException {
        Part part = request.getPart(parameterKey);
        return getStringFromInputStream(part.getInputStream());
    }

    public static String getStringFromInputStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
    
    public static String getExchangeErrorJson(ResponseMessage message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", message.getMessage());
        return new Gson().toJson(jsonObject);
    }
}

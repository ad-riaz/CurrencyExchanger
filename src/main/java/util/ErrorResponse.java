package util;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import enums.ResponseMessage;

public class ErrorResponse {
    
    public static void sendBadRequestStatus(HttpServletResponse response, String message) {
        try {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendNotFoundStatus(HttpServletResponse response, String message) {
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendConflictStatus(HttpServletResponse response, String message) {
        try {
            response.sendError(HttpServletResponse.SC_CONFLICT, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendInternalServerError(HttpServletResponse response, String message) {
        try {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void sendCurrencyCodeIsMissingError(HttpServletResponse reponse) {
        sendBadRequestStatus(reponse, ResponseMessage.CURRENCY_CODE_IS_MISSING.getMessage());
    }

    public static void sendCurrencyIsNotFoundInListError(HttpServletResponse response) {
        sendNotFoundStatus(response, ResponseMessage.CURRENCY_IS_NOT_FOUND_IN_DB.getMessage());
    }

    public static void sendCurrencyParametersAreNotValidError(HttpServletResponse response) {
        sendBadRequestStatus(response, ResponseMessage.CURRENCY_PARAMETERS_ARE_NOT_VALID.getMessage());
    }

    public static void sendCurrencyIsAlreadyExistsError(HttpServletResponse response) {
        sendConflictStatus(response, ResponseMessage.CURRENCY_IS_ALREADY_EXIST.getMessage());
    }



    public static void sendExchangeRateParametersAreNotValidError(HttpServletResponse response) {
        sendBadRequestStatus(response, ResponseMessage.EXCHANGE_RATE_PARAMETERS_ARE_NOT_VALID.getMessage());
    }

    public static void sendExchangeRateIsInListError(HttpServletResponse response) {
        sendConflictStatus(response, ResponseMessage.EXCHANGE_RATE_IS_ALREADY_IN_LIST.getMessage());
    }

    public static void sendInvalidPathError(HttpServletResponse response) {
        sendBadRequestStatus(response, ResponseMessage.EXCHANGE_RATE_CODES_ARE_MISSING.getMessage());
    }

    public static void sendExchangeRateNotFoundError(HttpServletResponse response) {
        sendNotFoundStatus(response, ResponseMessage.EXCHANGE_RATE_IS_NOT_FOUND.getMessage());
    }

    public static void sendExchangeRateIsEmptyError(HttpServletResponse response) {
        sendBadRequestStatus(response, ResponseMessage.EXCHANGE_RATE_IS_EMPTY.getMessage());
    }

    public static void sendExchangeRateIsNotANumberError(HttpServletResponse response) {
        sendBadRequestStatus(response, ResponseMessage.EXCHANGE_RATE_IS_NOT_A_NUMBER.getMessage());
    }
}

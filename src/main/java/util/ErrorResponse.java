package util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import enums.ResponseMessage;

public class ErrorResponse {
	private static final Gson gson = new Gson();
	
	private static void sendErrorResponseMessage(HttpServletResponse response, String message) {
		try {
			PrintWriter writer = response.getWriter();
			writer.print(getErrorResponseMessageJson(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	private static void sendErrorResponseMessage(HttpServletResponse response, ResponseMessage message) {
		sendErrorResponseMessage(response, message.getMessage());
	}
    
    private static String getErrorResponseMessageJson(String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", message);
        return gson.toJson(jsonObject);
    }
    
    
    
    public static void sendBadRequestStatus(HttpServletResponse response, ResponseMessage message) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		sendErrorResponseMessage(response, message);
    }
    
    public static void sendNotFoundStatus(HttpServletResponse response, ResponseMessage message) {
    	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		sendErrorResponseMessage(response, message);
    }
    
    public static void sendConflictStatus(HttpServletResponse response, ResponseMessage message) {
    	response.setStatus(HttpServletResponse.SC_CONFLICT);
		sendErrorResponseMessage(response, message);
    }

    public static void sendInternalServerError(HttpServletResponse response, String message) {
    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		sendErrorResponseMessage(response, message);
    }
    


    public static void sendCurrencyCodeIsMissingError(HttpServletResponse response) {
        sendBadRequestStatus(response, ResponseMessage.CURRENCY_CODE_IS_MISSING);
    }

    public static void sendCurrencyIsNotFoundInListError(HttpServletResponse response) {
        sendNotFoundStatus(response, ResponseMessage.CURRENCY_IS_NOT_FOUND_IN_DB);
    }

    public static void sendCurrencyParametersAreNotValidError(HttpServletResponse response) {
        sendBadRequestStatus(response, ResponseMessage.CURRENCY_PARAMETERS_ARE_NOT_VALID);
    }

    public static void sendCurrencyIsAlreadyExistsError(HttpServletResponse response) {
        sendConflictStatus(response, ResponseMessage.CURRENCY_IS_ALREADY_EXIST);
    }



    public static void sendExchangeRateParametersAreNotValidError(HttpServletResponse response) {
        sendBadRequestStatus(response, ResponseMessage.EXCHANGE_RATE_PARAMETERS_ARE_NOT_VALID);
    }

    public static void sendExchangeRateIsInListError(HttpServletResponse response) {
        sendConflictStatus(response, ResponseMessage.EXCHANGE_RATE_IS_ALREADY_IN_LIST);
    }

    public static void sendInvalidPathError(HttpServletResponse response) {
        sendBadRequestStatus(response, ResponseMessage.EXCHANGE_RATE_CODES_ARE_MISSING);
    }

	public static void sendExchangeRateNotFoundError(HttpServletResponse response) {
        sendNotFoundStatus(response, ResponseMessage.EXCHANGE_RATE_IS_NOT_FOUND);
    }

    public static void sendExchangeRateIsEmptyError(HttpServletResponse response) {
        sendBadRequestStatus(response, ResponseMessage.EXCHANGE_RATE_IS_EMPTY);
    }

    public static void sendExchangeRateIsNotANumberError(HttpServletResponse response) {
        sendBadRequestStatus(response, ResponseMessage.EXCHANGE_RATE_IS_NOT_A_NUMBER);
    }
    
    
    
    public static void sendExchangeCodeAreMissingError(HttpServletResponse response) {
    	sendBadRequestStatus(response, ResponseMessage.EXCHANGE_CODES_ARE_MISSING);
    }
    
    public static void sendExchangeAmountIsNotANumberError(HttpServletResponse response) {
    	sendBadRequestStatus(response, ResponseMessage.EXCHANGE_AMOUNT_IS_NOT_A_NUMBER);
    }
    
    public static void sendExchangeRateIsNotFoundError(HttpServletResponse response) {
    	sendNotFoundStatus(response, ResponseMessage.EXCHANGE_RATE_IS_NOT_FOUND);
    }
}

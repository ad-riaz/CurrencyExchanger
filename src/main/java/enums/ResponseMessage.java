package enums;

import com.google.gson.annotations.SerializedName;

public enum ResponseMessage {
    CURRENCY_CODE_IS_MISSING("The currency code is missing in the request. Please use the following mask '/CURRENCY_CODE'"),
    CURRENCY_IS_NOT_FOUND_IN_DB("The currency is not found in the database."),
    MESSAGE_CUR_PARAMETERS_ARE_INCORRECT("The request parameters are incorrect. Please do not use empty parameters or parameters that are too long. For example, \"code\": \"EUR\", \"name\": \"Euro\", \"sign\": \"€\"."),
    MESSAGE_CUR_IS_ALREADY_ADDED("The currency you are trying to add is already in the list. Please add a new currency."),
    MESSAGE_ER_PARAMETERS_ARE_INCORRECT("The request parameters are incorrect. Please do not use empty parameters or parameters that are too long. For example, \"baseCurrencyCode\": \"USD\", \"targetCurrencyCode\": \"EUR\", \"rate\": \"0.99\"."),
    MESSAGE_ER_IS_ALREADY_ADDED("The exchange rate you are trying to add is already in the list. Please add a new exchange rate."),
    MESSAGE_ER_IS_NOT_FOUND("The exchange rate is not found."),
    MESSAGE_ER_IS_NOT_A_NUMBER("Exchange rate is not a number. Example: 0.99"),
    MESSAGE_ER_IS_EMPTY("Exchange rate is empty"),
    MESSAGE_ER_CODES_ARE_NOT_EXIST("Currency codes are missing in the address. Please use the following mask '/CODE1CODE2'"),
    MESSAGE_AMOUNT_IS_NOT_A_NUMBER("Amount is not a number");

    @SerializedName("message")
    private String message;
    ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}

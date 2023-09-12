package enums;

public enum ResponseMessage {
    MESSAGE_CUR_PARAMETERS_ARE_INCORRECT("The request parameters are incorrect. Please do not use empty parameters or parameters that are too long. For example, \"code\": \"EUR\", \"name\": \"Euro\", \"sign\": \"€\"."),
    MESSAGE_ER_PARAMETERS_ARE_INCORRECT("The request parameters are incorrect. Please do not use empty parameters or parameters that are too long. For example, \"baseCurrencyCode\": \"USD\", \"targetCurrencyCode\": \"EUR\", \"rate\": \"0.99\"."),
    MESSAGE_CURRENCY_IS_ALREADY_ADDED("The currency you are trying to add is already in the list. Please add a new currency."),
    MESSAGE_ER_IS_ALREADY_ADDED("The exchange rate you are trying to add is already in the list. Please add a new exchange rate."),
    MESSAGE_CURRENCY_IS_NOT_FOUND("The currency is not found."),
    MESSAGE_CODE_IS_MISSING("The currency code is missing in the request address. Please use the following mask '/CURRENCY_CODE'");

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

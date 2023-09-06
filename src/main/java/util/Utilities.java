package util;

public class Utilities {

    public static boolean areValidCurrenciesFields(String code, String name, String sign) {
        return (code != null && name != null && sign != null &&
                !code.isEmpty() && !name.isEmpty() && !sign.isEmpty() &&
                code.length() <= 3 && name.length() <= 70 && sign.length() <= 3);
    }
}

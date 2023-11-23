package servlets;

import com.google.gson.Gson;
import enums.ResponseMessage;
import model.Currency;
import model.Exchange;
import model.ExchangeRate;
import repository.CurrencyRepo;
import repository.ExchangeRatesRepo;
import util.Utilities;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

@WebServlet(name = "ExchangeServlet", value = "/exchange")
public class ExchangeServlet extends HttpServlet {

    private ExchangeRatesRepo exchangeRatesRepository;
    private CurrencyRepo currencyRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        exchangeRatesRepository = ExchangeRatesRepo.getInstance();
        currencyRepository = CurrencyRepo.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fromParameter = request.getParameter("from").toUpperCase();
        String toParameter = request.getParameter("to").toUpperCase();
        String amountParameter = request.getParameter("amount").toUpperCase();
        BigDecimal amount;
        BigDecimal convertedAmount;

        // If parameters are null or empty then send error message
        if (StringUtils.isEmpty(fromParameter) ||
            StringUtils.isEmpty(toParameter)) {
            sendExchangeResponseMessage(response, ResponseMessage.CURRENCY_IS_NOT_FOUND_IN_DB);
            return;
        }

        if (StringUtils.isEmpty(amountParameter) && !Utilities.isDouble(amountParameter)) {
            sendExchangeResponseMessage(response, ResponseMessage.MESSAGE_AMOUNT_IS_NOT_A_NUMBER);
            return;
        }

        // If currencies are not available then send error message
        if (!areCurrenciesValid(fromParameter, toParameter)) {
            sendExchangeResponseMessage(response, ResponseMessage.CURRENCY_IS_NOT_FOUND_IN_DB);
            return;
        }

        // Get exchange rate from the repository using a pair of codes.
        // If there is no exchange rate for this pair of codes,
        // then it tries to find the exchange rate for the reverse order of the codes in the pair.
        // If there are no any exchange rates for this pair of code then it returns 0.
        BigDecimal rate = getExchangeRate(fromParameter, toParameter);

        if (rate == null) {
            sendExchangeResponseMessage(response, ResponseMessage.EXCHANGE_RATE_IS_NOT_FOUND);
            return;
        }

        amount = new BigDecimal(amountParameter).setScale(2, RoundingMode.DOWN);
        convertedAmount = amount.multiply(rate).setScale(3, RoundingMode.DOWN);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(new Gson().toJson(getExchangeObject(fromParameter, toParameter, rate, amount, convertedAmount)));
    }

    private void sendExchangeResponseMessage(HttpServletResponse response, ResponseMessage message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(Utilities.getExchangeErrorJson(message));
    }

    private BigDecimal getExchangeRate(String baseCode, String targetCode) {
        BigDecimal rate;
        Optional<ExchangeRate> exchangeRate = exchangeRatesRepository.findByCodes(baseCode, targetCode);
        if (exchangeRate.isPresent()) return exchangeRate.get().getRate();

        Optional<ExchangeRate> reverseExchangeRate = exchangeRatesRepository.findByCodes(targetCode, baseCode);
        if (reverseExchangeRate.isPresent()) {
            return new BigDecimal("1").divide(reverseExchangeRate.get().getRate(), 3, RoundingMode.DOWN);
        }

        Optional<ExchangeRate> exchangeRateUSD_A = exchangeRatesRepository.findByCodes("USD", baseCode);
        Optional<ExchangeRate> exchangeRateUSD_B = exchangeRatesRepository.findByCodes("USD", targetCode);

        if (exchangeRateUSD_A.isPresent() && exchangeRateUSD_B.isPresent()) {
            return exchangeRateUSD_B.get().getRate().divide(exchangeRateUSD_A.get().getRate(), 3, RoundingMode.DOWN);
        }

        return null;
    }

    private boolean areCurrenciesValid(String from, String to) {
        Optional<Currency> baseCurrency = currencyRepository.findByCode(from);
        Optional<Currency> targetCurrency = currencyRepository.findByCode(to);

        return (baseCurrency.isPresent() && targetCurrency.isPresent());
    }

    private Exchange getExchangeObject(String fromCode, String toCode, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        return new Exchange(
                currencyRepository.findByCode(fromCode).get(),
                currencyRepository.findByCode(toCode).get(),
                rate,
                amount,
                convertedAmount
        );
    }
}

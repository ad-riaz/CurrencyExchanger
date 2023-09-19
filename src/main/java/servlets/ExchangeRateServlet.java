package servlets;

import com.google.gson.GsonBuilder;
import enums.ResponseMessage;
import model.ExchangeRate;
import repository.CurrencyRepository;
import repository.ExchangeRatesRepository;
import util.Utilities;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Optional;

@MultipartConfig
@WebServlet(name = "ExchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private ExchangeRatesRepository exchangeRatesRepository;
    private CurrencyRepository currencyRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        exchangeRatesRepository = ExchangeRatesRepository.getInstance();
        currencyRepository = CurrencyRepository.getInstance();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH"))
            doPatch(req, resp);
        else
            super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = extractPathInfo(request);

        if (isInvalidPath(pathInfo)) {
            sendInvalidPathError(response);
            return;
        }

        StringBuilder path = new StringBuilder(pathInfo.replaceFirst("/", "").toUpperCase());
        String baseCurrency = path.substring(0, 3);
        String targetCurrency = path.substring(3, 6);

        if (!currencyRepository.findByCode(baseCurrency).isPresent()
                || !currencyRepository.findByCode(targetCurrency).isPresent()) {
            sendCurrencyNotFoundError(response);
            return;
        }

        Optional<ExchangeRate> exchangeRate = exchangeRatesRepository.findByBaseCodeAndTargetCode(baseCurrency, targetCurrency);

        if (!exchangeRate.isPresent()) {
            sendExchangeRateNotFoundError(response);
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(new GsonBuilder().create().toJson(exchangeRate.get()));
        writer.close();
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String rate = Utilities.getParameterValue("rate", request);

        if (rate == null || rate.isEmpty()) {
            sendExchangeRateIsEmptyError(response);
            return;
        }

        if (!Utilities.isStringDouble(rate)) {
            sendExchangeRateIsNotANumberError(response);
            return;
        }

        String pathInfo = extractPathInfo(request);

        if (isInvalidPath(pathInfo)) {
            sendInvalidPathError(response);
            return;
        }

        StringBuilder path = new StringBuilder(pathInfo.replaceFirst("/", "").toUpperCase());
        String baseCurrency = path.substring(0, 3);
        String targetCurrency = path.substring(3, 6);

        if (!currencyRepository.findByCode(baseCurrency).isPresent()
                || !currencyRepository.findByCode(targetCurrency).isPresent()) {
            sendCurrencyNotFoundError(response);
            return;
        }

        Optional<ExchangeRate> exchangeRate = exchangeRatesRepository.findByBaseCodeAndTargetCode(baseCurrency, targetCurrency);

        if (!exchangeRate.isPresent()) {
            sendExchangeRateNotFoundError(response);
            return;
        }

        try {
            BigDecimal rateNumber = new BigDecimal(rate);
            exchangeRate.get().setRate(rateNumber);
            exchangeRatesRepository.update(exchangeRate.get());
            doGet(request, response);
        } catch (NumberFormatException e) {
            System.out.println("Error: Could not parse the string into a number: " + e.getMessage());
        }
    }

    private void sendBadRequest(HttpServletResponse response, String message) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
    }

    private void sendNotFound(HttpServletResponse response, String message) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, message);
    }

    private void sendInvalidPathError(HttpServletResponse response) throws IOException {
        sendBadRequest(response, ResponseMessage.MESSAGE_ER_CODES_ARE_NOT_EXIST.getMessage());
    }

    private void sendCurrencyNotFoundError(HttpServletResponse response) throws IOException {
        sendBadRequest(response, ResponseMessage.MESSAGE_CUR_IS_NOT_FOUND.getMessage());
    }

    private void sendExchangeRateNotFoundError(HttpServletResponse response) throws IOException {
        sendNotFound(response, ResponseMessage.MESSAGE_ER_IS_NOT_FOUND.getMessage());
    }

    private void sendExchangeRateIsEmptyError(HttpServletResponse response) throws IOException {
        sendBadRequest(response, ResponseMessage.MESSAGE_ER_IS_EMPTY.getMessage());
    }

    private void sendExchangeRateIsNotANumberError(HttpServletResponse response) throws IOException {
        sendBadRequest(response, ResponseMessage.MESSAGE_ER_IS_NOT_A_NUMBER.getMessage());
    }

    private String extractPathInfo(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        return (pathInfo != null) ? pathInfo : "";
    }

    private boolean isInvalidPath(String pathInfo) {
        return (pathInfo.isEmpty() || pathInfo.equals("/") || pathInfo.length() != 7);
    }
}

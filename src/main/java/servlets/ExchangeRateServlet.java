package servlets;

import com.google.gson.GsonBuilder;
import model.ExchangeRate;
import repository.CurrencyRepo;
import repository.CurrencyRepository;
import repository.ExchangeRatesRepo;
import repository.ExchangeRatesRepository;
import service.JsonParser;
import util.ErrorResponse;
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
        exchangeRatesRepository = ExchangeRatesRepo.getInstance();
        currencyRepository = CurrencyRepo.getInstance();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH"))
            doPatch(req, resp);
        else
            super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String pathInfo = request.getPathInfo();

            if (!Utilities.isValidExchangeRatePath(pathInfo)) {
                ErrorResponse.sendInvalidPathError(response);
                return;
            }

            StringBuilder path = new StringBuilder(pathInfo.replaceFirst("/", "").toUpperCase());
            String baseCurrency = path.substring(0, 3);
            String targetCurrency = path.substring(3, 6);

            if (!currencyRepository.findByCode(baseCurrency).isPresent() ||
                !currencyRepository.findByCode(targetCurrency).isPresent()) {
                ErrorResponse.sendCurrencyIsNotFoundInListError(response);
                return;
            }

            Optional<ExchangeRate> exchangeRate = exchangeRatesRepository.findByCodes(baseCurrency, targetCurrency);

            if (!exchangeRate.isPresent()) {
                ErrorResponse.sendExchangeRateNotFoundError(response);
                return;
            }
            
            PrintWriter writer = response.getWriter();
            
            writer.print(JsonParser.toJson(exchangeRate.get()));
            writer.close();
        } catch (Exception e) {
            ErrorResponse.sendInternalServerError(response, e.getMessage());
            return;
        }
    }

    protected void doPatch(HttpServletRequest request, HttpServletResponse response) {
        try {
            String rate = Utilities.getParameterValue("rate", request);

            if (Utilities.areEmpty(rate)) {
                ErrorResponse.sendExchangeRateIsEmptyError(response);
                return;
            }

            if (!Utilities.isDouble(rate)) {
                ErrorResponse.sendExchangeRateIsNotANumberError(response);
                return;
            }

            String pathInfo = request.getPathInfo();

            if (!Utilities.isValidExchangeRatePath(pathInfo)) {
                ErrorResponse.sendInvalidPathError(response);
                return;
            }

            StringBuilder path = new StringBuilder(pathInfo.replaceFirst("/", "").toUpperCase());
            String baseCurrency = path.substring(0, 3);
            String targetCurrency = path.substring(3, 6);

            if (!currencyRepository.findByCode(baseCurrency).isPresent() ||
                !currencyRepository.findByCode(targetCurrency).isPresent()) {
                ErrorResponse.sendCurrencyIsNotFoundInListError(response);
                return;
            }

            Optional<ExchangeRate> exchangeRate = exchangeRatesRepository.findByCodes(baseCurrency, targetCurrency);

            if (!exchangeRate.isPresent()) {
                ErrorResponse.sendExchangeRateNotFoundError(response);
                return;
            }

            try {
                exchangeRate.get().setRate(new BigDecimal(rate));
                exchangeRatesRepository.update(exchangeRate.get());
                doGet(request, response);
            } catch (NumberFormatException e) {
                ErrorResponse.sendExchangeRateIsNotANumberError(response);
                return;
            }
        } catch (Exception e) {
            ErrorResponse.sendInternalServerError(response, e.getMessage());
            return;
        }
    }
}

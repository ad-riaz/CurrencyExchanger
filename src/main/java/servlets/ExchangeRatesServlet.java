package servlets;

import com.google.gson.GsonBuilder;
import model.Currency;
import model.ExchangeRate;
import repository.CurrencyRepo;
import repository.CurrencyRepository;
import repository.ExchangeRatesRepo;
import repository.ExchangeRatesRepository;
import util.ErrorResponse;
import util.Utilities;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "ExchangeRatesServlet", value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private ExchangeRatesRepository exchangeRatesRepository;
    private CurrencyRepository currencyRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        exchangeRatesRepository = ExchangeRatesRepo.getInstance();
        currencyRepository = CurrencyRepo.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        List<ExchangeRate> exchangeRates = exchangeRatesRepository.findAll();

        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
        
            writer.print(new GsonBuilder().create().toJson(exchangeRates));
            writer.close();
        } catch (IOException e) {
            ErrorResponse.sendInternalServerError(response, e.getMessage());
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String baseCurrencyCode = request.getParameter("baseCurrencyCode").toUpperCase();
        String targetCurrencyCode = request.getParameter("targetCurrencyCode").toUpperCase();
        String rate = request.getParameter("rate");

        if (!Utilities.areValidExchangeRatesFields(baseCurrencyCode, targetCurrencyCode, rate)) {
            ErrorResponse.sendExchangeRateParametersAreNotValidError(response);
            return;
        }

        if (!Utilities.isDouble(rate)) {
            ErrorResponse.sendExchangeRateIsNotANumberError(response);
            return;
        }

        if (exchangeRatesRepository.findByCodes(baseCurrencyCode, targetCurrencyCode).isPresent()) {
            ErrorResponse.sendExchangeRateIsInListError(response);
            return;
        }

        Optional<Currency> baseCurrency = currencyRepository.findByCode(baseCurrencyCode);
        Optional<Currency> targetCurrency = currencyRepository.findByCode(targetCurrencyCode);

        if (!baseCurrency.isPresent() || !targetCurrency.isPresent()) {
            ErrorResponse.sendCurrencyIsNotFoundInListError(response);
            return;
        }

        ExchangeRate exchangeRate = new ExchangeRate(baseCurrency.get(), targetCurrency.get(), new BigDecimal(rate));
        exchangeRatesRepository.save(exchangeRate);
        doGet(request, response);
    }
}

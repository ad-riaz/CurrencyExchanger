package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.ResponseMessage;
import model.Currency;
import model.ExchangeRate;
import repository.CurrencyRepository;
import repository.ExchangeRatesRepository;
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
        exchangeRatesRepository = ExchangeRatesRepository.getInstance();
        currencyRepository = CurrencyRepository.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<ExchangeRate> exchangeRates = exchangeRatesRepository.findAll();
        Gson gson = new GsonBuilder().create();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(gson.toJson(exchangeRates));
        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rate = request.getParameter("rate");

        if (!Utilities.areValidExchangeRatesFields(baseCurrencyCode, targetCurrencyCode, rate)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ResponseMessage.MESSAGE_ER_PARAMETERS_ARE_INCORRECT.getMessage());
            return;
        }

        if (exchangeRatesRepository.findByBaseCodeAndTargetCode(baseCurrencyCode, targetCurrencyCode).isPresent()) {
            response.sendError(HttpServletResponse.SC_CONFLICT, ResponseMessage.MESSAGE_ER_IS_ALREADY_ADDED.getMessage());
            return;
        }

        Optional<Currency> baseCurrency = currencyRepository.findByCode(baseCurrencyCode);
        Optional<Currency> targetCurrency = currencyRepository.findByCode(targetCurrencyCode);

        if (!baseCurrency.isPresent() || !targetCurrency.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ResponseMessage.MESSAGE_CURRENCY_IS_NOT_FOUND.getMessage());
            return;
        }

        ExchangeRate exchangeRate = new ExchangeRate(baseCurrency.get(), targetCurrency.get(), new BigDecimal(rate));
        exchangeRatesRepository.save(exchangeRate);
        doGet(request, response);
    }
}

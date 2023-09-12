package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.ResponseMessage;
import model.Currency;
import repository.CurrencyRepository;
import util.Utilities;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyRepository repository;

    @Override
    public void init() throws ServletException {
        super.init();
        repository = CurrencyRepository.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Currency> currencies = repository.findAll();
        Gson gson = new GsonBuilder().create();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        writer.print(gson.toJson(currencies));
        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String sign = request.getParameter("sign");

        if (!Utilities.areValidCurrenciesFields(code, name, sign)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ResponseMessage.MESSAGE_CUR_PARAMETERS_ARE_INCORRECT.getMessage());
            return;
        }

        if (repository.findByCode(code).isPresent()) {
            response.sendError(HttpServletResponse.SC_CONFLICT, ResponseMessage.MESSAGE_CURRENCY_IS_ALREADY_ADDED.getMessage());
            return;
        }

        repository.save(new Currency(code, name, sign));
        doGet(request, response);
    }
}

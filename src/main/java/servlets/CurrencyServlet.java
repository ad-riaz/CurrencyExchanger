package servlets;

import com.google.gson.GsonBuilder;
import enums.ResponseMessage;
import model.Currency;
import repository.CurrencyRepository;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private CurrencyRepository repository;

    @Override
    public void init(ServletConfig config) {
        repository = CurrencyRepository.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getPathInfo().toUpperCase();

        if (code == null || code.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ResponseMessage.CURRENCY_CODE_IS_MISSING.getMessage());
            return;
        }

        Optional<Currency> currency = repository.findByCode(code.replace("/", ""));
        
        if (!currency.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ResponseMessage.CURRENCY_IS_NOT_FOUND_IN_DB.getMessage());
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        writer.print(new GsonBuilder().create().toJson(currency.get()));
        writer.close();
    }
}

package servlets;

import com.google.gson.GsonBuilder;
import model.Currency;
import repository.CurrencyRepo;
import repository.CurrencyRepository;
import util.ErrorResponse;

import javax.servlet.ServletConfig;
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
        repository = CurrencyRepo.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = request.getPathInfo().toUpperCase();

            if (code == null || code.equals("/")) {
                ErrorResponse.sendCurrencyCodeIsMissingError(response);
                return;
            }

            Optional<Currency> currency = repository.findByCode(code.replace("/", ""));
            
            if (!currency.isPresent()) {
                ErrorResponse.sendCurrencyIsNotFoundInListError(response);
                return;
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();

            writer.print(new GsonBuilder().create().toJson(currency.get()));
            writer.close();
        } catch (Exception e) {
            ErrorResponse.sendInternalServerError(response, e.getMessage());
            return;
        }
    }
}

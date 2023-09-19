package servlets;

import com.google.gson.Gson;
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ResponseMessage.MESSAGE_CUR_CODE_IS_MISSING.getMessage());
            return;
        }

        Optional<Currency> currency = repository.findByCode(code.replace("/", ""));
        if (!currency.isPresent()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ResponseMessage.MESSAGE_CUR_IS_NOT_FOUND.getMessage());
            return;
        }

        Gson gson = new GsonBuilder().create();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        writer.print(gson.toJson(currency.get()));
        writer.close();
    }
}

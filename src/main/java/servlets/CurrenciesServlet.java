package servlets;

import com.google.gson.GsonBuilder;
import model.Currency;
import repository.CurrencyRepo;
import repository.CurrencyRepository;
import util.ErrorResponse;
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
        repository = CurrencyRepo.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
        	List<Currency> currencies = repository.findAll();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();

            writer.print(new GsonBuilder().create().toJson(currencies));
            writer.close();
        } catch (Exception e) {
            ErrorResponse.sendInternalServerError(response, e.getMessage());
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    	try {
    		String code = request.getParameter("code").toUpperCase();
    		String name = request.getParameter("name").toUpperCase();
	        String sign = request.getParameter("sign").toUpperCase();
	
	        if (!Utilities.areValidCurrencyFields(code, name, sign)) {
	            ErrorResponse.sendCurrencyParametersAreNotValidError(response);
	            return;
	        }

        	repository.save(new Currency(code, name, sign));
        } catch (Exception e) {
        	ErrorResponse.sendInternalServerError(response, e.getMessage());
        	return;
        }
        
        doGet(request, response);
    }
}

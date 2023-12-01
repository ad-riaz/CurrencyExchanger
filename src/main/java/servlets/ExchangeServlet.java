package servlets;

import com.google.gson.Gson;
import model.Currency;
import model.Exchange;
import model.ExchangeRate;
import repository.CurrencyRepo;
import repository.ExchangeRatesRepo;
import util.ErrorResponse;
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
        try {
        	String fromParameter = request.getParameter("from").toUpperCase();
	        String toParameter = request.getParameter("to").toUpperCase();
	        String amountParameter = request.getParameter("amount").toUpperCase();
	        BigDecimal amount;
	        BigDecimal convertedAmount;
	
	        // If parameters are null or empty then send error message
	        if (Utilities.areEmpty(fromParameter, toParameter)) {
	        	ErrorResponse.sendExchangeCodeAreMissingError(response);
	            return;
	        }
	
	        // If amount is empty or if it is not a number
	        if (StringUtils.isEmpty(amountParameter) || !Utilities.isDouble(amountParameter)) {
	        	ErrorResponse.sendExchangeAmountIsNotANumberError(response);
	            return;
	        }
	        
	        Optional<Currency> baseCurrency = currencyRepository.findByCode(fromParameter);
	        Optional<Currency> targetCurrency = currencyRepository.findByCode(toParameter);
	
	        // If currencies are not available in the DB then send error message
	        if (!baseCurrency.isPresent() || !targetCurrency.isPresent()) {
	        	ErrorResponse.sendCurrencyIsNotFoundInListError(response);
	            return;
	        }
	
	        // Get exchange rate from the repository using a pair of codes.
	        // If there is no exchange rate for this pair of codes,
	        // then it tries to find the exchange rate for the reverse order of the codes in the pair.
	        // If there are no any exchange rates for this pair of code then it returns null.
	        Optional<BigDecimal> rate = getExchangeRate(fromParameter, toParameter);
	
	        // If exchange rate is not found send error message
	        if (rate.isEmpty()) {
	        	ErrorResponse.sendExchangeRateIsNotFoundError(response);
	            return;
	        }
	
	        amount = new BigDecimal(amountParameter).setScale(2, RoundingMode.DOWN);
	        convertedAmount = roundAccordingToTrashold(
	        		amount.multiply(rate.get()).setScale(5, RoundingMode.DOWN)
	        );
	
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        PrintWriter writer = response.getWriter();
	        
	        writer.print(new Gson().toJson(getExchangeObject(
	        												baseCurrency, 
	        												targetCurrency, 
	        												rate.get(), 
	        												amount, 
	        												convertedAmount))
	        );
        } catch (Exception e) {
            ErrorResponse.sendInternalServerError(response, e.getMessage() + "\n" + e.toString());
            return;
        }
    }

    private Optional<BigDecimal> getExchangeRate(String baseCode, String targetCode) throws Exception {
        BigDecimal rate = null;
        
        Optional<ExchangeRate> exchangeRate = exchangeRatesRepository.findByCodes(baseCode, targetCode);
        
        if (exchangeRate.isPresent()) {
        	rate = exchangeRate.get().getRate();
        } else {
        	Optional<ExchangeRate> reverseExchangeRate = exchangeRatesRepository.findByCodes(targetCode, baseCode);
            
        	if (reverseExchangeRate.isPresent()) {
                rate = new BigDecimal("1").divide(reverseExchangeRate.get().getRate(), 5, RoundingMode.DOWN);
            } else {
            	Optional<ExchangeRate> exchangeRateUSD_base = exchangeRatesRepository.findByCodes("USD", baseCode);
                Optional<ExchangeRate> exchangeRateUSD_target = exchangeRatesRepository.findByCodes("USD", targetCode);

                if (exchangeRateUSD_base.isPresent() && exchangeRateUSD_target.isPresent()) {
                    rate = exchangeRateUSD_target.get().getRate()
                    		.divide(exchangeRateUSD_base.get().getRate(), 5, RoundingMode.DOWN);
                }
            }            
        }
 
        return Optional.ofNullable(roundAccordingToTrashold(rate));
    }
    
    private BigDecimal roundAccordingToTrashold(BigDecimal number) {
    	BigDecimal firstTrashold = new BigDecimal("0.1");
    	BigDecimal secondTrashold = new BigDecimal("0.01");
    	
    	if (number == null) {
    		return null;
    	}
    	
    	if (number.compareTo(firstTrashold) < 0 &&
    		number.compareTo(secondTrashold) > 0) {
    		return number.setScale(3, RoundingMode.DOWN);
    	} else if (number.compareTo(secondTrashold) <= 0) {
    		return number.setScale(4, RoundingMode.DOWN);
    	} else {
    		return number.setScale(2, RoundingMode.DOWN);
    	}
    }

    private Exchange getExchangeObject(Optional<Currency> baseCurrency, Optional<Currency> targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        return new Exchange(
                baseCurrency.get(),
                targetCurrency.get(),
                rate,
                amount,
                convertedAmount
        );
    }
}

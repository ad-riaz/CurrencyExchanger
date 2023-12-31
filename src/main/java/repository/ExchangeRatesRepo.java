package repository;

import model.ExchangeRate;
import service.DatabaseManager;
import util.Utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesRepo implements ExchangeRatesRepository {
    private static ExchangeRatesRepo instance;
    private final CurrencyRepo currencyRepository;
    private final DatabaseManager dbManager;

    private ExchangeRatesRepo() {
        dbManager = DatabaseManager.getInstance();
        currencyRepository = CurrencyRepo.getInstance();
    }

    public static ExchangeRatesRepo getInstance() {
        if (instance == null) {
            synchronized (ExchangeRatesRepo.class) {
                if (instance == null) {
                    instance = new ExchangeRatesRepo();
                }
            }
        }
        return instance;
    }

    @Override
    public void save(ExchangeRate entity) throws Exception{
        String query = "INSERT INTO ExchangeRates VALUES (NULL, ?, ?, ?)";

        Connection connection = dbManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, entity.getBaseCurrency().getId());
        statement.setLong(2, entity.getTargetCurrency().getId());
        statement.setBigDecimal(3, entity.getRate());
        statement.executeUpdate();
        dbManager.closeConnection(connection);
    }

    @Override
    public Optional<ExchangeRate> findById(Long id) throws Exception {
        String query = "SELECT * FROM ExchangeRates WHERE id = ?";
        Optional<ExchangeRate> entity = Optional.ofNullable(null);

        Connection connection = dbManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            entity = Optional.ofNullable(createNewExchangeRate(resultSet));
        }

        dbManager.closeConnection(connection);

        return entity;
    }

    @Override
    public Optional<ExchangeRate> findByCodes(String baseCode, String targetCode) throws Exception {
        Optional<ExchangeRate> entity = Optional.ofNullable(null);
        String query = "SELECT" +
                    "    er.id AS id," +
                    "    er.BaseCurrencyId AS BaseCurrencyId," +
                    "    er.TargetCurrencyId AS TargetCurrencyId," +
                    "    er.rate AS rate" +
                    "   FROM Currencies c" +
                    "         JOIN ExchangeRates er" +
                    "              ON c.id = er.BaseCurrencyId" +
                    "         JOIN currencies c2" +
                    "              ON er.TargetCurrencyId = c2.id" +
                    "   WHERE c.code = ? AND c2.code = ?";

        Connection connection = dbManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, baseCode);
        preparedStatement.setString(2, targetCode);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setId(resultSet.getLong("id"));
            exchangeRate.setRate(resultSet.getBigDecimal("rate"));
            exchangeRate.setBaseCurrency(currencyRepository.findById(resultSet.getLong("BaseCurrencyId")).get());
            exchangeRate.setTargetCurrency(currencyRepository.findById(resultSet.getLong("TargetCurrencyId")).get());

            entity = Optional.ofNullable(exchangeRate);
        }
        dbManager.closeConnection(connection);

        return entity;
    }

    @Override
    public List<ExchangeRate> findAll() throws Exception {
        List<ExchangeRate> rates = new ArrayList<>();
        String query = "SELECT * FROM ExchangeRates";

        Connection connection = dbManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
        	rates.add(createNewExchangeRate(resultSet));
        }
        
        resultSet.close();
        dbManager.closeConnection(connection);
        
        return rates;
    }

    @Override
    public void update(ExchangeRate entity) throws Exception {
        String query = "UPDATE ExchangeRates SET BaseCurrencyId = ?, TargetCurrencyId = ?, rate = ? where id = ?";

        Connection connection = dbManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, entity.getBaseCurrency().getId());
        statement.setLong(2, entity.getTargetCurrency().getId());
        statement.setBigDecimal(3, entity.getRate());
        statement.setLong(4, entity.getId());
        statement.executeUpdate();
        dbManager.closeConnection(connection);
    }

    @Override
    public void delete(Long id) throws Exception {
        String query = "DELETE FROM ExchangeRates WHERE id = ?";
        Utilities.deleteEntityById(query, id, dbManager);
    }
    
    private ExchangeRate createNewExchangeRate(ResultSet set) throws Exception {
    	return new ExchangeRate(
                set.getLong("id"),
                currencyRepository.findById(set.getLong("baseCurrencyId")).get(),
                currencyRepository.findById(set.getLong("targetCurrencyId")).get(),
                set.getBigDecimal("rate")
        );
    }
}

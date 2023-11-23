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
    public void save(ExchangeRate entity) {
        String query = "INSERT INTO ExchangeRates VALUES (NULL, ?, ?, ?)";

        try {
            Connection connection = dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, entity.getBaseCurrency().getId());
            statement.setLong(2, entity.getTargetCurrency().getId());
            statement.setBigDecimal(3, entity.getRate());
            statement.executeUpdate();
            dbManager.closeConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRate> findById(Long id) {
        String query = "SELECT * FROM ExchangeRates WHERE id = ?";
        Optional<ExchangeRate> entity = Optional.ofNullable(null);

        try {
            Connection connection = dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
//                        FIXME: Move into separate method
                ExchangeRate exchangeRate = new ExchangeRate(
                        resultSet.getLong("id"),
                        currencyRepository.findById(resultSet.getLong("baseCurrencyId")).get(),
                        currencyRepository.findById(resultSet.getLong("targetCurrencyId")).get(),
                        resultSet.getBigDecimal("rate")
                );

                entity = Optional.ofNullable(exchangeRate);
            }

            dbManager.closeConnection(connection);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }

        return entity;
    }

    @Override
    public Optional<ExchangeRate> findByCodes(String baseCode, String targetCode) {
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

        try {
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
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }

        return entity;
    }

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> rates = new ArrayList<>();
        String query = "SELECT * FROM ExchangeRates";

        try {
            Connection connection = dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ExchangeRate exchangeRate = new ExchangeRate(
//                        FIXME: Move into separate method
                        resultSet.getLong("id"),
                        currencyRepository.findById(resultSet.getLong("baseCurrencyId")).get(),
                        currencyRepository.findById(resultSet.getLong("targetCurrencyId")).get(),
                        resultSet.getBigDecimal("rate")
                );
                rates.add(exchangeRate);
            }
            resultSet.close();
            dbManager.closeConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rates;
    }

    @Override
    public void update(ExchangeRate entity) {
        String query = "UPDATE ExchangeRates SET BaseCurrencyId = ?, TargetCurrencyId = ?, rate = ? where id = ?";

        try {
            Connection connection = dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, entity.getBaseCurrency().getId());
            statement.setLong(2, entity.getTargetCurrency().getId());
            statement.setBigDecimal(3, entity.getRate());
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
            dbManager.closeConnection(connection);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM ExchangeRates WHERE id = ?";
        Utilities.deleteEntityById(query, id, dbManager);
    }
}

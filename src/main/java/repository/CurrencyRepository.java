package repository;

import model.Currency;
import service.DatabaseManager;
import util.Utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyRepository implements CrudRepository<Currency> {
    private static CurrencyRepository instance;
    private final DatabaseManager dbManager;

    private CurrencyRepository() {
        dbManager = DatabaseManager.getInstance();
    }

    public static CurrencyRepository getInstance() {
        if (instance == null) {
            synchronized (CurrencyRepository.class) {
                if (instance == null) {
                    instance = new CurrencyRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public void save(Currency entity) {
        String query = "INSERT INTO currencies VALUES (NULL, ?, ?, ?)";

        try {
            Connection connection = dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, entity.getCode());
            statement.setString(2,entity.getFullName());
            statement.setString(3, entity.getSign());
            statement.executeUpdate();
            dbManager.closeConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Currency> findById(Long id) {
        String query = "SELECT * FROM currencies WHERE id = ?";
        Currency currency = null;

        try {
            Connection connection = dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                currency = createNewCurrency(resultSet);
            }
            resultSet.close();
            dbManager.closeConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(currency);
    }

    public Optional<Currency> findByCode(String code) {
        Currency currency = null;
        String query = "SELECT * FROM currencies WHERE code = ?";

        try {
            Connection connection = dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                currency = createNewCurrency(resultSet);
            }
            resultSet.close();
            dbManager.closeConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(currency);
    }

    @Override
    public List findAll() {
        List<Currency> currencies = new ArrayList<>();
        String query = "SELECT * FROM currencies";

        try {
            Connection connection = dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Currency cur = createNewCurrency(resultSet);
                currencies.add(cur);
            }
            resultSet.close();
            dbManager.closeConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return currencies;
    }

    @Override
    public void update(Currency entity) {
        String query =  "UPDATE currencies " +
                        "SET " +
                            "code = ?," +
                            "fullname = ?," +
                            "sign = ?" +
                        "WHERE id = ?";

        try {
            Connection connection = dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
            dbManager.closeConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM currencies WHERE id = ?";
        Utilities.deleteEntityById(query, id, dbManager);
    }

    private Currency createNewCurrency(ResultSet resultSet) throws SQLException {
        Currency currency = new Currency();
        currency.setId(resultSet.getLong("id"));
        currency.setCode(resultSet.getString("code"));
        currency.setSign(resultSet.getString("sign"));
        currency.setFullName(resultSet.getString("fullname"));

        return currency;
    }
}

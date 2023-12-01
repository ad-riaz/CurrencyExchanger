package repository;

import model.Currency;
import service.DatabaseManager;
import util.Utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyRepo implements CurrencyRepository {
    private static CurrencyRepo instance;
    private final DatabaseManager dbManager;

    private CurrencyRepo() {
        dbManager = DatabaseManager.getInstance();
    }

    public static CurrencyRepo getInstance() {
        if (instance == null) {
            synchronized (CurrencyRepo.class) {
                if (instance == null) {
                    instance = new CurrencyRepo();
                }
            }
        }
        return instance;
    }

    @Override
    public void save(Currency entity) throws Exception {
        String query = "INSERT INTO currencies VALUES (NULL, ?, ?, ?)";

        Connection connection = dbManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, entity.getCode());
        statement.setString(2,entity.getFullName());
        statement.setString(3, entity.getSign());
        statement.executeUpdate();
        dbManager.closeConnection(connection);
    }

    @Override
    public Optional<Currency> findById(Long id) throws Exception {
        String query = "SELECT * FROM currencies WHERE id = ?";
        Currency currency = null;

	    Connection connection = dbManager.getConnection();
	    PreparedStatement statement = connection.prepareStatement(query);
	    statement.setLong(1, id);
	    ResultSet resultSet = statement.executeQuery();
	    if (resultSet.next()) {
	        currency = createNewCurrency(resultSet);
	    }
	    resultSet.close();
	    dbManager.closeConnection(connection);
        

        return Optional.ofNullable(currency);
    }

    @Override
    public Optional<Currency> findByCode(String code) throws Exception {
        Currency currency = null;
        String query = "SELECT * FROM currencies WHERE code = ?";

        Connection connection = dbManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, code);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            currency = createNewCurrency(resultSet);
        }
        resultSet.close();
        dbManager.closeConnection(connection);

        return Optional.ofNullable(currency);
    }

    @Override
    public List findAll() throws Exception {
        List<Currency> currencies = new ArrayList<>();
        String query = "SELECT * FROM currencies";

    
        Connection connection = dbManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            Currency cur = createNewCurrency(resultSet);
            currencies.add(cur);
        }
        resultSet.close();
        dbManager.closeConnection(connection);

        return currencies;
    }

    @Override
    public void update(Currency entity) throws Exception {
        String query =  "UPDATE currencies " +
                        "SET " +
                            "code = ?," +
                            "fullname = ?," +
                            "sign = ?" +
                        "WHERE id = ?";

        Connection connection = dbManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, entity.getCode());
        statement.setString(2, entity.getFullName());
        statement.setString(3, entity.getSign());
        statement.setLong(4, entity.getId());
        statement.executeUpdate();
        dbManager.closeConnection(connection);
    }

    @Override
    public void delete(Long id) throws Exception {
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

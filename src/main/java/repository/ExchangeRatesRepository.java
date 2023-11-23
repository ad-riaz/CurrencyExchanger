package repository;

import java.util.Optional;

import model.ExchangeRate;

public interface ExchangeRatesRepository extends CrudRepository<ExchangeRate> {

    Optional<ExchangeRate> findByCodes(String baseCode, String targetCode);
}

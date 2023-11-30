package repository;

import model.Currency;
import java.util.Optional;

public interface CurrencyRepository extends CrudRepository<Currency> {
    
    Optional<Currency> findByCode(String code) throws Exception;
}

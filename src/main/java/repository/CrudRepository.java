package repository;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    void save(T entity) throws Exception;

    Optional<T> findById(Long id) throws Exception;

    List<T> findAll() throws Exception;

    void update(T entity) throws Exception;

    void delete(Long id) throws Exception;
}

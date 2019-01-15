package me.identityprovider.common.service;

import java.util.Optional;

import me.identityprovider.common.exception.ServiceException;
import org.springframework.data.repository.CrudRepository;

/**
 * todo: write javadoc...
 *
 * @param <T>
 * @param <ID>
 */
public abstract class BaseService<T, ID> {

    private CrudRepository<T, ID> repository;

    public BaseService(CrudRepository<T, ID> repository) {
        this.repository = repository;
    }

    public T read(ID id) throws ServiceException {
        Optional<T> entity = repository.findById(id);
        if (!entity.isPresent()) {
            throw new ServiceException("No entity was found with the given Id");
        }
        return entity.get();
    }

    public T save(T entity) throws ServiceException {
        if (entity == null){
            throw new ServiceException("Cannot persist a null object");
        }
        return repository.save(entity);
    }

    public void delete(ID entityId) throws ServiceException {
        T entity = read(entityId);
        repository.delete(entity);
    }

    public boolean exists(ID entityId) {
        return repository.existsById(entityId);
    }

}

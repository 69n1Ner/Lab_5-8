package db;

import exceptions.NoSuchEntityException;
import security.User;

import java.util.List;

public interface Dao<E> {
    // если нужно будет реализовать другие объекты через интерфейс, то создать иерархию ошибок для отсутствующего элемента
    int save(E e, User user);
    boolean update(E e, Long id, User user) throws NoSuchEntityException;
    List<E> findAll();
    E findById(Long id) throws NoSuchEntityException;
    boolean delete(Long id, User user) throws NoSuchEntityException;
}

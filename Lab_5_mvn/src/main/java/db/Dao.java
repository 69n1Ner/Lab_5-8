package db;

import exceptions.NoSuchEntityException;
import exceptions.NoSuchOrganizationException;

import java.util.List;

public interface Dao<E> {
    // если нужно будет реализовать другие объекты через интерфейс, то создать иерархию ошибок для отсутствующего элемента
    int save(E e);
    boolean update(E e,Long id) throws NoSuchEntityException;
    List<E> findAll();
    E findById(Long id) throws NoSuchEntityException;
    boolean delete(Long id) throws NoSuchEntityException;
}

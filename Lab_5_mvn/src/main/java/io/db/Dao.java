package io.db;

import exceptions.NoSuchOrganizationException;

import java.util.List;

public interface Dao<E> {
    int save(E e);
    boolean update(E e,Long id) throws NoSuchOrganizationException;
    List<E> findAll();
    E findById(Long id) throws NoSuchOrganizationException;
    boolean delete(Long id);
}

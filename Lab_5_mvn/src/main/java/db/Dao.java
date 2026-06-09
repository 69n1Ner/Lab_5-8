package db;

import exceptions.NoSuchEntityException;
import io.ObjWithFeedback;
import security.User;

import java.util.List;

public interface Dao<E> {
    // если нужно будет реализовать другие объекты через интерфейс, то создать иерархию ошибок для отсутствующего элемента
    ObjWithFeedback<Integer> save(E e, User user);
    ObjWithFeedback<Boolean> update(E e, Long id, User user) throws NoSuchEntityException;
    List<E> findAll();
    ObjWithFeedback<E> findById(Long id) throws NoSuchEntityException;
    ObjWithFeedback<Boolean> delete(Long id, User user) throws NoSuchEntityException;
}

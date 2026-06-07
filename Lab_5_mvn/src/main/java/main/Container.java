package main;

import exceptions.NoSuchEntityException;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

import static java.lang.Math.abs;


public abstract class Container<T extends IdGettable<T>> {
    protected final TreeSet<T> container;
    protected final LocalDate creationDate;

    public Container(Comparator<T> comparator){
        this.creationDate = LocalDate.now();
        this.container = new TreeSet<>(comparator);

    }

    public abstract T getById(Long id) throws NoSuchEntityException;

    public void update(T t,Long id) throws NoSuchEntityException {
        T t1 = getById(id);
        t1.update(t);
    }

    public void add(T t) {
        try {
            getById(t.getId());
        } catch (NoSuchEntityException e) {
            container.add(t);
        }
    }

    public void addList(List<T> list){
        this.container.addAll(list);
    }

    public List<T> getAll(){
        return new ArrayList<>(container);
    }

    public int size(){
        return container.size();
    }

    public void removeById(Long id) throws NoSuchEntityException {
        container.remove(getById(id));
    }

    public void removeAll(ArrayList<T> list){
        list.forEach(container::remove);
    }

    public void remove(T t){
        container.remove(t);
    }

    public boolean removeIf(Predicate<T> filter) {
        return container.removeIf(filter);
    }

    public void clear(){
        this.container.clear();
    }

    public LocalDate getCreationDate(){
        return creationDate;
    }


}

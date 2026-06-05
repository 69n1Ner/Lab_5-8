package main;

import exceptions.NoSuchOrganizationException;
import exceptions.SameOrganizationExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Organization;
import sorts.SortById;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import static java.lang.Math.abs;


public class Container<T extends Organization> {
    private final TreeSet<Organization> container;
    private final LocalDate creationDate;
    private static final Container<Organization> INSTANCE = new Container<>();

    private Container(){
        this.creationDate = LocalDate.now();
        this.container = new TreeSet<>(new SortById());
    }

    public static Container<Organization> getInstance() {
        return INSTANCE;
    }

    public Organization getById(Long id) throws NoSuchOrganizationException{
        return container.stream()
                .filter(org -> Objects.equals(org.getId(), id))
                .findFirst()
                .orElseThrow(NoSuchOrganizationException::new);
    }

    public void update(Organization newOrg,Long id) throws NoSuchOrganizationException {
        Organization oldOrg = getById(id);
        oldOrg.update(newOrg);
    }




    public void add(T newOrganization) {
        try {
            getById(newOrganization.getId());
        } catch (NoSuchOrganizationException e) {
            container.add(newOrganization);
        }
    }

    public void addList(List<T> list){
        this.container.addAll(list);
    }

    public List<T> getAll(){
        return new ArrayList<>((Collection<? extends T>) container);
    }

    public int size(){
        return container.size();
    }

    public void removeById(Long id) throws NoSuchOrganizationException{
        container.remove(getById(id));
    }

    public void removeAll(ArrayList<T> list){
        list.forEach(container::remove);
    }

    public void remove(T t){
        container.remove(t);
    }

    public boolean removeIf(Predicate<Organization> filter) {
        return container.removeIf(filter);
    }

    public void clear(){
        this.container.clear();
    }

    public LocalDate getCreationDate(){
        return creationDate;
    }


}

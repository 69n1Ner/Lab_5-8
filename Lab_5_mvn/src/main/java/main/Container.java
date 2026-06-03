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
    private static final Logger logger = LogManager.getLogger(Container.class);

    public Container(){
        this.creationDate = LocalDate.now();
        this.container = new TreeSet<>(new SortById());
    }

    public Organization getById(Long id) throws NoSuchOrganizationException{
        return container.stream()
                .filter(org -> Objects.equals(org.getId(), id))
                .findFirst()
                .orElseThrow(NoSuchOrganizationException::new);
    }

    public Long getIdBy(Organization organization){
        return organization.getId();
    }

    public void update(Organization newOrg,Organization oldOrg){

        oldOrg.setAnnualTurnover(newOrg.getAnnualTurnover() > 0 ? newOrg.getAnnualTurnover() : oldOrg.getAnnualTurnover());
        long xC = newOrg.getCoordinates().getX();
        Double yC = newOrg.getCoordinates().getY();

        oldOrg.getCoordinates().setX(xC);
        if (yC != null){
            oldOrg.getCoordinates().setY(yC);
        }

        oldOrg.setEmployeesCount(newOrg.getEmployeesCount() > 0 ? newOrg.getEmployeesCount() : oldOrg.getEmployeesCount());
        oldOrg.setName((newOrg.getName() != null && !newOrg.getName().isEmpty()) ? newOrg.getName() : oldOrg.getName());

        String zip = newOrg.getPostalAddress().getZipCode();
        Float xL = newOrg.getPostalAddress().getTown().getX();
        Integer yL = newOrg.getPostalAddress().getTown().getY();
        Integer zL = newOrg.getPostalAddress().getTown().getZ();
        String name = newOrg.getPostalAddress().getTown().getName();
        if (zip != null && zip.length() >= 4){
            oldOrg.getPostalAddress().setZipCode(zip);
        }
        if (xL != null){
            oldOrg.getPostalAddress().getTown().setX(xL);
        }
        if (yL != null){
            oldOrg.getPostalAddress().getTown().setY(yL);
        }
        if (zL != null){
            oldOrg.getPostalAddress().getTown().setZ(zL);
        }
        if (name != null && !name.isEmpty()){
            oldOrg.getPostalAddress().getTown().setName(name);
        }

        oldOrg.setType(newOrg.getType() != null ? newOrg.getType() : oldOrg.getType());
    }




    public void add(T newOrganization) {
        try {
            getById(newOrganization.getId());
        } catch (NoSuchOrganizationException e) {
            container.add(newOrganization);
        }
    }

    public void addList(ArrayList<T> list){
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

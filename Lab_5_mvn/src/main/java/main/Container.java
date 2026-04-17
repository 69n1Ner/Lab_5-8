package main;

import organization.Organization;
import sorts.SortById;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

import static java.lang.Math.abs;
import static java.util.Objects.hash;


public class Container<T extends Organization> {
    private final TreeSet<Organization> container;
    private final LocalDate creationDate;

    public Container(){
        this.creationDate = LocalDate.now();
        this.container = new TreeSet<>(new SortById());
    }

    public Organization getById(Long id){
        return container.stream()
                .filter(org -> Objects.equals(org.getId(), id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Такой организации не существует"));
    }

    public Long getIdBy(Organization organization){
        return organization.getId();
    }

    private void setParamsTo(Organization newOrg,Organization oldOrg){
        oldOrg.setAnnualTurnover(newOrg.getAnnualTurnover() > 0 ? newOrg.getAnnualTurnover() : oldOrg.getAnnualTurnover());
        long xC = newOrg.getCoordinates().getX();
        Double yC = newOrg.getCoordinates().getY();
        if (xC != 0){
            oldOrg.getCoordinates().setX(xC);
        }
        if (yC != null){
            oldOrg.getCoordinates().setY(yC);
        }

        oldOrg.setEmployeesCount( newOrg.getEmployeesCount() > 0 ? newOrg.getEmployeesCount() : oldOrg.getEmployeesCount());
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

    public Organization generateFields(Organization organization, boolean isReadFile){
        if (!isReadFile){
            if (organization.getId() == null || organization.getId() <= 0){
                organization.setId((long) abs(hash(ZonedDateTime.now()) + hashCode()));
            }
            organization.setCreationDate(organization.getCreationDate() == null ? LocalDate.now() : organization.getCreationDate());
        }

        if (organization.getAnnualTurnover() == 0){
            System.out.println("Значение годовой выручки не было установлено");
        }

        long xC = organization.getCoordinates().getX();
        Double yC = organization.getCoordinates().getY();
        if (xC == 0){
            System.out.println("Значение координаты X организации не было установлено");
        } else
        if (xC > 623) {
            organization.getCoordinates().setX(623);
            System.out.println("Значение координаты X организации получило максимальное значение (623)");
        }
        if (yC== null){
            System.out.println("Значение координаты Y организации не было установлено");
        }

        if ( organization.getEmployeesCount() == 0){
            System.out.println("Значение количества сотрудников не было установлено");
        }

        if (organization.getName().isEmpty()){
            System.out.println("Значение названия организации не было установлено");
        }

        String zip = organization.getPostalAddress().getZipCode();
        Float xL = organization.getPostalAddress().getTown().getX();
        Integer yL = organization.getPostalAddress().getTown().getY();
        Integer zL = organization.getPostalAddress().getTown().getZ();
        String name = organization.getPostalAddress().getTown().getName();
        if (zip == null || zip.length() < 4){
            System.out.println("Значение почтового индекса не было установлено");
        }
        if (xL == null){
            System.out.println("Значение координаты X города не было установлено");
        }
        if (yL == null){
            System.out.println("Значение координаты Y города не было установлено");
        }
        if (zL == null){
            System.out.println("Значение координаты Z города не было установлено");
        }
        if (name == null || name.isEmpty()){
            System.out.println("Значение названия города не было установлено");
        }
        if (organization.getType() == null){
            System.out.println("Значение типа организации не было установлено");
        }

        return organization;
    }


    public void add(Organization newOrganization) {
        this.container.add(newOrganization);
    }

    public void addList(ArrayList<T> list){
        this.container.addAll(list);
    }

    public void update(T parametrizedOrg,T oldOrg ){
        setParamsTo(parametrizedOrg, oldOrg);
    }

    public List<Organization> getAll(){
        return new ArrayList<>(container);
    }

    public int size(){
        return container.size();
    }

    public void removeById(Long id){
        this.container.remove(this.getById(id));
    }



    public void clear(){
        this.container.clear();
    }

    public LocalDate getCreationDate(){
        return creationDate;
    }


}

package MainProg;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

import static java.lang.Math.abs;
import static java.util.Objects.hash;

public class Container<T extends Organization> {
    private TreeSet<T> container;

    public Container(){
        this.container = new TreeSet<>(new SortById());
    }

    //todo добавить ошибку на наличие элемента
    public Organization getById(Long id){
        Organization comparingOrganization = new Organization();
        comparingOrganization.setId(id);

        Organization possibleOrg = container.ceiling((T) comparingOrganization);
        if (possibleOrg != null){
            return possibleOrg;
        } else {
            throw new NullPointerException("Такой организации не существует");
        }
    }

    public Long getIdBy(Organization organization){
        Organization possibleOrg = container.ceiling((T) organization);
        if (possibleOrg != null){
            return possibleOrg.getId();
        } else {
            throw new NullPointerException("Такой организации не существует");
        }
    }

    private void setParamsTo(Organization newOrg,Organization oldOrg){
        oldOrg.setAnnualTurnover(newOrg.getAnnualTurnover() > 0 ? newOrg.getAnnualTurnover() : oldOrg.getAnnualTurnover());
        Long xC = newOrg.getCoordinates().getX();
        Double yC = newOrg.getCoordinates().getY();
        if (xC != 0){
            oldOrg.getCoordinates().setX(xC);
        }
        if (yC != null){
            oldOrg.getCoordinates().setY(yC);
        }

        oldOrg.setEmployeesCount( newOrg.getEmployeesCount() > 0 ? newOrg.getEmployeesCount() : oldOrg.getEmployeesCount());
        oldOrg.setName(newOrg.getName() != null ? newOrg.getName() : oldOrg.getName());

        String zip = newOrg.getPostalAddress().getZipCode();
        Float xL = newOrg.getPostalAddress().getTown().getX();
        Integer yL = newOrg.getPostalAddress().getTown().getY();
        Integer zL = newOrg.getPostalAddress().getTown().getZ();
        String name = newOrg.getPostalAddress().getTown().getName();
        if (zip != null && zip.length() < 4){
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

    public Organization generateFields(Organization organization){
        organization.setId((long) abs(hash(ZonedDateTime.now()) + hashCode()));
        organization.setCreationDate(LocalDate.now());

        if (organization.getAnnualTurnover() == 0){
            organization.setAnnualTurnover(1);
            System.out.println("Значение годовой выручки получило базовое значение (1)");
        }

        Long xC = organization.getCoordinates().getX();
        Double yC = organization.getCoordinates().getY();
        if (xC == null || xC == 0){
            organization.getCoordinates().setX(1);
            System.out.println("Значение координаты X организации получило базовое значение (1)");
        } else if (xC > 623) {
            organization.getCoordinates().setX(623);
            System.out.println("Значение координаты X организации получило максимальное значение (623)");
        }
        if (yC== null){
            organization.getCoordinates().setY(1.0);
            System.out.println("Значение координаты Y организации получило базовое значение (1.0)");
        }

        if ( organization.getEmployeesCount() == 0){
            organization.setEmployeesCount(1);
            System.out.println("Значение количества сотрудников получило базовое значение (1)");
        }

        if (organization.getName().isEmpty()){
            organization.setName("Organization"+organization.getId());
            System.out.println("Значение названия организации получило базовое значение ("+organization.getName()+")");
        }

        String zip = organization.getPostalAddress().getZipCode();
        Float xL = organization.getPostalAddress().getTown().getX();
        Integer yL = organization.getPostalAddress().getTown().getY();
        Integer zL = organization.getPostalAddress().getTown().getZ();
        String name = organization.getPostalAddress().getTown().getName();
        if (zip.isEmpty() || zip.length() < 4){
            organization.getPostalAddress().setZipCode("0000");
            System.out.println("Значение почтового индекса получило базовое значение (0000)");

        }
        if (xL == null){
            organization.getPostalAddress().getTown().setX(1F);
            System.out.println("Значение координаты X города получило базовое значение (1F)");
        }
        if (yL == null){
            organization.getPostalAddress().getTown().setY(1);
            System.out.println("Значение координаты Y города получило базовое значение (1)");
        }
        if (zL == null){
            organization.getPostalAddress().getTown().setZ(1);
            System.out.println("Значение координаты Z города получило базовое значение (1)");
        }
        if (name.isEmpty()){
            organization.getPostalAddress().getTown().setName("Town"+organization.getId());
            System.out.println("Значение названия города получило базовое значение ("+organization.getPostalAddress().getTown().getName()+")");
        }

        organization.setType(organization.getType() == null ? OrganizationType.PUBLIC : organization.getType());
        if (organization.getType() == null){
            organization.setType(OrganizationType.PUBLIC);
            System.out.println("Значение типа организации получило базовое значение (Публичная)");
        }

        return organization;
    }


    //TODO Доделать после создания команды update
    public void add(T newOrganization) {
        this.container.add(newOrganization);
    }

    public void addList(ArrayList<T> list){
        this.container.addAll(list);
    }

    public void update(T parametrizedOrg,T oldOrg ){
        setParamsTo(parametrizedOrg, oldOrg);
    }

    public List<T> getAll(){
        return new ArrayList<>(container);
    }

    public void removeById(Long id){
        this.container.remove(this.getById(id));
    }

    public void clear(){
        this.container.clear();
    }


}

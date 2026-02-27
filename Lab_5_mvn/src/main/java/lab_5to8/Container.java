package lab_5to8;

import java.util.*;

public class Container<T extends Organization> {
    private TreeSet<T> container;

    public Container(){
        this.container = new TreeSet<>(new SortById());
    }

    private Organization getById(Long id){
        Organization comparingOrganization = new Organization();
        comparingOrganization.setId(id);
        return this.container.ceiling((T) comparingOrganization);
    }

    //TODO Доделать после создания команды update
    public void add(T newOrganization) {
        newOrganization.generateId();
        newOrganization.generateDate();
        this.container.add(newOrganization);
    }

    public void updateById(Long id, T parametrizedOrg){
        this.getById(id).setParams(parametrizedOrg);
    }

    public void removeById(Long id){
        this.container.remove(this.getById(id));
    }

    public void clear(){
        this.container.clear();
    }


}

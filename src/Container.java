import java.util.*;

public class Container<T extends Organization> {
    private TreeSet<T> container;

    public Container(){
        this.container = new TreeSet<>(new SortById());
    }

    public List<T> getAll() {
        return new ArrayList<>(container);
    }

    public Long getId(T t){
        return t.getId();
    }
    //TODO Доделать после создания команды update
    public void addById(Long id, T newOrganization) {
        Organization temp = new Organization(id);
//        Organization newOrganization = container.ceiling((T) temp);
//        container.remove(oldOrganization);

    }
}

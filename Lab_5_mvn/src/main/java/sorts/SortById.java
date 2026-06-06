package sorts;

import main.IdGettable;
import organization.Organization;

import java.util.Comparator;
import java.util.Objects;

public class SortById<T extends IdGettable<T>> implements Comparator<T> {


    @Override
    public int compare(T o1, T o2) {
        if (o1.getId() < o2.getId()){
            return -1;
        } else if (Objects.equals(o1.getId(), o2.getId())){
            return 0;
        } else return 1;
    }
}

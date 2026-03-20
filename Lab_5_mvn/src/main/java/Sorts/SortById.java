package Sorts;

import MainProg.Organization;

import java.util.Comparator;
import java.util.Objects;

public class SortById implements Comparator<Organization> {

    @Override
    public int compare(Organization o1, Organization o2) {
        if (o1.getId() < o2.getId()){
            return -1;
        } else if (Objects.equals(o1.getId(), o2.getId())){
            return 0;
        } else return 1;
    }
}

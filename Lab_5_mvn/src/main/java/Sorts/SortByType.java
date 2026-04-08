package Sorts;

import OrganizationObject.Organization;

import java.util.Comparator;

public class SortByType implements Comparator<Organization> {
    @Override
    public int compare(Organization o1, Organization o2) {
        if (o1.getType() == null && o2.getType() == null){
            return 0;
        } else if (o1.getType() != null && o2.getType() == null){
            return -1;
        } else if (o1.getType() == null && o2.getType() != null) {
            return 1;
        }
        return (o1.getType().getName().compareTo(o2.getType().getName()));
    }
}

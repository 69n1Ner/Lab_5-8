package Sorts;

import OrganizationObject.Organization;

import java.util.Comparator;

public class SortByType implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        return ((Organization) o1).getType().getName().compareTo(((Organization) o2).getType().getName());
    }
}

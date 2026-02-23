import java.util.Comparator;

public class SortById implements Comparator<Organization> {

    @Override
    public int compare(Organization o1, Organization o2) {
        if (o1.getId() < o2.getId()){
            return -1;
        } else if ( o1.getId() == o2.getId()){
            return 0;
        } else return 1;
    }
}

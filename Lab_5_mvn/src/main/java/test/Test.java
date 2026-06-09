package test;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<String> lst = new ArrayList<>();
        System.out.println(lst.isEmpty());
        StringBuilder sb = new StringBuilder();
        lst.add(sb.toString());
        System.out.println(lst.isEmpty());
        sb.append("");
        lst.add(sb.toString());
        System.out.println(lst.isEmpty());

    }
}

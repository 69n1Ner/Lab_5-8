package lab_5to8;

import jakarta.xml.bind.Unmarshaller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static List<String> parseXMLStringToStringList(String xmlString){
        BufferedReader bufferedReader = new BufferedReader(new StringReader(xmlString));
        return new ArrayList<>();
    }
}

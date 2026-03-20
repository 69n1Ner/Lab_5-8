package IO;


import MainProg.Address;
import MainProg.Container;
import MainProg.Organization;
import MainProg.OrganizationType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.TreeSet;

public class XmlUtil {


    public static void writeListToFile(ArrayList<Organization> list, String filename) {


        try {
            ContainerWrapper wrapper = new ContainerWrapper();
            wrapper.getOrganizations().addAll(list);

            JAXBContext context = JAXBContext.newInstance(ContainerWrapper.class);

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(wrapper, new File(filename));

            System.out.println("Коллекция записана в файл: " + filename);

        } catch (JAXBException e) {
            System.err.println("Ошибка при записи: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static ArrayList<Organization> readListFromFile(String filename) {
        File file = new File(filename);

        if (!file.exists()) {
            System.err.println("Файл не найден: " + file.getAbsolutePath());
            return new ArrayList<>();
        }

        if (!file.isFile()) {
            System.err.println("Указанный путь не является файлом: " + file.getAbsolutePath());
            return new ArrayList<>();
        }

        try {
            JAXBContext context = JAXBContext.newInstance(ContainerWrapper.class);

            Unmarshaller unmarshaller = context.createUnmarshaller();

            ContainerWrapper wrapper = (ContainerWrapper) unmarshaller.unmarshal(new File(filename));

            System.out.println("Список прочитан из файла: " + filename);
            return new ArrayList<>(wrapper.getOrganizations());

        } catch (JAXBException e) {
            System.err.println("Ошибка при чтении: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static Organization readObjectFromString(String xmlString) {
        if (xmlString == null || xmlString.trim().isEmpty()) {
            System.err.println("Пустая XML-строка");
            return null;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(Organization.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Organization) unmarshaller.unmarshal(new StringReader(xmlString));

        } catch (JAXBException e) {
            System.err.println("Ошибка чтения XML"+ e.getCause().getMessage());;
            return null;
        }
    }

    public static Address readAddressFromString(String xmlString){
        if (xmlString == null || xmlString.trim().isEmpty()) {
            System.err.println("Пустая XML-строка");
            return null;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(Address.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Address) unmarshaller.unmarshal(new StringReader(xmlString));

        } catch (JAXBException e) {
            System.err.println("Ошибка чтения XML"+ e.getCause().getMessage());;
            return null;
        }
    }
}

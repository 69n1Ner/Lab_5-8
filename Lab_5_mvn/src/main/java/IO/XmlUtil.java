package IO;


import OrganizationObject.Address;
import OrganizationObject.Organization;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

public class XmlUtil {


    public static void writeListToFile(ArrayList<Organization> list, String filename) {


        try {
            ContainerWrapper wrapper = new ContainerWrapper();
            wrapper.getOrganizations().addAll(list);

            JAXBContext context = JAXBContext.newInstance(ContainerWrapper.class);

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(wrapper, new File(filename));

            System.out.println("Коллекция записана в файл: " + System.getProperty("user.dir")+"\\"+filename);

        } catch (JAXBException e) {
            System.err.println("Ошибка при записи: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static ArrayList<Organization> readListFromFile(String resourceName) {
        InputStream inputStream = XmlUtil.class.getClassLoader().getResourceAsStream(resourceName);

        if (inputStream == null) {
            System.err.println("!! Коллекция не загружена. Поставьте значение PLAB5_8 = 'initial_collection.xml' !!");
            return new ArrayList<>();
        }

        try {
            JAXBContext context = JAXBContext.newInstance(ContainerWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            ContainerWrapper wrapper = (ContainerWrapper) unmarshaller.unmarshal(inputStream);
            System.out.println("Загружено из JAR: " + wrapper.getOrganizations().size());
            return new ArrayList<>(wrapper.getOrganizations());
        } catch (JAXBException e) {
            System.err.println("!! Ошибка парсинга XML: " + e.getMessage()+" !!");
            return new ArrayList<>();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public static Organization readObjectFromString(String xmlString) {
        if (xmlString == null || xmlString.trim().isEmpty()) {
            System.err.println("!! Пустая XML-строка !!");
            return null;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(Organization.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Organization) unmarshaller.unmarshal(new StringReader(xmlString));

        } catch (JAXBException e) {
            System.err.println("!! Ошибка чтения XML"+ e.getMessage()+" !!");;
            return null;
        }
    }

    public static Address readAddressFromString(String xmlString){
        if (xmlString == null || xmlString.trim().isEmpty()) {
            System.err.println("!! Пустая XML-строка !!");
            return null;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(Address.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Address) unmarshaller.unmarshal(new StringReader(xmlString));

        } catch (JAXBException e) {
            System.err.println("!! Ошибка чтения XML"+ e.getMessage()+" !!");;
            return null;
        }
    }
}

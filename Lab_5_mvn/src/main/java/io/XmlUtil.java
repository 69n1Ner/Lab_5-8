package io;


import exceptions.XmlUtilException;
import organization.Address;
import organization.Organization;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

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
            throw new RuntimeException(Arrays.toString(e.getStackTrace()).replace(",","\n"));

        }
    }


    public static ArrayList<Organization> readListFromFile(String resourceName) {
        InputStream inputStream = XmlUtil.class.getClassLoader().getResourceAsStream(resourceName);

        try (inputStream) {
            try {
                if (inputStream == null) {
                    throw new XmlUtilException("Коллекция не загружена. Поставьте значение LAB5_8 = 'initial_collection.xml'");
                }
                JAXBContext context = JAXBContext.newInstance(ContainerWrapper.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                ContainerWrapper wrapper = (ContainerWrapper) unmarshaller.unmarshal(inputStream);
                System.out.println("Загружено из " + resourceName + ": " + wrapper.getOrganizations().size() + " организаций");
                return new ArrayList<>(wrapper.getOrganizations());
            } catch (JAXBException e) {
                throw new XmlUtilException("Ошибка парсинга XML: " + e.getMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Organization readObjectFromString(String xmlString) {
        if (xmlString == null || xmlString.trim().isEmpty()) {
            throw new XmlUtilException("Пустая XML-строка");
        }

        try {
            JAXBContext context = JAXBContext.newInstance(Organization.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Organization) unmarshaller.unmarshal(new StringReader(xmlString));

        } catch (JAXBException e) {
            throw new XmlUtilException("Ошибка чтения XML"+ e.getMessage());
        }
    }

    public static Address readAddressFromString(String xmlString){
        if (xmlString == null || xmlString.trim().isEmpty()) {
            throw new XmlUtilException("Пустая XML-строка");
        }

        try {
            JAXBContext context = JAXBContext.newInstance(Address.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (Address) unmarshaller.unmarshal(new StringReader(xmlString));

        } catch (JAXBException e) {
            throw new XmlUtilException("Ошибка чтения XML "+ e.getMessage());
        }
    }
}

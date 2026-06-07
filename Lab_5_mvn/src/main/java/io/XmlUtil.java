package io;


import exceptions.XmlUtilException;
import jakarta.xml.bind.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import organization.Address;
import organization.Organization;

import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class XmlUtil {

    final private static JAXBContext CONTEXT_WRAPPER;
    final private static JAXBContext CONTEXT_ORGANIZATION;
    final private static JAXBContext CONTEXT_ADDRESS;
    final private static Logger logger = LogManager.getLogger(XmlUtil.class);
    static {
        try {
            CONTEXT_ADDRESS = JAXBContext.newInstance(Address.class);
            CONTEXT_WRAPPER = JAXBContext.newInstance(ContainerWrapper.class);
            CONTEXT_ORGANIZATION = JAXBContext.newInstance(Organization.class);
        } catch (JAXBException e) {
            throw new XmlUtilException(Arrays.toString(e.getStackTrace()).replace(",","\n"));
        }
    }

    public static String adrToXml(Address address) throws XmlUtilException{
        try{
            Marshaller marshaller = CONTEXT_ADDRESS.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter xmlOrg = new StringWriter();
            marshaller.marshal(address,new StreamResult(xmlOrg));
            String xmlString = xmlOrg.toString();
            xmlString = xmlString.replaceAll("<\\?xml[^>]*>","")
                    .replaceAll("\\s","");
            return xmlString;
        }catch (JAXBException e){
            throw new XmlUtilException(Arrays.toString(e.getStackTrace()).replace(",","\n"));
        }
    }

    public static String orgToXml(Organization organization) throws XmlUtilException{
        try {
            ContainerWrapper wrapper = new ContainerWrapper();
            wrapper.getOrganizations().add(organization);
            Marshaller marshaller = CONTEXT_WRAPPER.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter xmlOrg = new StringWriter();
            marshaller.marshal(wrapper,new StreamResult(xmlOrg));
            String xmlString = xmlOrg.toString().strip();

            xmlString = xmlString.replaceAll("<\\?xml[^>]*>","")
                    .replaceAll("\\s","")
                    .replaceAll("</?collection>","");
            logger.debug("txt- {}",xmlString);
            return xmlString;
        } catch (JAXBException e) {
            throw new XmlUtilException(Arrays.toString(e.getStackTrace()).replace(",","\n"));
        }
    }

    public static String writeListToFile(ArrayList<Organization> list, String filename) throws XmlUtilException {
        try {
            ContainerWrapper wrapper = new ContainerWrapper();
            wrapper.getOrganizations().addAll(list);


            Marshaller marshaller = CONTEXT_WRAPPER.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(wrapper, new File(filename));

            return "Коллекция записана в файл: " + System.getProperty("user.dir")+"\\"+filename;

        } catch (JAXBException e) {
            throw new XmlUtilException(Arrays.toString(e.getStackTrace()).replace(",","\n"));

        }
    }


    public static ArrayList<Organization> readListFromFile(Path path) throws XmlUtilException{
        InputStream inputStream = XmlUtil.class.getClassLoader().getResourceAsStream(path.toString());

        try (inputStream) {
            try {
                if (inputStream == null) {
                    throw new XmlUtilException("Коллекция не загружена. Поставьте значение LAB5_8 = 'initial_collection.xml'");
                }

                Unmarshaller unmarshaller = CONTEXT_WRAPPER.createUnmarshaller();
                ContainerWrapper wrapper = (ContainerWrapper) unmarshaller.unmarshal(inputStream);
                System.out.println("Загружено из " + path + ": " + wrapper.getOrganizations().size() + " организаций");
                return new ArrayList<>(wrapper.getOrganizations());
            } catch (JAXBException e) {
                throw new XmlUtilException("Ошибка парсинга XML: " + e);
            }
        } catch (IOException e) {
            throw new XmlUtilException(Arrays.toString(e.getStackTrace()).replace(",","\n"));
        }
    }

    public static Organization readOrganizationFromString(String xmlString)  throws XmlUtilException{
        if (xmlString == null || xmlString.trim().isEmpty()) {
            throw new XmlUtilException("Пустая XML-строка");
        }

        try {
            Unmarshaller unmarshaller = CONTEXT_ORGANIZATION.createUnmarshaller();
            return (Organization) unmarshaller.unmarshal(new StringReader(xmlString));

        } catch (JAXBException e) {
            throw new XmlUtilException("Ошибка чтения XML"+ e);
        }
    }

    public static Address readAddressFromString(String xmlString) throws XmlUtilException{
        if (xmlString == null || xmlString.trim().isEmpty()) {
            throw new XmlUtilException("Пустая XML-строка");
        }

        try {
            Unmarshaller unmarshaller = CONTEXT_ADDRESS.createUnmarshaller();
            return (Address) unmarshaller.unmarshal(new StringReader(xmlString));

        } catch (JAXBException e) {
            throw new XmlUtilException("Ошибка чтения XML "+ e);
        }
    }
}

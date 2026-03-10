package IO;


import MainProg.Container;
import MainProg.Organization;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.TreeSet;

public class XmlUtil {

    // ==================== ЗАПИСЬ (Marshalling) ====================

    /**
     * Записывает список организаций в XML-файл
     */
    public static void writeListToFile(TreeSet<Organization> list, String filename) {
        try {
            // 1. Создаем обертку и помещаем в нее список
            Container<Organization> wrapper = new Container<>();
            wrapper.getAll().addAll(list);

            // 2. Создаем контекст для обертки
            JAXBContext context = JAXBContext.newInstance(Container.class);

            // 3. Создаем Marshaller
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            // 4. Записываем в файл
            marshaller.marshal(wrapper, new File(filename));

            System.out.println("Список записан в файл: " + filename);

        } catch (JAXBException e) {
            System.err.println("Ошибка при записи: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== ЧТЕНИЕ (Unmarshalling) ====================

    /**
     * Читает список организаций из XML-файла
     */
    public static ArrayList<Organization> readListFromFile(String filename) {
        try {
            // 1. Создаем контекст для обертки
            JAXBContext context = JAXBContext.newInstance(Container.class);

            // 2. Создаем Unmarshaller
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // 3. Читаем файл в обертку
            Container<Organization> wrapper = (Container<Organization>) unmarshaller.unmarshal(new File(filename));

            // 4. Возвращаем список из обертки
            System.out.println("Список прочитан из файла: " + filename);
            return new ArrayList<>(wrapper.getAll());

        } catch (JAXBException e) {
            System.err.println("Ошибка при чтении: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Читает объект из XML-строки (текста)
     */
    public static Organization readObjectFromString(String xmlString) {
        try {
            JAXBContext context = JAXBContext.newInstance(Organization.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            return (Organization) unmarshaller.unmarshal(new StringReader(xmlString));
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
}

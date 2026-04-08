package OrganizationObject;

import IO.LocalDateAdapter;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "organization")
@XmlAccessorType(XmlAccessType.FIELD)
public class Organization implements Comparable<Object> {
    @XmlElement(name = "id")
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    @XmlElement(name = "creation_date")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    @XmlElement(name = "name")
    private String name; //Поле не может быть null, Строка не может быть пустой
    @XmlElement(name = "type")
    private OrganizationType type; //Поле может быть null
    @XmlElement(name = "coordinates")
    private Coordinates coordinates; //Поле не может быть null
    @XmlElement(name = "postal_address")
    private Address postalAddress; //Поле не может быть null
    @XmlElement(name = "employees_count")
    private long employeesCount; //Значение поля должно быть больше 0
    @XmlElement(name = "annual_turnover")
    private int annualTurnover; //Значение поля должно быть больше 0

    public Organization(String name,
                        Integer annualTurnover,
                        Coordinates coordinates,
                        Long employeesCount,
                        Address postalAddress,
                        OrganizationType type) {
        this.annualTurnover = annualTurnover != null ? annualTurnover : 0;
        this.coordinates = coordinates;
        this.employeesCount = employeesCount != null ? employeesCount : 0;
        this.name = name != null ? name : "";
        this.postalAddress = postalAddress;
        this.type = type;
    }

    public Organization() {
        this.coordinates = new Coordinates();
        this.postalAddress = new Address();
        this.postalAddress.setTown(new Location());
    }

    @Override
    public String toString() {

        List<String> fieldsToPrint = new ArrayList<>(List.of(
                "\n Id: ",
                "\n Дата создания объекта: ",
                "\n Название: " ,
                "\n Тип организации: " ,
                "\n Координаты: " ,
                "\n Адрес: " ,
                "\n Количество сотрудников: " ,
                "\n Годовая выручка: "));
        Field[] fields = this.getClass().getDeclaredFields();

        for (int i = 0; i < fieldsToPrint.size(); i++){
            Field field = fields[i];
            String fieldToPrint = fieldsToPrint.get(i);

            field.setAccessible(true);
            try {
                Object value = field.get(this);

                if (value == null || (field.getType() == String.class && value.equals(""))) {
                    fieldsToPrint.set(i, fieldToPrint + "null");
                }else {
                    fieldsToPrint.set(i,fieldToPrint + value);
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return  "Organization:" +
                String.join("", fieldsToPrint);

    }

    // этот метод вызывать, при добавлении элемента в коллекцию

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public Address getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(Address postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getEmployeesCount() {
        return employeesCount;
    }

    public void setEmployeesCount(long employeesCount) {
        this.employeesCount = employeesCount;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public int getAnnualTurnover() {
        return annualTurnover;
    }

    public void setAnnualTurnover(int annualTurnover) {
        this.annualTurnover = annualTurnover;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null){
            return 1;
        } else {
            Organization other = (Organization) o;

            if (other.annualTurnover == 0 || other.employeesCount == 0){
                return 1;
            }

            int C1 = Integer.compare(annualTurnover,other.annualTurnover);
            int C2 = Long.compare(employeesCount,other.employeesCount);
            if (C1!= 0){
                return C1;
            }
            return C2;
        }

    }
}

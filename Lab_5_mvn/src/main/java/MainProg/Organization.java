package Main;

import jakarta.xml.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

import static java.lang.Math.abs;
import static java.util.Objects.checkFromToIndex;
import static java.util.Objects.hash;

@XmlRootElement(name = "Organization")
@XmlAccessorType(XmlAccessType.FIELD)
public class Organization {
    @XmlAttribute(name = "id")
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    @XmlElement(name = "name")
    private String name; //Поле не может быть null, Строка не может быть пустой
    @XmlElement(name = "coordinates")
    private Coordinates coordinates; //Поле не может быть null
    @XmlAttribute(name = "creationDate")
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    @XmlElement(name = "annualTurnover")
    private int annualTurnover; //Значение поля должно быть больше 0
    @XmlElement(name = "employeesCount")
    private long employeesCount; //Значение поля должно быть больше 0
    @XmlElement(name = "type")
    private OrganizationType type; //Поле может быть null
    @XmlElement(name = "postalAddress")
    private Address postalAddress; //Поле не может быть null

    public Organization(String name,
                        int annualTurnover,
                        Coordinates coordinates,
                        long employeesCount,
                        Address postalAddress,
                        OrganizationType type) {
        this.annualTurnover = annualTurnover;
        this.coordinates = coordinates;
        this.employeesCount = employeesCount;
        this.name = name;
        this.postalAddress = postalAddress;
        this.type = type;
    }

    public Organization() {
    }

    @Override
    public String toString() {
        return "Organization:" +
                "\n\tId: " + id +
                "\n\tДата создания объекта: " + creationDate +
                "\n\tНазвание: '" + name + '\'' +
                "\n\tТип организации: " + type.getName() +
                "\n\t" + coordinates +
                "\n\t" + postalAddress +
                "\n\tКоличество сотрудников: " + employeesCount +
                "\n\tГодовая выручка: " + annualTurnover;
    }

    // этот метод вызывать, при добавлении элемента в коллекцию
    public void generateId(){
        id = (long) abs(hash(ZonedDateTime.now()) + hashCode());
    }

    public void generateDate(){
        creationDate = LocalDate.now();
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

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public int getAnnualTurnover() {
        return annualTurnover;
    }

    public void setAnnualTurnover(int annualTurnover) {
        this.annualTurnover = annualTurnover;
    }
}

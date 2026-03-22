package OrganizationObject;

import IO.LocalDateAdapter;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDate;

import static java.lang.Math.abs;
import static java.util.Objects.checkFromToIndex;

//todo пройтись по ограничениям
@XmlRootElement(name = "organization")
@XmlAccessorType(XmlAccessType.FIELD)
public class Organization implements Comparable {
    @XmlElement(name = "id")
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    @XmlElement(name = "name")
    private String name; //Поле не может быть null, Строка не может быть пустой
    @XmlElement(name = "coordinates")
    private Coordinates coordinates; //Поле не может быть null
    @XmlElement(name = "creation_date")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    @XmlElement(name = "annual_turnover")
    private int annualTurnover; //Значение поля должно быть больше 0
    @XmlElement(name = "employees_count")
    private long employeesCount; //Значение поля должно быть больше 0
    @XmlElement(name = "type")
    private OrganizationType type; //Поле может быть null
    @XmlElement(name = "postal_address")
    private Address postalAddress; //Поле не может быть null

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
        this.name = "";
        this.coordinates = new Coordinates();
        this.postalAddress = new Address();
        this.postalAddress.setTown(new Location());
    }

    @Override
    public String toString() {
        return "Organization:" +
                "\n Id: " + id +
                "\n Дата создания объекта: " + creationDate +
                "\n Название: '" + name + '\'' +
                "\n Тип организации: " + type.getName() +
                "\n " + coordinates +
                "\n " + postalAddress +
                "\n Количество сотрудников: " + employeesCount +
                "\n Годовая выручка: " + annualTurnover;
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

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
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
            if (C2 != 0){
                return C2;
            }
            return 0;
        }

    }
}

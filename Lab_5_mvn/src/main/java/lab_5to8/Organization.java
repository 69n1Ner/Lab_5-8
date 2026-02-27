package lab_5to8;

import jakarta.xml.bind.annotation.*;
import jdk.jfr.DataAmount;

import java.time.LocalDate;

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

    public Organization(String name, int annualTurnover, Coordinates coordinates, LocalDate creationDate, long employeesCount, Long id, Address postalAddress, OrganizationType type) {
        this.annualTurnover = annualTurnover;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.employeesCount = employeesCount;
        this.id = id;
        this.name = name;
        this.postalAddress = postalAddress;
        this.type = type;
    }

    public Organization() {
    }

    public void setParams(Organization newOrg){
        this.annualTurnover = newOrg.annualTurnover > 0 ? newOrg.annualTurnover : this.annualTurnover;
        this.coordinates = newOrg.coordinates != null ? newOrg.coordinates : this.coordinates;
        this.employeesCount = newOrg.employeesCount > 0 ? newOrg.employeesCount : this.employeesCount;
        this.name = newOrg.name != null ? newOrg.name : this.name;
        this.postalAddress = newOrg.postalAddress != null ? newOrg.postalAddress : this.postalAddress;
        this.type = newOrg.type != null ? newOrg.type : this.type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    // этот метод вызывать, при добавлении элемента в коллекцию
    public void generateId(){
        //todo сделать генерацию id
    }

    public void generateDate(){
        //todo сделать генерацию даты создания
    }
}

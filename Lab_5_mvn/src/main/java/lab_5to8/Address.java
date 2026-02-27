package lab_5to8;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class Address {
    @XmlElement
    private String zipCode; //Длина строки должна быть не меньше 4, Поле может быть null
    @XmlElementWrapper
    @XmlElement
    private Location town; //Поле не может быть null

    public Address(){}

    public Address(String zipCode, Location town) {
        this.zipCode = zipCode;
        this.town = town;
    }
}
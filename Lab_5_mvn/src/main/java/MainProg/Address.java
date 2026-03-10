package Main;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import java.util.HashMap;
import java.util.Map;

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
    //todo переделать
    @Override
    public String toString() {
        return "Адрес:" +
                "\n\t\t" + town +
                "\n\t\tПочтовый индекс: '" + zipCode +'\'';
    }

    public Address(Address address){
        this(address.zipCode, address.town);
    }

    public void setTown(Location town) {
        this.town = town;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
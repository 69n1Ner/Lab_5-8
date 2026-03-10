package MainProg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class Address {
    @XmlElement
    private String zipCode; //Длина строки должна быть не меньше 4, Поле может быть null
    @XmlElement
    private Location town; //Поле не может быть null

    public Address(){
        this.zipCode = "";
    }

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

    public String getZipCode() {
        return zipCode;
    }

    public Location getTown() {
        return town;
    }

    public void setTown(Location town) {
        this.town = town;
    }

    public void setZipCode(String zipCode) {
        if (zipCode.length() >= 4) {
            this.zipCode = zipCode;
        } else {
            this.zipCode = null;
        }
    }
}
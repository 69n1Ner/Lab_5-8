package OrganizationObject;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "postal_address")
@XmlAccessorType(XmlAccessType.FIELD)
public class Address implements Comparable {
    @XmlElement(name = "zip_code")
    private String zipCode; //Длина строки должна быть не меньше 4, Поле может быть null
    @XmlElement
    private Location town; //Поле не может быть null

    public Address(){
    }

    public Address(String zipCode, Location town) {
        this.zipCode = zipCode;
        this.town = town;
    }
    @Override
    public String toString() {
        return  "\n  Расположение: " + town +
                "\n  Почтовый индекс: " + zipCode;
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
        this.zipCode = zipCode;
    }

    @Override
    public int compareTo(Object o) {
        if (o== null){
            return 1;
        }
        return zipCode.compareTo(((Address) o).getZipCode());
    }
}
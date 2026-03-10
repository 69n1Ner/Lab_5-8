package Main;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class Location {
    @XmlElement
    private Float x; //Поле не может быть null
    @XmlElement
    private Integer y; //Поле не может быть null
    @XmlElement
    private Integer z; //Поле не может быть null
    @XmlElement
    private String name; //Строка не может быть пустой, Поле может быть null

    public Location(String name, Float x, Integer y, Integer z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }
    //todo переделать
    @Override
    public String toString() {
        return "Расположение:" +
                "\n\t\t\tГород: '" + name +'\'' +
                "\n\t\t\tx: " + x +
                "\n\t\t\ty: " + y +
                "\n\t\t\tz: " + z;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setX(String  x) throws NumberFormatException{
        this.x = Float.parseFloat(x);
    }

    public void setY(String  y) throws NumberFormatException{
        this.y = Integer.parseInt(y);
    }

    public void setZ(String  z) throws NumberFormatException{
        this.z = Integer.parseInt(z);
    }
}
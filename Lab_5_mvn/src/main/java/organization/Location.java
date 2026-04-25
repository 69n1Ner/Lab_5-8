package organization;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public Location(){
        this.name = "";
    }
    @Override
    public String toString() {
        return  "\n   Город: " + name +
                "\n   x: " + x +
                "\n   y: " + y +
                "\n   z: " + z;
    }

    public void setName(String name) {
        if (name == null){
            this.name = null;
        }
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }

    public Float getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Integer getZ() {
        return z;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public void setZ(Integer z) {
        this.z = z;
    }


}
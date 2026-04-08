package OrganizationObject;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates {
    @XmlElement
    private long x; //Максимальное значение поля: 623
    @XmlElement
    private Double y; //Поле не может быть null

    public Coordinates(){}

    @Override
    public String toString() {
        return  "\n  x: " + x +
                "\n  y: " + y;
    }

    public Coordinates(Long x, Double y) {
        this.x = x != null ? x : 0;
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public void setX(long x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }


}
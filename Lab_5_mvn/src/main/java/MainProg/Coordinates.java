package Main;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates {
    @XmlElement
    private long x; //Максимальное значение поля: 623
    @XmlElement
    private Double y; //Поле не может быть null

    public Coordinates(){}

    //todo переделать
    @Override
    public String toString() {
        return "Координаты:" +
                "\n\t\tx: " + x +
                "\n\t\ty: " + y;
    }

    public Coordinates(long x, Double y) {
        this.x = x;
        this.y = y;
    }

    public void setY(String  y) {
        this.y = Double.parseDouble(y);
    }

    public void setX(String  x) {
        this.x = Long.parseLong(x);
    }
}
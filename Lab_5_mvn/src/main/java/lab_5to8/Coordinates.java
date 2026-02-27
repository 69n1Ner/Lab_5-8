package lab_5to8;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates {
    @XmlElement
    private long x; //Максимальное значение поля: 623
    @XmlElement
    private Double y; //Поле не может быть null

    public Coordinates(){}

    public Coordinates(long x, Double y) {
        this.x = x;
        this.y = y;
    }
}
package lab_5to8;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

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
}
package Main;

import jakarta.xml.bind.annotation.*;

@XmlEnum
public enum OrganizationType {
    //todo добавить перевод
    @XmlEnumValue("public")
    PUBLIC("Публичная"),
    @XmlEnumValue("government")
    GOVERNMENT("Государственная"),
    @XmlEnumValue("open-joint-stock-company")
    OPEN_JOINT_STOCK_COMPANY("Открытая совместная складская");

    private String name;

    OrganizationType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }



}
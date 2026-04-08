package OrganizationObject;

import jakarta.xml.bind.annotation.*;

@XmlEnum
public enum OrganizationType {
    @XmlEnumValue("public")
    PUBLIC("Публичная"),
    @XmlEnumValue("government")
    GOVERNMENT("Государственная"),
    @XmlEnumValue("open_joint_stock_company")
    OPEN_JOINT_STOCK_COMPANY("Открытая совместная складская");

    private final String name;

    OrganizationType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
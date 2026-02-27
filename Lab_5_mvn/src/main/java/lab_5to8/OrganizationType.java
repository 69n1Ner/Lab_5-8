package lab_5to8;

import jakarta.xml.bind.annotation.*;

@XmlEnum
public enum OrganizationType {
    //todo добавить перевод
    @XmlEnumValue("public")
    PUBLIC,
    @XmlEnumValue("government")
    GOVERNMENT,
    @XmlEnumValue("open-joint-stock-company")
    OPEN_JOINT_STOCK_COMPANY;
}
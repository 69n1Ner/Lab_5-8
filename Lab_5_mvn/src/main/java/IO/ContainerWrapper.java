package IO;

import OrganizationObject.Organization;
import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "collection")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerWrapper {

    @XmlElement(name = "organization")
    private List<Organization> organizations;

    public ContainerWrapper() {
        this.organizations = new ArrayList<>();
    }

    // Конструктор для удобства
    public ContainerWrapper(List<Organization> organizations) {
        this.organizations = organizations != null ? organizations : new ArrayList<>();
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }
}
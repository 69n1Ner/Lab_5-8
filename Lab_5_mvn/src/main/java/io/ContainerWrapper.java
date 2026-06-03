package io;

import organization.Organization;
import jakarta.xml.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "collection")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerWrapper implements Serializable {

    @XmlElement(name = "organization")
    private final List<Organization> organizations;

    public ContainerWrapper() {
        this.organizations = new ArrayList<>();
    }



    public List<Organization> getOrganizations() {
        return organizations;
    }

}
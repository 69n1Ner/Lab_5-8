package io;

import organization.Address;
import organization.Organization;

import java.util.List;

public record ObjWithFeedback(
        Organization organization,
        Address address,
        List<String> feedback
) {
    public ObjWithFeedback setOrganization(Organization organization){
        return new ObjWithFeedback(organization,address,feedback);
    }
    public ObjWithFeedback setAddress(Address address){
        return new ObjWithFeedback(organization,address,feedback);
    }
}

package io;

import organization.Organization;

import java.util.ArrayList;
import java.util.List;

public record OrganizationWithFeedback(
        Organization organization,
        List<String> feedback
) {
    public OrganizationWithFeedback setOrganization(Organization organization){
        return new OrganizationWithFeedback(organization,feedback);
    }
}

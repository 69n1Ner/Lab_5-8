package main;

import exceptions.NoSuchEntityException;
import exceptions.NoSuchOrganizationException;
import exceptions.NoSuchUserException;
import organization.Organization;
import security.User;

import java.util.Comparator;
import java.util.Optional;

public class OrganizationContainer extends Container<Organization> {
    public OrganizationContainer(Comparator<Organization> comparator) {
        super(comparator);
    }

    @Override
    public Organization getById(Long id) throws NoSuchEntityException {
        Optional<Organization> optional = container.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();

        if (optional.isEmpty()) {
            throw new NoSuchOrganizationException();
        } else return optional.get();
    }
}

package main;

import organization.Organization;

public interface IdGettable<T> extends NseeCreatable, UpdatableEntity<T> {
    Long getId();
}

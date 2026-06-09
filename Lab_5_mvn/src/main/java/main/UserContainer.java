package main;

import exceptions.NoSuchEntityException;
import exceptions.NoSuchUserException;
import organization.Organization;
import security.User;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Optional;

public class UserContainer extends Container<User> {
    public UserContainer(Comparator<User> comparator) {
        super(comparator);
    }

    @Override
    public User getById(Long id) throws NoSuchEntityException {
        Optional<User> optional = container.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();

        if (optional.isEmpty()) {
            throw new NoSuchUserException();
        } else return optional.get();
    }

    public User findByUserName(String userName) throws NoSuchEntityException {
        Optional<User> optional = container.stream().filter(user -> user.getUserName().equals(userName)).findFirst();
        if (optional.isEmpty()){
            User user = new User();
            throw user.createNsee();
        } else {
            return optional.get();
        }
    }
}

package security;

import exceptions.NoSuchEntityException;
import exceptions.NoSuchUserException;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import main.IdGettable;

import java.io.Serializable;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class User implements IdGettable<User>, Serializable {
    private Long id;
    private String password;
    @XmlElement(name = "user_name")
    private String userName;

    private static final long serialVersionUID = 1L;

    public User(){}

    public User(Long id, String password, String userName) {
        this.id = id;
        this.password = password;
        this.userName = userName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public NoSuchEntityException createNsee() {
        return new NoSuchUserException();
    }

    @Override
    public User update(User user) {
        return setId(user.getId())
                .setUserName(user.getUserName())
                .setPassword(user.getPassword());
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    @Override
    public String toString() {
        return userName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(password, user.password) && Objects.equals(userName, user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, password, userName);
    }
}

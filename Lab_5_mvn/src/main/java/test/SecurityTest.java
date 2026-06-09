package test;

import db.OrganizationDao;
import db.UserDao;
import io.InputManager;
import security.MD2Hash;
import security.User;

import java.security.NoSuchAlgorithmException;

public class SecurityTest {
    public static void main(String[] args) throws NoSuchAlgorithmException {
//        try {
//            String s = MD2Hash.hashWithMD2("hi");
//            System.out.println(s);
        UserDao.getInstance().save(new User().setUserName("server").setPassword(MD2Hash.hashWithMD2("server")),null);
//        System.out.println(InputManager.authorize());
//        OrganizationDao.getInstance().save(InputManager.inputOrganization(), );
        System.out.println(OrganizationDao.getInstance().findAll());
//        System.out.println(UserDao.getInstance().findAll());
//            System.out.println(UserDao.getInstance().findAll());
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Алгоритм MD2 не поддерживается этой JVM", e);
//        }

    }
}

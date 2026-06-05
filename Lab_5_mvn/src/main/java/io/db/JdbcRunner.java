package io.db;

import exceptions.NoSuchOrganizationException;
import io.InputManager;
import io.ObjWithFeedback;
import organization.Address;
import organization.Coordinates;
import organization.Location;
import organization.Organization;

import java.sql.*;
import java.time.LocalDate;

public class JdbcRunner {
    public static void main(String[] args) {
        OrganizationDao organizationDao = OrganizationDao.getInstance();
        organizationDao.clear();
//        organizationDao.delete(1L);
//        organizationDao.delete(2L);
//        organizationDao.delete(3L);
//        organizationDao.delete(4L);
//        organizationDao.delete(4L);
        Organization organization = InputManager.inputOrganization(true);

        try {
            organizationDao.update(organization,3L);
        } catch (NoSuchOrganizationException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(organizationDao.findAll());
    }




}

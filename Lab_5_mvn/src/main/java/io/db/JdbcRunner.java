package io.db;

import io.InputManager;
import organization.Address;
import organization.Coordinates;
import organization.Location;
import organization.Organization;

import java.sql.*;
import java.time.LocalDate;

public class JdbcRunner {
    public static void main(String[] args) {

        Organization organization = InputManager.inputOrganization(false);
        InputManager.generateOrganizationFields(organization,false);
    }




}

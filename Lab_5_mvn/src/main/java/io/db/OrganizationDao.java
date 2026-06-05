package io.db;

import exceptions.NoSuchOrganizationException;
import main.Container;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jaxb.core.v2.model.core.ID;
import organization.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrganizationDao implements Dao<Organization>{
    private static final Container<Organization> CONTAINER = Container.getInstance();
    private static final OrganizationDao INSTANCE = new OrganizationDao();


        //language=POSTGRES-SQL
        private static final String SELECT_ORGANIZATIONS_SQL = """
                select
                    "annual_turnover",
                    "coordinates_x",
                    "coordinates_y",
                    "creation_date",
                    "employees_count",
                    o."id",
                    "location_name",
                    "location_x",
                    "location_y",
                    "location_z",
                    o."name",
                    ot.name as type_name,
                    "zip_code"
                from organization o
                join organization_type ot on o.type_id = ot.id""";

    //language=POSTGRES-SQL
    private static final String SAVE_ORGANIZATION_SQL = """
                    insert into organization (
                        "annual_turnover",
                        "coordinates_x",
                        "coordinates_y",
                        "creation_date",
                        "employees_count",
                        "location_name",
                        "location_x",
                        "location_y",
                        "location_z",
                        "name",
                        "type_id",
                        "zip_code")
                    values (?,?,?,?,?,?,?,?,?,?,?,?)
                    """;
    //language=POSTGRES-SQL
    private static final String UPDATE_ORGANIZATION_SQL = """
                    update organization
                    set "annual_turnover" = ?,
                        "coordinates_x" = ?,
                        "coordinates_y" = ?,
                        "creation_date" = ?,
                        "employees_count" = ?,
                        "location_name" = ?,
                        "location_x" = ?,
                        "location_y" = ?,
                        "location_z" = ?,
                        "name" = ?,
                        "type_id" = ?,
                        "zip_code" = ?
                    where id = ?
                    """;

    //language=POSTGRES-SQL
    private static final String DELETE_ORGANIZATION_SQL= """
                    delete from organization
                    where id = ?
                    """;

    private static final Logger log = LogManager.getLogger(OrganizationDao.class);

    @Override
    public int save(Organization organization) {
        try (Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(SAVE_ORGANIZATION_SQL,Statement.RETURN_GENERATED_KEYS)){
            connection.setAutoCommit(false);

            LocalDate localDate = organization.getCreationDate();
            String name = organization.getName();
            long employeesCount = organization.getEmployeesCount();
            int annualTurnover = organization.getAnnualTurnover();
            int typeID = getType(connection,organization.getType().getName());

            Address address = organization.getPostalAddress();
            String zipCode = address.getZipCode();

            Location location = address.getTown();
            Float x = location.getX();
            Integer y = location.getY();
            Integer z = location.getZ();
            String locationName = location.getName();

            Coordinates coordinates = organization.getCoordinates();
            long coordinateX = coordinates.getX();
            Double coordinateY = coordinates.getY();

            int organizationID = 0;
            statement.setInt(1,annualTurnover);
            statement.setLong(2,coordinateX);
            statement.setDouble(3,coordinateY);
            statement.setDate(4, Date.valueOf(localDate));
            statement.setLong(5,employeesCount);
            statement.setString(6,locationName);
            statement.setFloat(7,x);
            statement.setInt(8,y);
            statement.setInt(9,z);
            statement.setString(10,name);
            statement.setInt(11,typeID);
            statement.setString(12,zipCode);

            statement.executeUpdate();


            try (ResultSet resultSet = statement.getGeneratedKeys()){

                if (resultSet.next()){
                    organizationID = resultSet.getInt(1);
                }else {
                    connection.rollback();
                    throw new SQLException("Не удалось вытащить ID у организации");
                }
            }
            connection.commit();
            organization.setId((long) organizationID);
            CONTAINER.add(organization);
            return organizationID;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean update(Organization organization, Long ID) throws NoSuchOrganizationException {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(UPDATE_ORGANIZATION_SQL,Statement.RETURN_GENERATED_KEYS)){
            connection.setAutoCommit(false);

            organization = findById(ID).update(organization);

            LocalDate localDate = organization.getCreationDate();
            String name = organization.getName();
            long employeesCount = organization.getEmployeesCount();
            int annualTurnover = organization.getAnnualTurnover();
            int typeID = getType(connection,organization.getType().getName());

            Address address = organization.getPostalAddress();
            String zipCode = address.getZipCode();

            Location location = address.getTown();
            Float x = location.getX();
            Integer y = location.getY();
            Integer z = location.getZ();
            String locationName = location.getName();

            Coordinates coordinates = organization.getCoordinates();
            long coordinateX = coordinates.getX();
            Double coordinateY = coordinates.getY();

            int organizationID = 0;
            statement.setInt(1,annualTurnover);
            statement.setLong(2,coordinateX);
            statement.setDouble(3,coordinateY);
            statement.setDate(4, Date.valueOf(localDate));
            statement.setLong(5,employeesCount);
            statement.setString(6,locationName);
            statement.setFloat(7,x);
            statement.setInt(8,y);
            statement.setInt(9,z);
            statement.setString(10,name);
            statement.setInt(11,typeID);
            statement.setString(12,zipCode);
            statement.setLong(13,ID);

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()){
                if (resultSet.next()){
                    organizationID = resultSet.getInt(1);
                }else {
                    connection.rollback();
                    throw new SQLException("Не удалось вытащить ID у организации");
                }
            }
            connection.commit();
            CONTAINER.update(organization,ID);
            return organizationID > 0;


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Long id) throws NoSuchOrganizationException {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(DELETE_ORGANIZATION_SQL)) {
            connection.setAutoCommit(false);

            statement.setLong(1, id);

            int deletedRows = statement.executeUpdate();

            if (deletedRows > 0) {
                connection.commit();
                CONTAINER.removeById(id);
                return true;
            } else {
                connection.rollback();
                return false;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int clear(){
        List<Organization> list = findAll();
        int counter = 0;
        for (Organization organization : list){
            long id = organization.getId();
            boolean isDeleted = false;
            try {
                isDeleted = delete(id);

            if (isDeleted){
               counter++;
            }
            // it's just easier, that's why no processing
            } catch (NoSuchOrganizationException e) {
            }
        }
        return counter;
    }

    @Override
    public List<Organization> findAll() {
        return CONTAINER.getAll();
    }

    @Override
    public Organization findById(Long id) throws NoSuchOrganizationException {
        return CONTAINER.getById(id);
    }



    private static int getType(Connection connection,String locationType){
        try  {
            int locationTypeId = 0;
            //language=POSTGRES-SQL
            String sql = """
                    select id from organization_type
                    where name = ?""";
            PreparedStatement psType = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psType.setString(1,locationType);

            psType.executeQuery();
            try (ResultSet resultSet = psType.getResultSet()){
                if (resultSet.next()){
                    locationTypeId = resultSet.getInt(1);
                }else {
                    throw new SQLException("Не удалось вытащить ID у типа");
                }
            }
            return locationTypeId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private OrganizationDao() {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement psOrganization = connection.prepareStatement(SELECT_ORGANIZATIONS_SQL,Statement.RETURN_GENERATED_KEYS)){
            psOrganization.executeQuery();

            try (ResultSet resultSet = psOrganization.getResultSet()){
                List<Organization> organizations = new ArrayList<>();
                while (resultSet.next()){
                    Organization organization = new Organization();
                    organization.setId(resultSet.getLong("id"));
                    organization.setCreationDate(resultSet.getDate("creation_date").toLocalDate());
                    organization.setName(resultSet.getString("name"));
                    organization.setType(OrganizationType.ofName(resultSet.getString("type_name")));
                    organization.setEmployeesCount(resultSet.getLong("employees_count"));
                    organization.setAnnualTurnover(resultSet.getInt("annual_turnover"));

                    Coordinates coordinates = new Coordinates();
                    coordinates.setX(resultSet.getLong("coordinates_x"));
                    coordinates.setY(resultSet.getDouble("coordinates_y"));
                    organization.setCoordinates(coordinates);

                    Location location = new Location();
                    location.setX(resultSet.getFloat("location_x"));
                    location.setY(resultSet.getInt("location_y"));
                    location.setZ(resultSet.getInt("location_z"));
                    location.setName(resultSet.getString("location_name"));

                    Address address = new Address();
                    address.setZipCode(resultSet.getString("zip_code"));
                    address.setTown(location);
                    organization.setPostalAddress(address);
                    organizations.add(organization);
                }
                CONTAINER.addList(organizations);
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static OrganizationDao getInstance() {
        return INSTANCE;
    }

}

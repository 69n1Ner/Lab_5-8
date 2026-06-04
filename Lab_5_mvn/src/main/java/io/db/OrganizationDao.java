package io.db;

import exceptions.NoSuchOrganizationException;
import main.Container;
import org.glassfish.jaxb.core.v2.model.core.ID;
import organization.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrganizationDao implements Dao<Organization>{
    private static final OrganizationDao INSTANCE = new OrganizationDao();
    private static final Container<Organization> CONTAINER = Container.getInstance();

        //language=POSTGRES-SQL
        private static final String SELECT_ORGANIZATIONS_SQL = """
                    select o.id,
                           o.creation_date,
                           o.name,
                           o.employees_count,
                           o.annual_turnover,
                           c.x,
                           c.y,
                           a.zip_code,
                           l.name,
                           l.x,
                           l.y,
                           l.z,
                           ot.name
                    from organization o
                    join coordinates c on c.id = coordinates_id
                    join address a on a.id = postal_address_id
                    join location l on a.location_id = l.id
                    join organization_type ot on ot.id = o.type_id
                    """;

    //language=POSTGRES-SQL
    private static final String SAVE_ORGANIZATION_SQL = """
                    insert into organization (
                                creation_date
                                ,"name"
                                ,type_id
                                ,coordinates_id
                                ,postal_address_id
                                ,employees_count
                                ,annual_turnover)
                            values (?,?,?,?,?,?,?)
                    """;
    //language=POSTGRES-SQL
    private static final String UPDATE_ORGANIZATION_SQL = """
                    update organization
                            set creation_date = ?
                                ,"name" = ?
                                ,type_id = ?
                                ,coordinates_id = ?
                                ,postal_address_id = ?
                                ,employees_count = ?
                                ,annual_turnover = ?
                            where id = ?
                    """;

    //language=POSTGRES-SQL
    private static final String DELETE_ORGANIZATION_SQL= """
                    delete from organization
                    where id = ?
                    """;

    //language=POSTGRES-SQL
    private static final String SAVE_COORDINATES_SQL= """
                    insert into coordinates (x,y)
                    values (?,?)
                    """;
    //language=POSTGRES-SQL
    private static final String SAVE_LOCATION_SQL="""
                    insert into "location" (x,y,z,"name")
                    values (?,?,?,?)
                    """;

    //language=POSTGRES-SQL
    private static final String SAVE_ADDRESS_SQL="""
                    insert into address (zip_code,location_id)
                    values (?,?)
                    """;

    //language=POSTGRES-SQL
    private static final String UPDATE_COORDINATES_SQL= """
                    update coordinates
                    set x = ?,
                        y = ?
                    where id = ?
                    """;

    //language=POSTGRES-SQL
    private static final String UPDATE_LOCATION_SQL= """
                    update "location"
                    set "name" = ?,
                        x = ?,
                        y = ?,
                        z = ?
                    where id = ?
                    """;

    //language=POSTGRES-SQL
    private static final String UPDATE_ADDRESS_SQL= """
                    update address
                    set location_id = ?,
                        zip_code = ?
                    where id = ?
                    """;



    @Override
    public int save(Organization organization) {
        try (Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(SAVE_ORGANIZATION_SQL,Statement.RETURN_GENERATED_KEYS)){
            connection.setAutoCommit(false);

            LocalDate localDate = organization.getCreationDate();
            String name = organization.getName();
            int typeID = getType(connection,organization.getType().toString());
            Coordinates coordinates = organization.getCoordinates();
            int coordinateID = setCoordinates(connection,SAVE_COORDINATES_SQL,coordinates.getX(),coordinates.getY());
            Address address = organization.getPostalAddress();
            Location location = address.getTown();
            int locationID = setLocation(connection,SAVE_LOCATION_SQL,location.getX(),location.getY(),location.getZ(),location.getName());
            int addressID = setAddress(connection,address.getZipCode(),SAVE_ADDRESS_SQL,locationID);
            long employeesCount = organization.getEmployeesCount();
            int annualTurnover = organization.getAnnualTurnover();

            int organizationID = 0;
            statement.setDate(1, Date.valueOf(localDate));
            statement.setString(2,name);
            statement.setInt(3,typeID);
            statement.setInt(4,coordinateID);
            statement.setInt(5,addressID);
            statement.setLong(6,employeesCount);
            statement.setLong(7,annualTurnover);

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
            int typeID = getType(connection,organization.getType().toString());
            Coordinates coordinates = organization.getCoordinates();
            int coordinateID = setCoordinates(connection,UPDATE_COORDINATES_SQL,coordinates.getX(),coordinates.getY());
            Address address = organization.getPostalAddress();
            Location location = address.getTown();
            int locationID = setLocation(connection,UPDATE_LOCATION_SQL,location.getX(),location.getY(),location.getZ(),location.getName());
            int addressID = setAddress(connection,address.getZipCode(),UPDATE_ADDRESS_SQL,locationID);
            long employeesCount = organization.getEmployeesCount();
            int annualTurnover = organization.getAnnualTurnover();

            int organizationID = 0;
            statement.setDate(1, Date.valueOf(localDate));
            statement.setString(2,name);
            statement.setInt(3,typeID);
            statement.setInt(4,coordinateID);
            statement.setInt(5,addressID);
            statement.setLong(6,employeesCount);
            statement.setLong(7,annualTurnover);
            statement.setLong(8,ID);

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
            return organizationID > 0;


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(DELETE_ORGANIZATION_SQL,Statement.RETURN_GENERATED_KEYS)){
            connection.setAutoCommit(false);

            statement.setLong(1, id);

            int organizationID = 0;

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
            return organizationID > 0;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
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

    private static int setCoordinates(Connection connection,String SQL,long x, Double y){
        try (PreparedStatement psCoordinates = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            int coordiantesId = 0;

            psCoordinates.setLong(1,x);
            psCoordinates.setDouble(2,y);

            psCoordinates.executeUpdate();
            try (ResultSet resultSet = psCoordinates.getGeneratedKeys()){
                if (resultSet.next()){
                    coordiantesId = resultSet.getInt(1);
                }else {
                    throw new SQLException("Не удалось вытащить ID у координат");
                }
            }
            return coordiantesId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static int setLocation(Connection connection,String SQL,Float x, Integer y, Integer z, String name){
        try (PreparedStatement psLocation = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);) {
            int locationId = 0;

            psLocation.setFloat(1,x);
            psLocation.setInt(2,y);
            psLocation.setInt(3,z);
            psLocation.setString(4,name);

            psLocation.executeUpdate();
            try (ResultSet resultSet = psLocation.getGeneratedKeys()){
                if (resultSet.next()){
                    locationId = resultSet.getInt(1);
                }else {
                    throw new SQLException("Не удалось вытащить ID у локации");
                }
            }
            return locationId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static int setAddress(Connection connection,
                                  String SQL,
                                  String zipCode,
                                  int locationId){
        try (PreparedStatement psAddress = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);) {
            int addressId = 0;

            psAddress.setString(1,zipCode);
            psAddress.setInt(2,locationId);

            psAddress.executeUpdate();
            try (ResultSet resultSet = psAddress.getGeneratedKeys()){
                if (resultSet.next()){
                    addressId = resultSet.getInt(1);
                }else {
                    throw new SQLException("Не удалось вытащить ID у адреса");
                }
            }
            return addressId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private OrganizationDao() {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement psOrganization = connection.prepareStatement(SELECT_ORGANIZATIONS_SQL)){
            psOrganization.executeQuery();

            try (ResultSet resultSet = psOrganization.getResultSet()){
                List<Organization> organizations = new ArrayList<>();
                while (resultSet.next()){
                    Organization organization = new Organization();
                    organization.setId(resultSet.getLong("o.id"));
                    organization.setCreationDate(resultSet.getDate("o.creation_date").toLocalDate());
                    organization.setName(resultSet.getString("o.name"));
                    organization.setType(OrganizationType.ofName(resultSet.getString("ot.name")));
                    organization.setEmployeesCount(resultSet.getLong("o.employees_count"));
                    organization.setAnnualTurnover(resultSet.getInt("o.annual_turnover"));

                    Coordinates coordinates = new Coordinates();
                    coordinates.setX(resultSet.getLong("c.x"));
                    coordinates.setY(resultSet.getDouble("c.y"));
                    organization.setCoordinates(coordinates);

                    Location location = new Location();
                    location.setX(resultSet.getFloat("l.x"));
                    location.setY(resultSet.getInt("l.y"));
                    location.setZ(resultSet.getInt("l.z"));
                    location.setName(resultSet.getString("l.name"));

                    Address address = new Address();
                    address.setZipCode(resultSet.getString("a.zip_code"));
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

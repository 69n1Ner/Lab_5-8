package db;

import exceptions.NoSuchEntityException;
import main.Container;
import main.OrganizationContainer;
import net.Runner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.PSQLException;
import organization.*;
import security.User;
import sorts.SortById;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class OrganizationDao implements Dao<Organization>{
    private static final OrganizationContainer CONTAINER = new OrganizationContainer(new SortById<>());
    private static final OrganizationDao INSTANCE = new OrganizationDao();
    private static Runner runner;

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
                    "zip_code",
                    user_id
                from organization o
                join organization_type ot on o.type_id = ot.id
                join users u on u.id = user_id
                """;

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
                        "zip_code",
                        "user_id")
                    values (?,?,?,?,?,?,?,?,?,?,?,?,?)
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
                        "zip_code" = ?,
                        "user_id" = ?
                    where id = ?
                    """;

    //language=POSTGRES-SQL
    private static final String DELETE_ORGANIZATION_SQL= """
                    delete from organization
                    where id = ?
                    """;

    private static final Logger log = LogManager.getLogger(OrganizationDao.class);

    @Override
    public int save(Organization organization, User user) {
        try (Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(SAVE_ORGANIZATION_SQL,Statement.RETURN_GENERATED_KEYS)){
            connection.setAutoCommit(false);

            //searching for user
            UserDao userDao = UserDao.getInstance();
            long userId;
            try {
                userDao.findById(user.getId());
                userId = user.getId();
            } catch (NoSuchEntityException | NullPointerException e) {
                log.info("Такого пользователя не нашлось, организация не будет добавлена");
                return -1;
            }

            //adding other attributes
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
            statement.setLong(13,userId);

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
            organization.setUser(UserDao.getInstance().findById(userId));
            log.debug("организация после добавления, org={}",organization);
            CONTAINER.add(organization);
            return organizationID;

        }catch (SQLException e){
            if (e instanceof PSQLException) {
                log.warn(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return 0;
        } catch (NoSuchEntityException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean update(Organization organization, Long ID, User user) throws NoSuchEntityException {
        organization = findById(ID).update(organization);

        log.debug("до проверки на корректного юзера");
        if (!isCorrectUser(user,false, organization)) return false;
        log.debug("после проверки на корректного юзера");

        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(UPDATE_ORGANIZATION_SQL,Statement.RETURN_GENERATED_KEYS)){
            connection.setAutoCommit(false);




            LocalDate localDate = organization.getCreationDate();
            String name = organization.getName();
            long employeesCount = organization.getEmployeesCount();
            int annualTurnover = organization.getAnnualTurnover();
            int typeID = getType(connection,organization.getType().getName());

            long userId = getUserID(connection,user.getUserName(),user.getPassword());
            log.debug("после получения user_id={}",userId);

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

            int organizationID;
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
            statement.setLong(13,userId);
            statement.setLong(14,ID);
            log.debug("statement={}",statement);

            statement.executeUpdate();
            log.debug("после выполнения");

            try (ResultSet resultSet = statement.getGeneratedKeys()){
                if (resultSet.next()){
                    organizationID = resultSet.getInt("id");
                    log.debug("organizationID={}",organizationID);
                    connection.commit();
                    CONTAINER.update(organization,ID);
                    return organizationID > 0;
                }else {
                    connection.rollback();
                    throw new SQLException("Не удалось вытащить ID у организации");
                }
            }

        }catch (SQLException e){
            if (e instanceof PSQLException) {
                log.warn(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return false;
        }
    }

    private static boolean isCorrectUser(User user, boolean isClearCommand, Organization organization) {
        try {
            User user1 = UserDao.getInstance().findById(organization.getUser().getId());
            log.debug("user1={}",user1);
            log.debug("user={}",user);

            if (!user1.equals(user)){
                //todo добавить Optional для вывода сообщения или что то другое
                if (!isClearCommand) log.warn("Вы не можете редактировать эту организацию");
                return false;
            }

        } catch (NoSuchEntityException e) {
            log.debug(NoSuchEntityException.getMsg());
            return false;
        }
        return true;
    }

    private boolean delete(Long id, User user,boolean isClearCommand) throws NoSuchEntityException {

        log.debug("до delete проверки на корректного юзера (org id = {})", id);
        if (!isCorrectUser(user,isClearCommand, CONTAINER.getById(id))) return false;
        log.debug("после delete проверки на корректного юзера");

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
            if (e instanceof PSQLException) {
                log.warn(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return false;
        }

    }

    @Override
    public boolean delete(Long id, User user) throws NoSuchEntityException {
        return delete(id,user,false);
    }

    public int clear(User user){
        List<Organization> list = findAll();
        int counter = 0;
        for (Organization organization : list){
            long id = organization.getId();
            boolean isDeleted;
            try {
                isDeleted = delete(id, user,true);

            if (isDeleted){
               counter++;
            }
            // it's just easier, that's why no processing
            } catch (NoSuchEntityException e) {
            }
        }
        return counter;
    }

    @Override
    public List<Organization> findAll() {
        return CONTAINER.getAll();
    }

    @Override
    public Organization findById(Long id) throws NoSuchEntityException {
        return CONTAINER.getById(id);
    }

    public static Class<?> getContainerCollectionName(){
        return Arrays.stream(CONTAINER
                .getClass()
                        .getSuperclass()
                .getDeclaredFields())
                .filter(t -> Collection.class.isAssignableFrom(t.getType()))
                .findFirst().get().getType();
    }

    public static LocalDate getContainerCreationDate(){
        return CONTAINER.getCreationDate();
    }

    public static int getContainerSize(){
        return CONTAINER.size();
    }

    private static int getType(Connection connection,String locationType){
        try  {
            int locationTypeId = 0;
            //language=POSTGRES-SQL
            String sql = """
                    select id from organization_type
                    where name = ?""";
            try (PreparedStatement psType = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                psType.setString(1, locationType);

                psType.executeQuery();
                try (ResultSet resultSet = psType.getResultSet()) {
                    if (resultSet.next()) {
                        locationTypeId = resultSet.getInt(1);
                    } else {
                        throw new SQLException("Не удалось вытащить ID у типа");
                    }
                }
                return locationTypeId;
            }
        } catch (SQLException e) {
            if (e instanceof PSQLException) {
                log.warn(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return -1;
        }
    }

    private static long getUserID(Connection connection, String userName, String password){
        //language=POSTGRES-SQL
        String sql = """
                select id from users
                where user_name = ? and password = ?
                """;
        try (PreparedStatement psUser = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            long id;

            psUser.setString(1, userName);
            psUser.setString(2,password);

            psUser.executeQuery();

            try (ResultSet resultSet = psUser.getResultSet()) {
                if (resultSet.next()) {
                    id = resultSet.getInt("id");
                    return id;
                } else {
                    throw new SQLException("Не удалось вытащить ID у пользователя");
                }
            }
        } catch (SQLException e) {
            if (e instanceof PSQLException) {
                log.warn(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return -1;
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

                    UserDao userDao = UserDao.getInstance();
                    try {
                        User user = userDao.findById(resultSet.getLong("user_id"));
                        organization.setUser(user);
                    } catch (NoSuchEntityException e) {
                        throw new RuntimeException(e);
                    }

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
            if (e instanceof PSQLException) {
                log.warn(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
        }
    }

    public static OrganizationDao getInstance() {
        return INSTANCE;
    }


    public static Runner getRunner() {
        return runner;
    }

    public static void setRunner(Runner runner) {
        OrganizationDao.runner = runner;
    }

}

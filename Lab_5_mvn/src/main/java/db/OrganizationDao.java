package db;

import exceptions.NoSuchEntityException;
import io.ObjWithFeedback;
import main.OrganizationContainer;
import net.Runner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.PSQLException;
import organization.*;
import security.User;
import sorts.SortById;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

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
    public ObjWithFeedback<Integer> save(Organization organization, User user) {
        ObjWithFeedback<Integer> ans = new ObjWithFeedback<>(-1,new ArrayList<>());
        StringBuilder feedback = new StringBuilder();
        try (Connection connection = ConnectionManager.open();
                PreparedStatement statement = connection.prepareStatement(SAVE_ORGANIZATION_SQL,Statement.RETURN_GENERATED_KEYS)){
            connection.setAutoCommit(false);

            //searching for user
            UserDao userDao = UserDao.getInstance();
            long userId;
            try {
                userDao.findById(user.getId());
                userId = user.getId();
            } catch ( NullPointerException e) {
                log.debug("не нашел юзера");
                feedback.append("Такого пользователя не нашлось, организация не будет добавлена");
                return ans.addFeedback(feedback.toString());
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
                    connection.commit();
                    organization.setId((long) organizationID);

                    ObjWithFeedback<User> u = UserDao.getInstance().findById(userId);
                    User user1 = u.object();
                    List<String> lu = u.feedback();
                    if (!lu.isEmpty()){
                        log.debug("lu={}",lu);
                        for (String s:lu){
                            if (s.isEmpty()) break;

                            feedback.append(s);
                        }
                        return ans.addFeedback(feedback.toString());
                    }
                    organization.setUser(user1);

//                    log.debug("организация после добавления, org={}",organization);
                    CONTAINER.add(organization);
//                    log.debug("organizationID={}",organizationID);
                    return ans.setObject(organizationID);

                }else {
                    connection.rollback();
                    throw new SQLException("Не удалось вытащить ID у организации");
                }
            }

        }catch (SQLException e){
            if (e instanceof PSQLException) {
                feedback.append(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return ans.addFeedback(feedback.toString());
//        } catch (NoSuchEntityException e) {
//            throw new RuntimeException(e);
        }
    }


    @Override
    public ObjWithFeedback<Boolean> update(Organization organization, Long ID, User user) throws NoSuchEntityException {
        ObjWithFeedback<Boolean> ans = new ObjWithFeedback<>(false,new ArrayList<>());
        StringBuilder feedback = new StringBuilder();

        ObjWithFeedback<Organization> o = findById(ID);
        Organization organization1 = o.object();
        List<String> lo = o.feedback();
        if (!lo.isEmpty()){
            for (String s: lo){
                if (s.isEmpty()) break;

                feedback.append(s);
            }
            return ans.addFeedback(feedback.toString());
        }
        organization = organization1.update(organization);

//        log.debug("до проверки на корректного юзера");
        ObjWithFeedback<Boolean> b = isCorrectUser(user,false,organization);
        boolean isCorrectUser = b.object();
        List<String> lb = b.feedback();
        if (!lb.isEmpty()){
            for (String s:lb){
                if (s.isEmpty()) break;

                feedback.append(s);
            }
            return ans.addFeedback(feedback.toString());
        }

        if (!isCorrectUser) return ans;
//        log.debug("после проверки на корректного юзера");

        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(UPDATE_ORGANIZATION_SQL,Statement.RETURN_GENERATED_KEYS)){
            connection.setAutoCommit(false);

            LocalDate localDate = organization.getCreationDate();
            String name = organization.getName();
            long employeesCount = organization.getEmployeesCount();
            int annualTurnover = organization.getAnnualTurnover();
            int typeID = getType(connection,organization.getType().getName());

            ObjWithFeedback<Long> uId = getUserID(connection,user.getUserName(),user.getPassword());
            long userId = uId.object();
            List<String> luId = uId.feedback();
            if (!luId.isEmpty()){
                for (String s:luId){
                    if (s.isEmpty()) break;

                    feedback.append(s);
                }
                return ans.addFeedback(feedback.toString());
            }

//            log.debug("после получения user_id={}",userId);

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

            int isUpdated;
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
//            log.debug("statement={}",statement);

            statement.executeUpdate();
//            log.debug("после выполнения");

            try (ResultSet resultSet = statement.getGeneratedKeys()){
                if (resultSet.next()){
                    isUpdated = resultSet.getInt("id");
//                    log.debug("isUpdated={}",isUpdated);
                    connection.commit();
                    CONTAINER.update(organization,ID);
                    return ans.setObject(isUpdated > 0);
                }else {
                    connection.rollback();
                    throw new SQLException("Не удалось вытащить ID у организации");
                }
            }

        }catch (SQLException e){
            if (e instanceof PSQLException) {
                feedback.append(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return ans.addFeedback(feedback.toString());
        }
    }

    private static ObjWithFeedback<Boolean> isCorrectUser(User user, boolean isClearCommand, Organization organization) {
        ObjWithFeedback<Boolean> ans = new ObjWithFeedback<>(false,new ArrayList<>());
        StringBuilder feedback = new StringBuilder();
            ObjWithFeedback<User> u = UserDao.getInstance().findById(organization.getUser().getId());
            User user1 = u.object();
            List<String> lu = u.feedback();
            if(!lu.isEmpty()){
                log.debug("lu={}",lu);
                for (String s:lu){
                    if (s.isEmpty()) break;

                    feedback.append(s);
                }
                return ans.addFeedback(feedback.toString());
            }
            log.debug("user correct");

//            log.debug("user1={}",user1);
//            log.debug("user={}",user);

            if (!user1.equals(user)){
                if (!isClearCommand) feedback.append("Вы не можете редактировать эту организацию");
                log.debug("feedback='{}'",feedback);
                return ans.addFeedback(feedback.toString());
            }
            log.debug("user correct1");


        return ans.setObject(true);
    }

    public ObjWithFeedback<Boolean> delete(Long id, User user, boolean isClearCommand) throws NoSuchEntityException {
        ObjWithFeedback<Boolean> ans = new ObjWithFeedback<>(false,new ArrayList<>());
        StringBuilder feedback = new StringBuilder();
//        log.debug("до delete проверки на корректного юзера (org id = {})", id);

        ObjWithFeedback<Boolean> b = isCorrectUser(user,isClearCommand, CONTAINER.getById(id));
        boolean isCorrectUser = b.object();
        List<String> lb = b.feedback();
        if(!lb.isEmpty()){
            for (String s:lb){
                if (s.equals("")) break;
                log.debug("s='{}'",s);
                log.debug("lb={}",lb);
                feedback.append(s);
            }
            return ans.addFeedback(feedback.toString());
        }


        if (!isCorrectUser) return ans;
//        log.debug("после delete проверки на корректного юзера");

        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(DELETE_ORGANIZATION_SQL)) {
            connection.setAutoCommit(false);

            statement.setLong(1, id);

            int deletedRows = statement.executeUpdate();

            if (deletedRows > 0) {
                connection.commit();
                CONTAINER.removeById(id);
                return ans.setObject(true);
            } else {
                connection.rollback();
                return ans;
            }

        } catch (SQLException e) {
            if (e instanceof PSQLException) {
                feedback.append(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return ans.addFeedback(feedback.toString());
        }

    }

    @Override
    public ObjWithFeedback<Boolean> delete(Long id, User user) throws NoSuchEntityException {
        return delete(id,user,false);
    }

    public ObjWithFeedback<Integer> clear(User user){
        ObjWithFeedback<Integer> ans = new ObjWithFeedback<>(-1,new ArrayList<>());
        StringBuilder feedback = new StringBuilder();
        List<Organization> list = findAll();
        int counter = 0;
        for (Organization organization : list){
            long id = organization.getId();
            boolean isDeleted;
            try {
                ObjWithFeedback<Boolean> b = delete(id, user,true);
                isDeleted = b.object();
                List<String> lb = b.feedback();
                if (!lb.isEmpty()){
                    for (String s:lb){
                        if (s.equals("")) break;
                        log.debug("lb={}",lb);

                        feedback.append(s);
                    }
                    return ans.setObject(counter).addFeedback(feedback.toString());
                }

            if (isDeleted){
               counter++;
            }
            // it's just easier, that's why no processing
            } catch (NoSuchEntityException e) {
            }
        }
        log.debug("counter={}",counter);
        return ans.setObject(counter);
    }

    @Override
    public List<Organization> findAll() {
        return CONTAINER.getAll();
    }

    @Override
    public ObjWithFeedback<Organization> findById(Long id) throws NoSuchEntityException {
        ObjWithFeedback<Organization> ans = new ObjWithFeedback<>(null,new ArrayList<>());
        try {
            return ans.setObject(CONTAINER.getById(id));
        } catch (NoSuchEntityException e) {
            return ans.addFeedback(e.getMessage());
        }
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

    private static ObjWithFeedback<Long> getUserID(Connection connection, String userName, String password){
        ObjWithFeedback<Long> ans = new ObjWithFeedback<>(-1L,new ArrayList<>());
        StringBuilder feedback = new StringBuilder();
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
                    return ans.setObject(id);
                } else {
                    throw new SQLException("Не удалось вытащить ID у пользователя");
                }
            }
        } catch (SQLException e) {
            if (e instanceof PSQLException) {
                feedback.append(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return ans.addFeedback(feedback.toString());
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
                        ObjWithFeedback<User> u = userDao.findById(resultSet.getLong("user_id"));
                        User user = u.object();

                        organization.setUser(user);

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

package db;

import exceptions.NoSuchEntityException;
import main.Container;
import main.UserContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.PSQLException;
import security.User;
import sorts.SortById;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao implements Dao<User>{
    private static final UserContainer CONTAINER = new UserContainer(new SortById<>());
    private static final UserDao INSTANCE = new UserDao();

    private static final String SAVE_USER_SQL= """
            insert into users (
                user_name,
                password)
            values (?,?)
            """;

    private static final String UPDATE_USER_SQL= """
            update users
            set user_name = ?,
                password = ?
            where id = ?
            """;

    private static final String DELETE_USER_SQL= """
            delete from users
            where id = ?
            """;

    private static final String SELECT_USERS_SQL= """
            select
                u."id",
                u."password",
                u."user_name"
            from
                "users" u;
            """;
    private static final Logger log = LogManager.getLogger(UserDao.class);

    @Override
    public int save(User user, User user1) {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SAVE_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);

            String userName = user.getUserName();
            String password = user.getPassword();

            statement.setString(1,userName);
            statement.setString(2,password);

            statement.executeUpdate();

            int userID;
            try (ResultSet resultSet = statement.getGeneratedKeys()){
                if (resultSet.next()){
                    userID = resultSet.getInt(1);
                    connection.commit();
                    CONTAINER.add(user.setId((long) userID));
                    return userID;
                }else {
                    connection.rollback();
                    throw new SQLException("Не удалось вытащить ID у пользователя");
                }
            }


        } catch (SQLException e) {
            if (e instanceof PSQLException) {
                log.warn(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return 0;
        }
    }

    @Override
    public boolean update(User user, Long id, User user1) throws NoSuchEntityException {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);

            user = findById(id).update(user);

            long userId = user.getId();
            String userName = user.getUserName();
            String password = user.getPassword();

            statement.setString(1,userName);
            statement.setString(2,password);
            statement.setLong(3,userId);

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()){
                if (resultSet.next()){
                    userId = resultSet.getInt(1);
                    connection.commit();
                    CONTAINER.update(user,id);
                    return userId > 0;
                }else {
                    connection.rollback();
                    throw new SQLException("Не удалось вытащить ID у организации");
                }
            }


        } catch (SQLException e) {
            if (e instanceof PSQLException) {
                log.warn(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return false;
        }
    }

    @Override
    public List<User> findAll() {
        return CONTAINER.getAll();
    }

    @Override
    public User findById(Long id) throws NoSuchEntityException {
        return CONTAINER.getById(id);
    }

    public User findByUserName(String userName) throws NoSuchEntityException {
        return CONTAINER.findByUserName(userName);
    }

    @Override
    public boolean delete(Long id, User user) throws NoSuchEntityException {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER_SQL)) {
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

    public static UserDao getInstance() {
        return INSTANCE;
    }

    private UserDao() {
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(SELECT_USERS_SQL, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);

            statement.executeQuery();

            try (ResultSet resultSet = statement.getResultSet()) {
                List<User> users = new ArrayList<>();

                while (resultSet.next()){
                    long id = resultSet.getLong("id");
                    String userName = resultSet.getString("user_name");
                    String password = resultSet.getString("password");
                    User user = new User(id,password,userName);
                    users.add(user);
                }
                CONTAINER.addList(users);
            }


        } catch (SQLException e) {
            if (e instanceof PSQLException) {
                log.warn(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
        }
    }
}

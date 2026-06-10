package db;

import exceptions.NoSuchEntityException;
import io.ObjWithFeedback;
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
    public ObjWithFeedback<Integer> save(User user, User user1) {
        ObjWithFeedback<Integer> ans = new ObjWithFeedback<>(-1,new ArrayList<>());
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
                    synchronized (CONTAINER) {
                        CONTAINER.add(user.setId((long) userID));
                    }
                    return ans.setObject(userID);
                }else {
                    connection.rollback();
                    throw new SQLException("Не удалось вытащить ID у пользователя");
                }
            }


        } catch (SQLException e) {
            if (e instanceof PSQLException) {
                ans = ans.addFeedback(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return ans;
        }
    }

    @Override
    public ObjWithFeedback<Boolean> update(User user, Long id, User user1) throws NoSuchEntityException {
        ObjWithFeedback<Boolean> ans = new ObjWithFeedback<>(false,new ArrayList<>());
        StringBuilder feedback = new StringBuilder();
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);

            ObjWithFeedback<User> u = findById(id);
            User user2 = u.object();
            List<String> f = u.feedback();
            if (!f.isEmpty()){
                for (String s:f){
                    if (s.isEmpty()) break;

                    feedback.append(s);
                }
                return ans.addFeedback(feedback.toString());
            }else user = user2.update(user);



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
                    synchronized (CONTAINER) {
                        CONTAINER.update(user, id);
                    }
                    return ans.setObject(userId > 0);
                }else {
                    connection.rollback();
                    throw new SQLException("Не удалось вытащить ID у организации");
                }
            }


        } catch (SQLException e) {
            if (e instanceof PSQLException) {
                feedback.append(DbErrorTranslator.translateSqlException((PSQLException) e));
            }
            return ans.addFeedback(feedback.toString());
        }
    }

    @Override
    public List<User> findAll() {
        synchronized (CONTAINER) {
            return CONTAINER.getAll();
        }
    }

    @Override
    public ObjWithFeedback<User> findById(Long id) {
        ObjWithFeedback<User> ans = new ObjWithFeedback<>(null,new ArrayList<>());
        try {
            synchronized (CONTAINER) {
                User user = CONTAINER.getById(id);
                return ans.setObject(user);
            }
        } catch (NoSuchEntityException e) {
            return ans.addFeedback(e.getMessage());
        }
    }

    public User findByUserName(String userName) throws NoSuchEntityException {
        synchronized (CONTAINER) {
            return CONTAINER.findByUserName(userName);
        }
    }

    @Override
    public ObjWithFeedback<Boolean> delete(Long id, User user) throws NoSuchEntityException {
        ObjWithFeedback<Boolean> ans = new ObjWithFeedback<>(false,new ArrayList<>());
        StringBuilder feedback = new StringBuilder();
        try (Connection connection = ConnectionManager.open();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER_SQL)) {
            connection.setAutoCommit(false);

            statement.setLong(1, id);

            int deletedRows = statement.executeUpdate();

            if (deletedRows > 0) {
                connection.commit();
                synchronized (CONTAINER) {
                    CONTAINER.removeById(id);
                }
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

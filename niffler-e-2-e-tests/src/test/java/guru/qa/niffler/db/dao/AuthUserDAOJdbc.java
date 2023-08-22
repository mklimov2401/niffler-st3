package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.model.UserJson;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static guru.qa.niffler.model.CurrencyValues.convertToEnum;

public class AuthUserDAOJdbc implements AuthUserDAO, UserDataUserDAO {

    private final static DataSource authDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH);
    private final static DataSource userdataDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA);

    @Override
    public UUID createUser(UserEntity user) {
        UUID generatedUserId = null;
        try (Connection conn = authDs.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement usersPs = conn.prepareStatement(
                    "INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);

                 PreparedStatement authorityPs = conn.prepareStatement(
                         "INSERT INTO authorities (user_id, authority) " +
                                 "VALUES (?, ?)")) {

                usersPs.setString(1, user.getUsername());
                usersPs.setString(2, pe.encode(user.getPassword()));
                usersPs.setBoolean(3, user.getEnabled());
                usersPs.setBoolean(4, user.getAccountNonExpired());
                usersPs.setBoolean(5, user.getAccountNonLocked());
                usersPs.setBoolean(6, user.getCredentialsNonExpired());

                usersPs.executeUpdate();

                try (ResultSet generatedKeys = usersPs.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedUserId = UUID.fromString(generatedKeys.getString("id"));
                    } else {
                        throw new IllegalStateException("Can`t obtain id from given ResultSet");
                    }
                }

                for (Authority authority : Authority.values()) {
                    authorityPs.setObject(1, generatedUserId);
                    authorityPs.setString(2, authority.name());
                    authorityPs.addBatch();
                    authorityPs.clearParameters();
                }

                authorityPs.executeBatch();
                user.setId(generatedUserId);
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return generatedUserId;
    }

    @Override
    public void deleteUserById(UUID userId) {
        try (Connection conn = authDs.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement usersPs = conn.prepareStatement(
                    "DELETE FROM users WHERE id = ?");

                 PreparedStatement authorityPs = conn.prepareStatement(
                         "DELETE FROM authorities WHERE user_id = ?")) {

                authorityPs.setObject(1, userId);
                authorityPs.executeUpdate();

                usersPs.setObject(1, userId);
                usersPs.executeUpdate();

                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updateUser(UserEntity user) {
        try (Connection conn = authDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "UPDATE  users " +
                            "SET id = ?, username = ?, password = ?, enabled = ?, account_non_expired = ?, " +
                            " account_non_locked = ? , credentials_non_expired = ? " +
                            "WHERE id = ? ")) {

                usersPs.setObject(1, user.getId());
                usersPs.setString(2, user.getUsername());
                usersPs.setString(3, pe.encode(user.getPassword()));
                usersPs.setBoolean(4, user.getEnabled());
                usersPs.setBoolean(5, user.getAccountNonExpired());
                usersPs.setBoolean(6, user.getAccountNonLocked());
                usersPs.setBoolean(7, user.getCredentialsNonExpired());
                usersPs.setObject(8, user.getId());

                usersPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity getUser(UUID userId) {
        UserEntity user = new UserEntity();
        try (Connection conn = authDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "SELECT * FROM users " +
                            "WHERE id = ? ")) {

                usersPs.setObject(1, userId);

                usersPs.execute();
                ResultSet resultSet = usersPs.getResultSet();

                while (resultSet.next()){
                    user.setId(resultSet.getObject("id", UUID.class));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEnabled(resultSet.getBoolean("enabled"));
                    user.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
                    user.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
                    user.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public UserEntity getUser(String username) {
        UserEntity user = new UserEntity();
        try (Connection conn = authDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "SELECT * FROM users " +
                            "WHERE username = ? ")) {

                usersPs.setString(1, username);

                usersPs.execute();
                ResultSet resultSet = usersPs.getResultSet();

                while (resultSet.next()){
                    user.setId(resultSet.getObject("id", UUID.class));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEnabled(resultSet.getBoolean("enabled"));
                    user.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
                    user.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
                    user.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public int createUserInUserData(UserEntity user) {
        int createdRows = 0;
        try (Connection conn = userdataDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "INSERT INTO users (username, currency) " +
                            "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {

                usersPs.setString(1, user.getUsername());
                usersPs.setString(2, CurrencyValues.RUB.name());

                createdRows = usersPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return createdRows;
    }

    @Override
    public void deleteUserByIdInUserData(UUID userId) {
        try (Connection conn = userdataDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "DELETE FROM users " +
                            "WHERE id = ? ")) {

                usersPs.setObject(1, userId);

                usersPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUserByUsernameInUserData(String username) {
        try (Connection conn = userdataDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "DELETE FROM users " +
                            "WHERE username = ? ")) {

                usersPs.setString(1, username);

                usersPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserJson getUserData(String username) {
        UserJson user = new UserJson();
        try (Connection conn = userdataDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "SELECT * FROM users " +
                            "WHERE username = ? ")) {

                usersPs.setString(1, username);

                usersPs.execute();
                ResultSet resultSet = usersPs.getResultSet();

                while (resultSet.next()){
                    user.setId(resultSet.getObject("id", UUID.class));
                    user.setUsername(resultSet.getString("username"));
                    String currency = resultSet.getString("currency");
                    user.setCurrency(convertToEnum(currency));
                    user.setFirstname(resultSet.getString("firstname"));
                    user.setSurname(resultSet.getString("surname"));
                    user.setPhoto(resultSet.getString("photo"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public void updateUserData(UserJson user) {
        try (Connection conn = userdataDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "UPDATE  users " +
                            "SET id = ?, username = ?, currency = ?, firstname = ?, surname = ?, photo = ? " +
                            "WHERE id = ? ")) {

                usersPs.setObject(1, user.getId());
                usersPs.setString(2, user.getUsername());
                usersPs.setObject(3, user.getCurrency().name());
                usersPs.setString(4, user.getFirstname());
                usersPs.setString(5, user.getSurname());
                usersPs.setObject(6, user.getPhoto());
                usersPs.setObject(7, user.getId());

                usersPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

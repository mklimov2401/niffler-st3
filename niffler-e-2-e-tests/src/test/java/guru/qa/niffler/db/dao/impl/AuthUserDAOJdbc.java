package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.jdbc.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.model.*;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.userdata.UserDataEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AuthUserDAOJdbc implements AuthUserDAO, UserDataUserDAO {

    private final static DataSource authDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH);
    private final static DataSource userdataDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA);

    @Override
    public int createUser(AuthUserEntity user) {
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
            } catch (SQLException e) {
                conn.rollback();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }


    @Override
    public AuthUserEntity updateUser(AuthUserEntity user) {
        try (Connection conn = authDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "UPDATE  users " +
                            "SET id = ?, password = ?, enabled = ?, account_non_expired = ?, " +
                            " account_non_locked = ? , credentials_non_expired = ? " +
                            "WHERE id = ? ")) {

                usersPs.setObject(1, user.getId());
                usersPs.setString(2, pe.encode(user.getPassword()));
                usersPs.setBoolean(3, user.getEnabled());
                usersPs.setBoolean(4, user.getAccountNonExpired());
                usersPs.setBoolean(5, user.getAccountNonLocked());
                usersPs.setBoolean(6, user.getCredentialsNonExpired());
                usersPs.setObject(7, user.getId());

                usersPs.executeUpdate();
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(AuthUserEntity userId) {
        try (Connection conn = authDs.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement usersPs = conn.prepareStatement(
                    "DELETE FROM users WHERE id = ?");

                 PreparedStatement authorityPs = conn.prepareStatement(
                         "DELETE FROM authorities WHERE user_id = ?")) {

                authorityPs.setObject(1, userId.getId());
                authorityPs.executeUpdate();

                usersPs.setObject(1, userId.getId());
                usersPs.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public AuthUserEntity getUserById(UUID userId) {
        AuthUserEntity user = new AuthUserEntity();
        try (Connection conn = authDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "SELECT * FROM users " +
                            "WHERE id = ? ")) {

                usersPs.setObject(1, userId);

                usersPs.execute();
                ResultSet rs = usersPs.getResultSet();

                while (rs.next()) {
                    user.setId(rs.getObject("id", UUID.class));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEnabled(rs.getBoolean("enabled"));
                    user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                }
            }
            try (PreparedStatement authPs = conn.prepareStatement(
                    "select * from authorities a " +
                            "where user_id = ?")) {
                if (Objects.nonNull(user.getId())) {
                    authPs.setObject(1, user.getId());
                    authPs.execute();
                    ResultSet rs = authPs.getResultSet();

                    List<AuthorityEntity> authorityEntityList = new ArrayList<>();
                    while (rs.next()) {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setId(rs.getObject("id", UUID.class));
                        ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                        ae.setUser(user);
                        authorityEntityList.add(ae);
                    }

                    user.setAuthorities(authorityEntityList);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }


    @Override
    public AuthUserEntity getUserByName(String username) {
        AuthUserEntity user = new AuthUserEntity();
        try (Connection conn = authDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "SELECT * FROM users " +
                            "WHERE username = ? ")) {

                usersPs.setString(1, username);

                usersPs.execute();
                ResultSet rs = usersPs.getResultSet();

                while (rs.next()) {
                    user.setId(rs.getObject("id", UUID.class));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEnabled(rs.getBoolean("enabled"));
                    user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                }
            }
            try (PreparedStatement authPs = conn.prepareStatement(
                    "select * from authorities a " +
                            "where user_id = ?")) {
                if (Objects.nonNull(user.getId())) {
                    authPs.setObject(1, user.getId());
                    authPs.execute();
                    ResultSet rs = authPs.getResultSet();

                    List<AuthorityEntity> authorityEntityList = new ArrayList<>();
                    while (rs.next()) {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setId(rs.getObject("id", UUID.class));
                        ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                        ae.setUser(user);

                        authorityEntityList.add(ae);
                    }

                    user.setAuthorities(authorityEntityList);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public int createUserInUserData(UserDataEntity user) {
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
    public UserDataEntity getUserData(String username) {
        UserDataEntity user = new UserDataEntity();
        try (Connection conn = userdataDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "SELECT * FROM users " +
                            "WHERE username = ? ")) {

                usersPs.setString(1, username);

                usersPs.execute();
                ResultSet rs = usersPs.getResultSet();

                while (rs.next()) {
                    user.setId(rs.getObject("id", UUID.class));
                    user.setUsername(rs.getString("username"));
                    user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    user.setFirstname(rs.getString("firstname"));
                    user.setSurname(rs.getString("surname"));
                    String photo = rs.getString("photo");
                    user.setPhoto(photo != null ? photo.getBytes() : null);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public void updateUserData(UserDataEntity user) {
        try (Connection conn = userdataDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "UPDATE  users " +
                            "SET id = ?, currency = ?, firstname = ?, surname = ?, photo = ? " +
                            "WHERE id = ? ")) {

                usersPs.setObject(1, user.getId());
                usersPs.setObject(2, user.getCurrency().name());
                usersPs.setString(3, user.getFirstname());
                usersPs.setString(4, user.getSurname());
                usersPs.setObject(5, user.getPhoto());
                usersPs.setObject(6, user.getId());


                usersPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

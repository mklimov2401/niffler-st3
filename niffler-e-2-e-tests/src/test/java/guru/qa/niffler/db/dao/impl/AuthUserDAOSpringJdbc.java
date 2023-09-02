package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.jdbc.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.model.userdata.UserDataEntity;
import guru.qa.niffler.db.springjdbc.AuthorityEntityRowMapper;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.springjdbc.UserDataEntityRowMapper;
import guru.qa.niffler.db.springjdbc.UserEntityRowMapper;
import guru.qa.niffler.db.model.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class AuthUserDAOSpringJdbc implements AuthUserDAO, UserDataUserDAO {

    private final TransactionTemplate authTtpl;
    private final TransactionTemplate userdataTtpl;
    private final JdbcTemplate authJdbcTemplate;
    private final JdbcTemplate userdataJdbcTemplate;

    public AuthUserDAOSpringJdbc() {
        JdbcTransactionManager authTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH));
        JdbcTransactionManager userdataTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA));

        this.authTtpl = new TransactionTemplate(authTm);
        this.userdataTtpl = new TransactionTemplate(userdataTm);
        this.authJdbcTemplate = new JdbcTemplate(authTm.getDataSource());
        this.userdataJdbcTemplate = new JdbcTemplate(userdataTm.getDataSource());
    }

    @Override
    @SuppressWarnings("unchecked")
    public int createUser(AuthUserEntity user) {
        return authTtpl.execute(status -> {
            KeyHolder kh = new GeneratedKeyHolder();

            authJdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement("INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, pe.encode(user.getPassword()));
                ps.setBoolean(3, user.getEnabled());
                ps.setBoolean(4, user.getAccountNonExpired());
                ps.setBoolean(5, user.getAccountNonLocked());
                ps.setBoolean(6, user.getCredentialsNonExpired());
                return ps;
            }, kh);
            final UUID userId = (UUID) kh.getKeyList().get(0).get("id");
            authJdbcTemplate.batchUpdate("INSERT INTO authorities (user_id, authority) VALUES (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, userId);
                    ps.setObject(2, Authority.values()[i].name());
                }

                @Override
                public int getBatchSize() {
                    return Authority.values().length;
                }
            });
            return 0;
        });
    }

    @Override
    public AuthUserEntity updateUser(AuthUserEntity user) {
        authJdbcTemplate.update("UPDATE  users " +
                        "SET id = ?, password = ?, enabled = ?, account_non_expired = ?, " +
                        " account_non_locked = ? , credentials_non_expired = ? " +
                        "WHERE id = ? ", user.getId(), pe.encode(user.getPassword()),
                user.getEnabled(), user.getAccountNonExpired(), user.getAccountNonLocked(),
                user.getCredentialsNonExpired(), user.getId());
        return user;
    }

    @Override
    public void deleteUser(AuthUserEntity userId) {
        authTtpl.executeWithoutResult(status -> {
            authJdbcTemplate.update("DELETE FROM authorities WHERE user_id = ? ", userId.getId());
            authJdbcTemplate.update("DELETE FROM users WHERE id = ?", userId.getId());
        });
    }

    @Override
    public AuthUserEntity getUserById(UUID userId) {
        List<AuthUserEntity> user = authJdbcTemplate.query(
                "SELECT * FROM users WHERE id = ? ",
                UserEntityRowMapper.instance,
                userId
        );
        if (user.size() == 0) {
            return null;
        }
        List<AuthorityEntity> authorities = authJdbcTemplate.query(
                "SELECT * FROM authorities WHERE user_id = ? ",
                AuthorityEntityRowMapper.instance,
                user.get(0).getId()
        );
        authorities.forEach(a -> a.setUser(user.get(0)));
        user.get(0).setAuthorities(authorities);
        return user.get(0);
    }

    @Override
    public AuthUserEntity getUserByName(String username) {
        List<AuthUserEntity> user = authJdbcTemplate.query(
                "SELECT * FROM users WHERE username = ? ",
                UserEntityRowMapper.instance,
                username
        );
        if (user.size() == 0) {
            return null;
        }
        List<AuthorityEntity> authorities = authJdbcTemplate.query(
                "SELECT * FROM authorities WHERE user_id = ? ",
                AuthorityEntityRowMapper.instance,
                user.get(0).getId()
        );
        authorities.forEach(a -> a.setUser(user.get(0)));
        user.get(0).setAuthorities(authorities);
        return user.get(0);
    }


    @Override
    public int createUserInUserData(UserDataEntity user) {
        return userdataJdbcTemplate.update(
                "INSERT INTO users (username, currency) VALUES (?, ?)",
                user.getUsername(),
                CurrencyValues.RUB.name());
    }

    @Override
    public void deleteUserByIdInUserData(UUID userId) {
        userdataJdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
    }

    @Override
    public void deleteUserByUsernameInUserData(String username) {
        userdataJdbcTemplate.update("DELETE FROM users WHERE username = ?", username);
    }

    @Override
    public UserDataEntity getUserData(String username) {
        List<UserDataEntity> userDataEntity = userdataJdbcTemplate.query(
                "SELECT * FROM users WHERE username = ? ",
                UserDataEntityRowMapper.instance,
                username
        );
        if (userDataEntity.size() > 0) {
            return userDataEntity.get(0);
        }
        return null;
    }

    @Override
    public List<UserDataEntity> getUsersData(String username) {
        return null;
    }

    @Override
    public void updateUserData(UserDataEntity user) {
        userdataJdbcTemplate.update("UPDATE  users " +
                        "SET id = ?, currency = ?, firstname = ?, surname = ?, photo = ? " +
                        "WHERE id = ? ", user.getId(), user.getCurrency().name(), user.getFirstname(),
                user.getSurname(), user.getPhoto(), user.getId());
    }
}

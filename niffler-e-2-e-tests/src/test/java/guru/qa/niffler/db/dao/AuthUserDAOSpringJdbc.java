package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.mapper.AuthorityEntityRowMapper;
import guru.qa.niffler.db.mapper.UserDataEntityRowMapper;
import guru.qa.niffler.db.mapper.UserEntityRowMapper;
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
    public UUID createUser(UserEntity user) {
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
            return userId;
        });
    }

    @Override
    public void updateUser(UserEntity user) {
        authJdbcTemplate.update("UPDATE  users " +
                        "SET id = ?, password = ?, enabled = ?, account_non_expired = ?, " +
                        " account_non_locked = ? , credentials_non_expired = ? " +
                        "WHERE id = ? ", user.getId(), pe.encode(user.getPassword()),
                user.getEnabled(), user.getAccountNonExpired(), user.getAccountNonLocked(),
                user.getCredentialsNonExpired(), user.getId());

    }

    @Override
    public void deleteUserById(UUID userId) {
        authTtpl.executeWithoutResult(status -> {
            authJdbcTemplate.update("DELETE FROM authorities WHERE user_id = ? ", userId);
            authJdbcTemplate.update("DELETE FROM users WHERE id = ?", userId);
        });
    }

    @Override
    public UserEntity getUserById(UUID userId) {
        List<UserEntity> user = authJdbcTemplate.query(
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
    public UserEntity getUserByName(String username) {
        List<UserEntity> user = authJdbcTemplate.query(
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

    }

    @Override
    public UserEntity getUser(UUID userId) {
        return null;
    }

    @Override
    public UserEntity getUser(String username) {
        return null;
    }

    @Override
    public UserDataEntity getUserData(String username) {
        List<UserDataEntity> userDataEntity = userdataJdbcTemplate.query(
                "SELECT * FROM users WHERE username = ? ",
                UserDataEntityRowMapper.instance,
                username
        );
        if (userDataEntity.size() > 0){
            return userDataEntity.get(0);
        }
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

package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.AuthUserDAOJdbc;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.jupiter.annotation.Dao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(DaoExtension.class)
public class DBUserExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {
    public ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);

    private AuthUserDAO authUserDAO = new AuthUserDAOJdbc();

    private UserDataUserDAO userDataUserDAO = new AuthUserDAOJdbc();

    @Override
    public void beforeEach(ExtensionContext context) {
        if (context.getRequiredTestMethod().isAnnotationPresent(DBUser.class)) {
            UserEntity user;
            DBUser ann = context.getRequiredTestMethod().getAnnotation(DBUser.class);
            user = createdUserEntity(ann);
            UserEntity userAuthFromDb = authUserDAO.getUser(user.getUsername());
            UserDataEntity userDataFromDb = userDataUserDAO.getUserData(user.getUsername());
            if (Objects.nonNull(userAuthFromDb)){
                authUserDAO.deleteUserById(userAuthFromDb.getId());
            }
            if (Objects.nonNull(userDataFromDb)){
                userDataUserDAO.deleteUserByUsernameInUserData(userDataFromDb.getUsername());
            }
            UUID uuid = authUserDAO.createUser(user);
            user.setId(uuid);
            userDataUserDAO.createUserInUserData(user);
            context.getStore(NAMESPACE).put(context.getUniqueId(), user);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        UserEntity user = context.getStore(NAMESPACE).get(context.getUniqueId(), UserEntity.class);
        userDataUserDAO.deleteUserByUsernameInUserData(user.getUsername());
        authUserDAO.deleteUserById(user.getId());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserEntity.class);
    }

    @Override
    public UserEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserEntity.class);
    }


    private UserEntity createdUserEntity(DBUser dbUser) {
        UserEntity user = new UserEntity();
        user.setUsername(dbUser.username());
        user.setPassword(dbUser.password());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setAuthorities(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    return ae;
                }).toList());
        return user;
    }

}

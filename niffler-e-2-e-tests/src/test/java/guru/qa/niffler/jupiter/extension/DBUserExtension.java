package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.dao.impl.AuthUserDAOHibernate;
import guru.qa.niffler.db.dao.impl.UserdataUserDAOHibernate;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;

import static guru.qa.niffler.db.model.CurrencyValues.*;

@ExtendWith(DaoExtension.class)
public class DBUserExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {
    public ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);

    private AuthUserDAO authUserDAO = new AuthUserDAOHibernate();

    private UserDataUserDAO userDataUserDAO = new UserdataUserDAOHibernate();


    @Override
    public void beforeEach(ExtensionContext context) throws CloneNotSupportedException {
        if (context.getRequiredTestMethod().isAnnotationPresent(DBUser.class)) {
            DBUser userAnn = context.getRequiredTestMethod().getAnnotation(DBUser.class);
            AuthUserEntity user = convertToEntity(userAnn);
            AuthUserEntity clone = (AuthUserEntity) user.clone();
            authUserDAO.createUser(user);
            AuthUserEntity userAuthFromDb = authUserDAO.getUserByName(user.getUsername());
            userDataUserDAO.createUserInUserData(userAuthFromDb.toUserDataEntity(RUB));
            context.getStore(NAMESPACE).put(context.getUniqueId(), clone);


        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        AuthUserEntity user = context.getStore(NAMESPACE).get(context.getUniqueId(), AuthUserEntity.class);
        userDataUserDAO.deleteUserByUsernameInUserData(user.getUsername());
        authUserDAO.deleteUser(user);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(AuthUserEntity.class);
    }

    @Override
    public AuthUserEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), AuthUserEntity.class);
    }


    private AuthUserEntity convertToEntity(DBUser dbUser) {
        AuthUserEntity user = new AuthUserEntity();
        if (dbUser.username().isEmpty() || dbUser.password().isEmpty()){
            Faker faker = Faker.instance();
            user.setUsername(faker.name().username());
            user.setPassword(faker.internet().password());
        } else {
            user.setUsername(dbUser.username());
            user.setPassword(dbUser.password());
        }

        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setAuthorities(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    ae.setUser(user);
                    return ae;
                }).toList());
        return user;
    }

}

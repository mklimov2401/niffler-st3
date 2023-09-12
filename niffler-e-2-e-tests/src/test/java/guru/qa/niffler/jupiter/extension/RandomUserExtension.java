package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.dao.impl.AuthUserDAOHibernate;
import guru.qa.niffler.db.dao.impl.UserdataUserDAOHibernate;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.jupiter.annotation.RandomUser;
import org.junit.jupiter.api.extension.*;

public class RandomUserExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(RandomUserExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        RandomUser randomUser = context.getRequiredTestMethod().getAnnotation(RandomUser.class);
        if (randomUser != null) {
            Faker faker = new Faker();
            AuthUserEntity user = new AuthUserEntity();
            user.setUsername(faker.name().username());
            user.setPassword(faker.internet().password(3, 12));
            context.getStore(NAMESPACE).put(context.getUniqueId(), user);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        AuthUserEntity user = context.getStore(NAMESPACE).get(context.getUniqueId(), AuthUserEntity.class);
        UserDataUserDAO userDataUserDAO = new UserdataUserDAOHibernate();
        AuthUserDAO authUserDAO = new AuthUserDAOHibernate();
        if (!userDataUserDAO.getUsersData(user.getUsername()).isEmpty()) {
            userDataUserDAO.deleteUserByUsernameInUserData(user.getUsername());
            authUserDAO.deleteUser(user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext pc, ExtensionContext context) throws ParameterResolutionException {
        return pc.getParameter().getType().isAssignableFrom(AuthUserEntity.class) &&
                context.getRequiredTestMethod().isAnnotationPresent(RandomUser.class);
    }

    @Override
    public AuthUserEntity resolveParameter(ParameterContext pc, ExtensionContext context) throws ParameterResolutionException {
        return context.getStore(NAMESPACE).get(context.getUniqueId(), AuthUserEntity.class);
    }
}

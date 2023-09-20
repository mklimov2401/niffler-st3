package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Faker;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.repository.UserRepository;
import guru.qa.niffler.db.repository.UserRepositoryHibernate;
import guru.qa.niffler.jupiter.annotation.DBUser;
import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@ExtendWith(DaoExtension.class)
public class DbUserExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {
    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DbUserExtension.class);

    private final UserRepository userRepository = new UserRepositoryHibernate();


    @Override
    @Step("Created user")
    public void beforeEach(ExtensionContext context) throws CloneNotSupportedException {
        if (context.getRequiredTestMethod().isAnnotationPresent(DBUser.class)) {
            DBUser userAnn = context.getRequiredTestMethod().getAnnotation(DBUser.class);
            AuthUserEntity user = convertToEntity(userAnn);
            AuthUserEntity clone = (AuthUserEntity) user.clone();
            userRepository.createUserForTest(user);
            context.getStore(NAMESPACE).put(context.getUniqueId(), clone);
        }
    }

    @Override
    @Step("Deleted user")
    public void afterTestExecution(ExtensionContext context) {
        AuthUserEntity user = context.getStore(NAMESPACE).get(context.getUniqueId(), AuthUserEntity.class);
        if (user != null) {
            userRepository.removeAfterTest(user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(AuthUserEntity.class)
                && extensionContext.getRequiredTestMethod().isAnnotationPresent(DBUser.class);
    }

    @Override
    public AuthUserEntity resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), AuthUserEntity.class);
    }


    private AuthUserEntity convertToEntity(DBUser dbUser) {
        AuthUserEntity user = new AuthUserEntity();
        if (dbUser.username().isEmpty() || dbUser.password().isEmpty()) {
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
        user.setAuthorities(new ArrayList<>(Arrays.stream(Authority.values())
                .map(a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setAuthority(a);
                    ae.setUser(user);
                    return ae;
                }).collect(Collectors.toList())));
        return user;
    }

}

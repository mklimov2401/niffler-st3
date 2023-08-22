package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class DBUserExtension implements BeforeEachCallback, ParameterResolver {

    public ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        UserEntity user = null;
        Optional<Method> beforeEach = Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class))
                .findFirst();
        if (beforeEach.isPresent()) {
            Optional<Parameter> param = Arrays.stream(beforeEach.get().getParameters())
                    .filter(parameter -> parameter.isAnnotationPresent(DBUser.class)
                            && parameter.getType().isAssignableFrom(UserEntity.class))
                    .findFirst();
            if (param.isPresent()) {
                DBUser ann = param.get().getAnnotation(DBUser.class);
                user = createdUserEntity(ann);
            }
        }

        if (Objects.isNull(user)) {
            Optional<Parameter> parameterFromTest = Arrays.stream(context.getRequiredTestMethod().getParameters())
                    .filter(parameter -> parameter.isAnnotationPresent(DBUser.class)
                            && parameter.getType().isAssignableFrom(UserEntity.class))
                    .findFirst();
            if (parameterFromTest.isPresent()) {
                DBUser ann = parameterFromTest.get().getAnnotation(DBUser.class);
                user = createdUserEntity(ann);

            }
        }
        context.getStore(NAMESPACE).put(context.getUniqueId(), user);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserEntity.class)
                && parameterContext.getParameter().isAnnotationPresent(DBUser.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId());
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

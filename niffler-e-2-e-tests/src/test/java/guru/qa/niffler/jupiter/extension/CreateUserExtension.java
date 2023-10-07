package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.GenerateUser;
import guru.qa.niffler.jupiter.annotation.GeneratedUser;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CreateUserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace
            NESTED = ExtensionContext.Namespace.create(GeneratedUser.Selector.NESTED),
            OUTER = ExtensionContext.Namespace.create(GeneratedUser.Selector.OUTER);

    @Override
    public void beforeEach(ExtensionContext context) {
        Map<String, GenerateUser> usersForTest = usersForTest(context);
        for (Map.Entry<String, GenerateUser> entry : usersForTest.entrySet()) {
            UserJson user = createUserForTest(entry.getValue());
            user.setFriends(createFriendsIfPresent(entry.getValue(), user));
            user.setIncomeInvitations(createIncomeInvitationsIfPresent(entry.getValue(), user));
            user.setOutcomeInvitations(createOutcomeInvitationsIfPresent(entry.getValue(), user));
            context.getStore(entry.getKey().contains(GeneratedUser.Selector.NESTED.name()) ? NESTED : OUTER)
                    .put(context.getUniqueId(), user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        return parameterContext.getParameter().isAnnotationPresent(GeneratedUser.class) &&
                parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        GeneratedUser generatedUser = parameterContext.getParameter().getAnnotation(GeneratedUser.class);
        return context.getStore(ExtensionContext.Namespace.create(generatedUser.selector()))
                .get(context.getUniqueId(), UserJson.class);
    }

    protected abstract UserJson createUserForTest(GenerateUser annotation);

    protected abstract List<UserJson> createFriendsIfPresent(GenerateUser annotation, UserJson currentUser);

    protected abstract List<UserJson> createIncomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser);

    protected abstract List<UserJson> createOutcomeInvitationsIfPresent(GenerateUser annotation, UserJson currentUser);


    private Map<String, GenerateUser> usersForTest(ExtensionContext context) {
        Map<String, GenerateUser> result = new HashMap<>();
        ApiLogin apiLogin = context.getRequiredTestMethod().getAnnotation(ApiLogin.class);
        if (apiLogin != null && apiLogin.user().handleAnnotation()) {
            result.put(context.getUniqueId() + GeneratedUser.Selector.NESTED, apiLogin.user());
        }
        GenerateUser outerUser = context.getRequiredTestMethod().getAnnotation(GenerateUser.class);
        if (outerUser != null && outerUser.handleAnnotation()) {
            result.put(context.getUniqueId() + GeneratedUser.Selector.OUTER, outerUser);
        }
        return result;
    }
}
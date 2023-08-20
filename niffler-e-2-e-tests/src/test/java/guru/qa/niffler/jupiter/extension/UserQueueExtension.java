package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.jupiter.annotation.User.UserType;
import static guru.qa.niffler.jupiter.annotation.User.UserType.*;

public class UserQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserQueueExtension.class);

    private static final Map<UserType, Queue<UserJson>> usersQueue = new ConcurrentHashMap<>();

    static {
        Queue<UserJson> usersWithFriends = new ConcurrentLinkedQueue<>();
        usersWithFriends.add(bindUser("misha", "123456"));
        usersWithFriends.add(bindUser("barsik", "123456"));
        usersQueue.put(WITH_FRIENDS, usersWithFriends);
        Queue<UserJson> usersInSent = new ConcurrentLinkedQueue<>();
        usersInSent.add(bindUser("bee", "123456"));
        usersInSent.add(bindUser("anna", "123456"));
        usersQueue.put(INVITATION_SENT, usersInSent);
        Queue<UserJson> usersInReceived = new ConcurrentLinkedQueue<>();
        usersInReceived.add(bindUser("valentin", "123456"));
        usersInReceived.add(bindUser("pizlly", "123456"));
        usersQueue.put(INVITATION_RECEIVED, usersInReceived);
    }

    @Override
    public void beforeEach(ExtensionContext context){
        Method[] methods = context.getRequiredTestClass().getDeclaredMethods();
        for (Method method : methods) {
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                if (parameter.getType().isAssignableFrom(UserJson.class)
                        && parameter.isAnnotationPresent(User.class)) {
                    User parameterAnnotation = parameter.getAnnotation(User.class);
                    Queue<UserJson> userQueueByType = usersQueue.get(parameterAnnotation.userType());
                    UserJson candidateForTest = null;
                    while (Objects.isNull(candidateForTest)) {
                        if (Objects.isNull(context.getStore(NAMESPACE).get(parameterAnnotation.userType()))) {
                            candidateForTest = userQueueByType.poll();
                            if (Objects.nonNull(candidateForTest)) {
                                candidateForTest.setUserType(parameterAnnotation.userType());
                                context.getStore(NAMESPACE).put(candidateForTest.getUserType(), candidateForTest);
                            }
                        } else {
                            break;
                        }
                    }

                }
            }
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        Method[] methods = context.getRequiredTestClass().getDeclaredMethods();
        for (Method method : methods) {
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                if (parameter.getType().isAssignableFrom(UserJson.class)
                        && parameter.isAnnotationPresent(User.class)) {
                    User parameterAnnotation = parameter.getAnnotation(User.class);
                    if (Objects.nonNull(context.getStore(NAMESPACE).get(parameterAnnotation.userType()))) {
                        UserJson userFromTest = context.getStore(NAMESPACE)
                                .get(parameterAnnotation.userType(), UserJson.class);
                        context.getStore(NAMESPACE).remove(parameterAnnotation.userType());
                        Queue<UserJson> userQueue = usersQueue.get(userFromTest.getUserType());
                        userQueue.add(userFromTest);
                    }
                }
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserJson.class) && parameterContext.getParameter().isAnnotationPresent(User.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE)
                .get(parameterContext.getParameter().getAnnotation(User.class).userType(), UserJson.class);
    }


    private static UserJson bindUser(String username, String password) {
        UserJson user = new UserJson();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
}

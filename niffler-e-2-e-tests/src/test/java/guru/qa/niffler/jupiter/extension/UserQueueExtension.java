package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static guru.qa.niffler.jupiter.annotation.User.UserType;
import static guru.qa.niffler.jupiter.annotation.User.UserType.*;

public class UserQueueExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserQueueExtension.class);

    private static final Map<UserType, Queue<UserJson>> usersQueue = new ConcurrentHashMap<>();

    static {
        Queue<UserJson> usersWithFriends = new ConcurrentLinkedQueue<>();
        usersWithFriends.add(bindUser("misha", "12345"));
        usersWithFriends.add(bindUser("barsik", "12345"));
        usersQueue.put(WITH_FRIENDS, usersWithFriends);
        Queue<UserJson> usersInSent = new ConcurrentLinkedQueue<>();
        usersInSent.add(bindUser("bee", "12345"));
        usersInSent.add(bindUser("anna", "12345"));
        usersQueue.put(INVITATION_SENT, usersInSent);
        Queue<UserJson> usersInReceived = new ConcurrentLinkedQueue<>();
        usersInReceived.add(bindUser("valentin", "12345"));
        usersInReceived.add(bindUser("pizlly", "12345"));
        usersQueue.put(INVITATION_RECEIVED, usersInReceived);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        Optional<Method> beforeEach = findBeforeEach(context);
        beforeEach.ifPresent(method -> fillStoreContextCreated(context, method));
        fillStoreContextCreated(context, context.getRequiredTestMethod());
    }


    @Override
    public void afterTestExecution(ExtensionContext context) {
        Optional<Method> beforeEach = findBeforeEach(context);
        beforeEach.ifPresent(method -> fillStoreContextDeleted(context, method));
        fillStoreContextDeleted(context, context.getRequiredTestMethod());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserJson.class) && parameterContext.getParameter().isAnnotationPresent(User.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (Objects.nonNull(parameterContext.getParameter().getDeclaringExecutable()
                .getAnnotation(BeforeEach.class))) {
            Optional<Method> beforeEach = findBeforeEach(extensionContext);
            if (beforeEach.isPresent()) {
                return extensionContext.getStore(NAMESPACE)
                        .get(getUniqueId(extensionContext, beforeEach.get(), parameterContext.getParameter()), UserJson.class);
            }
        }
        return extensionContext.getStore(NAMESPACE)
                .get(getUniqueId(extensionContext, extensionContext.getRequiredTestMethod(),
                        parameterContext.getParameter()), UserJson.class);
    }


    public static void fillStoreContextCreated(ExtensionContext context, Method method){
        Arrays.stream(method.getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(User.class)
                        && parameter.getType().isAssignableFrom(UserJson.class))
                .forEach(parameter -> {
                    User parameterAnnotation = parameter.getAnnotation(User.class);
                    Queue<UserJson> usersQueueByType = usersQueue.get(parameterAnnotation.userType());
                    UserJson candidateForTest = null;
                    while (Objects.isNull(candidateForTest)) {
                        candidateForTest = usersQueueByType.poll();
                    }
                    candidateForTest.setUserType(parameterAnnotation.userType());
                    context.getStore(NAMESPACE).put(getUniqueId(context, method, parameter), candidateForTest);
                });
    }

    public static void fillStoreContextDeleted(ExtensionContext context, Method method){
        Arrays.stream(method.getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(User.class)
                        && parameter.getType().isAssignableFrom(UserJson.class))
                .forEach(parameter -> {
                    UserJson userFromTest = context.getStore(NAMESPACE)
                            .get(getUniqueId(context, method, parameter), UserJson.class);
                    if (Objects.nonNull(userFromTest)) {
                        context.getStore(NAMESPACE).remove(getUniqueId(context, method, parameter));
                        Queue<UserJson> userQueue = usersQueue.get(userFromTest.getUserType());
                        userQueue.add(userFromTest);
                    }
                });
    }

    private static String getUniqueId(ExtensionContext context, Method method, Parameter parameter) {
        return new StringJoiner("_")
                .add(context.getUniqueId())
                .add(method.getName())
                .add(parameter.getName()).toString();
    }

    private static Optional<Method> findBeforeEach(ExtensionContext context) {
        return Arrays.stream(context.getRequiredTestClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(BeforeEach.class))
                .findFirst();
    }

    private static UserJson bindUser(String username, String password) {
        UserJson user = new UserJson();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
}

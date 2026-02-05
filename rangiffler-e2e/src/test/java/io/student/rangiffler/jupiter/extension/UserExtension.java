package io.student.rangiffler.jupiter.extension;

import com.github.javafaker.Faker;
import io.student.rangiffler.jupiter.annotation.User;
import io.student.rangiffler.model.UserJson;
import io.student.rangiffler.service.UserDbClient;
import io.student.rangiffler.service.UsersClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import static io.student.rangiffler.data.UserData.STANDART_PASSWORD;

public class UserExtension implements BeforeEachCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private final UserDbClient usersClient = new UserDbClient();
    private final Faker faker = new Faker();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(
                context.getRequiredTestMethod(),
                User.class
        ).ifPresent(
                a -> {
                    var user = usersClient.createUser(faker.name().username(), STANDART_PASSWORD);
                    context.getStore(NAMESPACE).put(
                            context.getUniqueId(),
                            user
                    );
                }
        );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserJson.class);
    }
}

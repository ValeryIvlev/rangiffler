package io.student.rangiffler;

import com.github.javafaker.Faker;
import io.student.rangiffler.jupiter.annotation.CloseConnections;

import io.student.rangiffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import static io.student.rangiffler.data.UserData.STANDART_PASSWORD;

@CloseConnections
public class CreateAuthorityDbTest {

    UserDbClient userDbClient = new UserDbClient();
    private final Faker faker = new Faker();

    @Test
    public void txTest() {
        String userName = faker.name().username();
        System.out.println("-----------------");
        System.out.println("-----------------");
        System.out.println(userName);
        System.out.println("-----------------");
        System.out.println("-----------------");
        userDbClient.createUser(userName, STANDART_PASSWORD);
    }
}

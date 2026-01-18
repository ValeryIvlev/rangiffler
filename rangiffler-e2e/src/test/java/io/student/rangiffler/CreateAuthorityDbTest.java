package io.student.rangiffler;

import com.github.javafaker.Faker;
import io.student.rangiffler.jupiter.annotation.CloseConnections;
import io.student.rangiffler.service.AuthorityDbClient;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.student.rangiffler.data.UserData.STANDART_PASSWORD;

@CloseConnections
public class CreateAuthorityDbTest {
    AuthorityDbClient authorityDbClient = new AuthorityDbClient();
    private final Faker faker = new Faker();

    @Test
    public void txTest() {
        String userName = faker.name().username();
        System.out.println("-----------------");
        System.out.println("-----------------");
        System.out.println(userName);
        System.out.println("-----------------");
        System.out.println("-----------------");
        authorityDbClient.createAuthorityUser(UUID.randomUUID(), userName, STANDART_PASSWORD);
    }
}

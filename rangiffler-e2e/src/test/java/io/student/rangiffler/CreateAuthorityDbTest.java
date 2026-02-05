package io.student.rangiffler;

import com.github.javafaker.Faker;
import io.student.rangiffler.data.entity.AuthUserEntity;
import io.student.rangiffler.data.entity.UserEntity;
import io.student.rangiffler.data.repository.AuthUserRepository;
import io.student.rangiffler.jupiter.annotation.CloseConnections;

import io.student.rangiffler.model.UserJson;
import io.student.rangiffler.service.UserDbClient;
import io.student.rangiffler.service.UserDbClientRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static io.student.rangiffler.data.UserData.STANDART_PASSWORD;

@CloseConnections
public class CreateAuthorityDbTest {

    UserDbClient userDbClient = new UserDbClient();
    private final Faker faker = new Faker();
    UserDbClientRepository userDbClientRepository = new UserDbClientRepository();

    @Test
    public void txTest() {
        String userName = faker.name().username();
        System.out.println("-----------------");
        System.out.println("-----------------");
        System.out.println(userName);
        System.out.println("-----------------");
        System.out.println("-----------------");
        userDbClient.createUser(userName, STANDART_PASSWORD);
        System.out.println("-----------------");
        System.out.println("-----------------");
        userDbClient.deleteUserByUsername(userName);
    }

    @Test
    public void txTestSpringJdbc() {
        String userName = faker.name().username();
        System.out.println("-----------------");
        System.out.println("-----------------");
        System.out.println(userName);
        System.out.println("-----------------");
        System.out.println("-----------------");
        userDbClient.createUserSpringJdbc(userName, STANDART_PASSWORD);
        System.out.println("-----------------");
        System.out.println("-----------------");
        userDbClient.deleteUserByUsernameSpringJdbc(userName);
    }

    @Test
    public void findAllTest() {
        System.out.println(userDbClient.findAllUsers());
        System.out.println(userDbClient.findAllAuthority());
        System.out.println(userDbClient.findAllUsersSpringJdbc());
        System.out.println(userDbClient.findAllAuthoritySpringJdbc());
    }

    @Test
    public void findAllRepositoryTest() {
        System.out.println(userDbClientRepository.findAllUsers());
    }

    @Test
    public void createRepositoryUserTest() {
        UUID countryId = UUID.fromString("05872163-e273-11f0-ac58-0242ac110002");

        UserEntity userA = new UserEntity(
                UUID.randomUUID(),
                "userA_" + System.currentTimeMillis(),
                "A",
                "Test",
                null,
                countryId
        );

        UserEntity userB = new UserEntity(
                UUID.randomUUID(),
                "userB_" + System.currentTimeMillis(),
                "B",
                "Test",
                null,
                countryId
        );

        // 1. create users in rangiffler-api.user
        userDbClientRepository.create(userA);
        userDbClientRepository.create(userB);

        System.out.println("Created userA: " + userA.getId());
        System.out.println("Created userB: " + userB.getId());

        // 2. A -> B invitation
        userDbClientRepository.addOutcomeInvitation(userA, userB);
        System.out.println("Invitation A -> B (PENDING)");

        // 3. B -> A invitation (edge-case)
        userDbClientRepository.addOutcomeInvitation(userB, userA);
        System.out.println("Invitation B -> A (PENDING)");

        // 4. Accept friendship
        userDbClientRepository.addFriend(userA, userB);
        System.out.println("Friendship accepted");


    }

    @Test
    public void createPrintDeleteAuthUserSpringJdbcRepositoryTest() {
        String userName = faker.name().username() + "_" + System.currentTimeMillis();

        System.out.println("---- CREATE ----");
        UserJson created = userDbClientRepository.createUserRepositorySpringJdbc(userName, STANDART_PASSWORD);
        System.out.println("Created: " + created);

        System.out.println("---- FIND ----");
        Optional<UserJson> foundOpt = userDbClientRepository.findUserJsonByUsernameSpringJdbc(userName);
        System.out.println("Found: " + foundOpt);
    }

    @Test
    public void createPrintDeleteAuthUserJdbcRepositoryTest() {
        String userName = faker.name().username() + "_" + System.currentTimeMillis();

        System.out.println("---- CREATE ----");
        UserJson created = userDbClientRepository.createUserRepositoryJdbc(userName, STANDART_PASSWORD);
        System.out.println("Created: " + created);

        System.out.println("---- FIND ----");
        Optional<UserJson> foundOpt = userDbClientRepository.findUserJsonByUsernameJdbc(userName);
        System.out.println("Found: " + foundOpt);
    }
}

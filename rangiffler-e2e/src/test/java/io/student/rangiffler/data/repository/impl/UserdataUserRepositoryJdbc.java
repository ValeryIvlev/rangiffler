package io.student.rangiffler.data.repository.impl;

import io.student.rangiffler.config.Config;
import io.student.rangiffler.data.entity.FriendshipStatus;
import io.student.rangiffler.data.entity.UserEntity;
import io.student.rangiffler.data.repository.UserdataUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static io.student.rangiffler.data.tpl.Connections.holder;

public class UserdataUserRepositoryJdbc implements UserdataUserRepository {
    private static final Config CFG = Config.getInstance();

    private static final String SQL_FIND_USER_BY_ID =
            """
            SELECT
                BIN_TO_UUID(id, true) AS id,
                username,
                firstname,
                lastName,
                avatar,
                BIN_TO_UUID(country_id, true) AS country_id
            FROM `rangiffler-api`.`user`
            WHERE id = UUID_TO_BIN(?, true);
            """;

    private static final String SQL_INSERT_FRIENDSHIP =
            """
            INSERT INTO `rangiffler-api`.`friendship`
            (requester_id, addressee_id, created_date, status)
            VALUES (UUID_TO_BIN(?, true), UUID_TO_BIN(?, true), ?, ?);
            """;

    private static final String SQL_UPDATE_FRIENDSHIP_STATUS =
            """
            UPDATE `rangiffler-api`.`friendship`
            SET status = ?
            WHERE requester_id = UUID_TO_BIN(?, true)
              AND addressee_id = UUID_TO_BIN(?, true);
            """;

    private static final String SQL_INSERT_USER =
            """
            INSERT INTO `rangiffler-api`.`user`
            (id, username, firstname, lastName, avatar, country_id)
            VALUES (UUID_TO_BIN(?, true), ?, ?, ?, ?, UUID_TO_BIN(?, true));
            """;

    @Override
    public UserEntity create(UserEntity user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        try (PreparedStatement ps =
                     holder(CFG.apiJdbcUrl()).connection().prepareStatement(SQL_INSERT_USER)) {

            ps.setString(1, user.getId().toString());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getLastName());
            ps.setBytes(5, user.getAvatar());
            ps.setString(6, user.getCountryId().toString());

            ps.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to create user: username=" + user.getUsername(),
                    e
            );
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(SQL_FIND_USER_BY_ID)) {
            ps.setString(1, id.toString());

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return Optional.empty();
            }

            return Optional.of(buildUser(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by id=" + id, e);
        }
    }

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        insertFriendship(requester.getId(), addressee.getId(), FriendshipStatus.PENDING);
    }

    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        insertFriendship(requester.getId(), addressee.getId(), FriendshipStatus.PENDING);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        // 1) requester -> addressee
        updateStatus(requester.getId(), addressee.getId(), FriendshipStatus.ACCEPTED);
        // 2) addressee -> requester
        boolean updated = updateStatus(addressee.getId(), requester.getId(), FriendshipStatus.ACCEPTED);
        if (!updated) {
            insertFriendship(addressee.getId(), requester.getId(), FriendshipStatus.ACCEPTED);
        }
    }

    private UserEntity buildUser(ResultSet rs) throws SQLException {
        UserEntity user = new UserEntity();
        user.setId(UUID.fromString(rs.getString("id")));
        user.setUsername(rs.getString("username"));
        user.setFirstname(rs.getString("firstname"));
        user.setLastName(rs.getString("lastName"));
        user.setAvatar(rs.getBytes("avatar"));

        String countryId = rs.getString("country_id");
        user.setCountryId(countryId != null ? UUID.fromString(countryId) : null);

        return user;
    }

    private void insertFriendship(UUID requesterId, UUID addresseeId, FriendshipStatus status) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(SQL_INSERT_FRIENDSHIP)) {
            ps.setString(1, requesterId.toString());
            ps.setString(2, addresseeId.toString());
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(4, status.name());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to insert friendship: requesterId=" + requesterId +
                            ", addresseeId=" + addresseeId +
                            ", status=" + status,
                    e
            );
        }
    }

    private boolean updateStatus(UUID requesterId, UUID addresseeId, FriendshipStatus status) {
        try (PreparedStatement ps =
                     holder(CFG.apiJdbcUrl()).connection().prepareStatement(SQL_UPDATE_FRIENDSHIP_STATUS)) {

            ps.setString(1, status.name());
            ps.setString(2, requesterId.toString());
            ps.setString(3, addresseeId.toString());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

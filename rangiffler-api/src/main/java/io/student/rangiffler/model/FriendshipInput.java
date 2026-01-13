package io.student.rangiffler.model;

import java.util.Objects;
import java.util.UUID;

public class FriendshipInput {
  private UUID user;

  private FriendshipAction action;

  public FriendshipInput() {
  }

  public FriendshipInput(UUID user, FriendshipAction action) {
    this.user = user;
    this.action = action;
  }

  public UUID getUser() {
    return user;
  }

  public void setUser(UUID user) {
    this.user = user;
  }

  public FriendshipAction getAction() {
    return action;
  }

  public void setAction(FriendshipAction action) {
    this.action = action;
  }

  @Override
  public String toString() {
    return "FriendshipInput{user='" + user + "', action='" + action + "'}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FriendshipInput that = (FriendshipInput) o;
    return Objects.equals(user, that.user) &&
        Objects.equals(action, that.action);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, action);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private UUID user;

    private FriendshipAction action;

    public FriendshipInput build() {
      FriendshipInput result = new FriendshipInput();
      result.user = this.user;
      result.action = this.action;
      return result;
    }

    public Builder user(UUID user) {
      this.user = user;
      return this;
    }

    public Builder action(FriendshipAction action) {
      this.action = action;
      return this;
    }
  }
}

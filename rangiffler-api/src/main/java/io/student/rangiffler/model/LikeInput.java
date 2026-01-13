package io.student.rangiffler.model;

import java.util.Objects;
import java.util.UUID;

public class LikeInput {
  private UUID user;

  public LikeInput() {
  }

  public LikeInput(UUID user) {
    this.user = user;
  }

  public UUID getUser() {
    return user;
  }

  public void setUser(UUID user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "LikeInput{user='" + user + "'}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LikeInput that = (LikeInput) o;
    return Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private UUID user;

    public LikeInput build() {
      LikeInput result = new LikeInput();
      result.user = this.user;
      return result;
    }

    public Builder user(UUID user) {
      this.user = user;
      return this;
    }
  }
}

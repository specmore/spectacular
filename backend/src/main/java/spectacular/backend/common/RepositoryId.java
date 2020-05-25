package spectacular.backend.common;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.util.Assert;
import spectacular.backend.github.domain.SearchCodeResultItem;

public class RepositoryId {
  private final String owner;
  private final String name;

  /**
   * Creates a RepositoryId that represents an unique repository.
   *
   * @param owner the repository owner name
   * @param name the name of the repository
   */
  public RepositoryId(@NotNull String owner, @NotNull String name) {
    Assert.hasText(owner, "owner cannot be null or empty");
    Assert.hasText(name, "name cannot be null or empty");

    this.owner = owner;
    this.name = name;
  }

  /**
   * Creates a Repository object using a / delimited string representing the repository Owner and Name.
   *
   * @param nameWithOwner a / delimited string containing the repository owner and name using the following format: {owner}/{name}
   * @return a new Repository
   */
  public static RepositoryId createForNameWithOwner(String nameWithOwner) {
    Assert.hasText(nameWithOwner, "nameWithOwner cannot be null or empty");

    var parts = nameWithOwner.split("/");
    if (parts.length != 2) {
      throw new IllegalArgumentException(
          "nameWithOwner needs to be in the format :owner-name/:repository-name");
    }

    return new RepositoryId(parts[0], parts[1]);
  }

  public static RepositoryId createRepositoryFrom(SearchCodeResultItem searchCodeResultItem) {
    return createRepositoryFrom(searchCodeResultItem.getRepository());
  }

  public static RepositoryId createRepositoryFrom(
      spectacular.backend.github.domain.Repository repository) {
    return createForNameWithOwner(repository.getFull_name());
  }

  public static RepositoryId createRepositoryFrom(
      spectacular.backend.github.graphql.Repository repository) {
    return createForNameWithOwner(repository.getNameWithOwner());
  }

  public String getOwner() {
    return owner;
  }

  public String getName() {
    return name;
  }

  public String getNameWithOwner() {
    return String.join("/", owner, name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RepositoryId that = (RepositoryId) o;
    return getOwner().equals(that.getOwner()) &&
        getName().equals(that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getOwner(), getName());
  }
}

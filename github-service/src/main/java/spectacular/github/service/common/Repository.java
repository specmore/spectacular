package spectacular.github.service.common;

import org.springframework.util.Assert;
import spectacular.github.service.github.domain.SearchCodeResultItem;

import java.util.Objects;

public class Repository {
    private final String owner;
    private final String name;
    private final String htmlUrl;

    public Repository(String owner, String name, String htmlUrl) {
        Assert.hasText(owner, "owner cannot be null or empty");
        Assert.hasText(name, "name cannot be null or empty");

        this.owner = owner;
        this.name = name;
        this.htmlUrl = htmlUrl;
    }

    public Repository(String nameWithOwner, String htmlUrl) {
        Assert.hasText(nameWithOwner, "nameWithOwner cannot be null or empty");

        var parts = nameWithOwner.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("nameWithOwner needs to be in the format :owner-name/:repository-name");
        }

        Assert.hasText(parts[0], "owner cannot be null or empty");
        Assert.hasText(parts[1], "name cannot be null or empty");

        this.owner = parts[0];
        this.name = parts[1];
        this.htmlUrl = htmlUrl;
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

    public String getHtmlUrl() {
        return htmlUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repository that = (Repository) o;
        return getOwner().equals(that.getOwner()) &&
                getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner(), getName());
    }

    public static Repository createRepositoryFrom(SearchCodeResultItem searchCodeResultItem) {
        return createRepositoryFrom(searchCodeResultItem.getRepository());
    }

    public static Repository createRepositoryFrom(spectacular.github.service.github.domain.Repository repository) {
        return new Repository(repository.getFull_name(), repository.getHtml_url());
    }
}

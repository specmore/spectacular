package spectacular.github.service.common;

import org.springframework.util.Assert;

import java.util.Objects;

public class Repository {
    private final String owner;
    private final String name;

    public Repository(String owner, String name) {
        Assert.hasText(owner, "owner cannot be null or empty");
        Assert.hasText(name, "name cannot be null or empty");

        this.owner = owner;
        this.name = name;
    }

    public Repository(String nameWithOwner) {
        Assert.hasText(nameWithOwner, "nameWithOwner cannot be null or empty");

        var parts = nameWithOwner.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("nameWithOwner needs to be in the format :owner-name/:repository-name");
        }

        Assert.hasText(parts[0], "owner cannot be null or empty");
        Assert.hasText(parts[1], "name cannot be null or empty");

        this.owner = parts[0];
        this.name = parts[1];
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
}

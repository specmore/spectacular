package spectacular.github.service.common;

public class Repository {
    private final String owner;
    private final String name;

    public Repository(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public Repository(String nameWithOwner) {
        var parts = nameWithOwner.split("/");
        this.owner = parts[0];
        if(parts.length > 1) this.name = parts[1];
        else this.name = "";
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
}

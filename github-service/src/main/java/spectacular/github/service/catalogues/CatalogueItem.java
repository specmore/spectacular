package spectacular.github.service.catalogues;

public class CatalogueItem {
    private final String name;
    private final String repo;

    public CatalogueItem(String name, String repo) {
        this.name = name;
        this.repo = repo;
    }

    public String getName() {
        return name;
    }

    public String getRepo() {
        return repo;
    }
}

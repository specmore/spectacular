package spectacular.backend.catalogues;

import spectacular.backend.common.Repository;
import spectacular.backend.github.domain.SearchCodeResultItem;

public class CatalogueId {
  private final Repository repository;
  private final String path;

  public CatalogueId(Repository repository, String path) {
    this.repository = repository;
    this.path = path;
  }

  public Repository getRepository() {
    return repository;
  }

  public String getPath() {
    return path;
  }

  public static CatalogueId createFrom(SearchCodeResultItem searchCodeResultItem) {
    return new CatalogueId(Repository.createRepositoryFrom(searchCodeResultItem.getRepository()), searchCodeResultItem.getPath());
  }
}

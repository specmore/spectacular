package spectacular.backend.catalogues;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import spectacular.backend.common.RepositoryId;

public class InterfaceId extends CatalogueId {
  protected final String interfaceName;

  public InterfaceId(@NotNull RepositoryId repositoryId,
                     @NotNull String path,
                     @NotNull String catalogueName,
                     @NotNull String interfaceName) {
    super(repositoryId, path, catalogueName);
    this.interfaceName = interfaceName;
  }

  public String getInterfaceName() {
    return interfaceName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    InterfaceId that = (InterfaceId) o;
    return getInterfaceName().equals(that.getInterfaceName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getInterfaceName());
  }

  @Override
  public String toString() {
    return "InterfaceId{" +
        "interfaceName='" + interfaceName + '\'' +
        ", catalogueName='" + catalogueName + '\'' +
        ", repository=" + repositoryId +
        ", path='" + path + '\'' +
        '}';
  }
}

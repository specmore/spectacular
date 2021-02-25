package spectacular.backend.cataloguemanifest;

import spectacular.backend.cataloguemanifest.model.Interface;
import spectacular.backend.common.CatalogueId;
import spectacular.backend.common.RepositoryId;

public class SpecFileRepositoryResolver {
  /**
   * A helper method to resolve the repository of a spec file for an interface config entry in a catalogue manifest.
   * @param catalogueInterfaceEntry the config entry for the interface in a catalogue manifest
   * @param catalogueId the id of the catalogue the interface entry belongs to
   * @return the resolved repository location of the spec file
   */
  public static RepositoryId resolveSpecFileRepository(Interface catalogueInterfaceEntry, CatalogueId catalogueId) {
    RepositoryId fileRepo;
    if (catalogueInterfaceEntry.getSpecFile().getRepo() != null) {
      fileRepo = RepositoryId.createForNameWithOwner(catalogueInterfaceEntry.getSpecFile().getRepo());
    } else {
      fileRepo = catalogueId.getRepositoryId();
    }
    return fileRepo;
  }
}

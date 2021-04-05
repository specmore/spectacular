package spectacular.backend.cataloguemanifest;

import static spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemError.createConfigError;
import static spectacular.backend.cataloguemanifest.GetCatalogueManifestConfigurationItemError.createNotFoundError;

import org.springframework.stereotype.Service;
import spectacular.backend.common.CatalogueId;

@Service
public class CatalogueInterfaceEntryConfigurationResolver {
  private final CatalogueEntryConfigurationResolver catalogueEntryConfigurationResolver;

  public CatalogueInterfaceEntryConfigurationResolver(
      CatalogueEntryConfigurationResolver catalogueEntryConfigurationResolver) {
    this.catalogueEntryConfigurationResolver = catalogueEntryConfigurationResolver;
  }

  public GetInterfaceEntryConfigurationResult getCatalogueInterfaceEntryConfiguration(CatalogueId catalogueId,
                                                                                      String interfaceName,
                                                                                      String username) {
    var getCatalogueEntryConfigurationResult = catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, username);

    if (getCatalogueEntryConfigurationResult.hasError()) {
      return GetInterfaceEntryConfigurationResult.createErrorResult(getCatalogueEntryConfigurationResult.getError());
    }

    var catalogueEntry = getCatalogueEntryConfigurationResult.getCatalogueEntry();
    if (catalogueEntry.getInterfaces() == null || !catalogueEntry.getInterfaces().getAdditionalProperties().containsKey(interfaceName)) {
      return GetInterfaceEntryConfigurationResult.createErrorResult(
          createNotFoundError("Interface entry not found in Catalogue entry in manifest file: " + catalogueId.getCombined() +
              ", with name: " + interfaceName));
    }

    var catalogueInterfaceEntry = catalogueEntry.getInterfaces().getAdditionalProperties().get(interfaceName);

    if (catalogueInterfaceEntry.getSpecFile() == null) {
      return GetInterfaceEntryConfigurationResult.createErrorResult(
          createConfigError("Interface entry in Catalogue entry in manifest file: " + catalogueId.getCombined() +
              ", with name: " + interfaceName + ", has no spec file location set."));
    }

    var manifestUri = getCatalogueEntryConfigurationResult.getManifestUri();
    return GetInterfaceEntryConfigurationResult.createSuccessfulResult(catalogueEntry, catalogueInterfaceEntry, manifestUri);
  }
}

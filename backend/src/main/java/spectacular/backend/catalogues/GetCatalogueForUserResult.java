package spectacular.backend.catalogues;

import spectacular.backend.api.model.Catalogue;

public class GetCatalogueForUserResult {
  private final boolean isNotFound;
  private final String notFoundErrorMessage;
  private final Catalogue catalogueDetails;

  private GetCatalogueForUserResult(boolean isNotFound, String notFoundErrorMessage, Catalogue catalogueDetails) {
    this.isNotFound = isNotFound;
    this.notFoundErrorMessage = notFoundErrorMessage;
    this.catalogueDetails = catalogueDetails;
  }

  public static GetCatalogueForUserResult createNotFoundResult(String error) {
    return new GetCatalogueForUserResult(true, error, null);
  }

  public static GetCatalogueForUserResult createFoundResult(Catalogue catalogueDetails) {
    return new GetCatalogueForUserResult(false, null, catalogueDetails);
  }

  public boolean isNotFound() {
    return isNotFound;
  }

  public String getNotFoundErrorMessage() {
    return notFoundErrorMessage;
  }

  public Catalogue getCatalogueDetails() {
    return catalogueDetails;
  }
}

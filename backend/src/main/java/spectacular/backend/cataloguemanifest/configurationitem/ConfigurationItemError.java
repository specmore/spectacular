package spectacular.backend.cataloguemanifest.configurationitem;

import static spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemErrorType.CONFIG_ERROR;
import static spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemErrorType.NOT_FOUND;

public class ConfigurationItemError {
  private final ConfigurationItemErrorType type;
  private final String message;

  private ConfigurationItemError(
      ConfigurationItemErrorType type, String message) {
    this.type = type;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public ConfigurationItemErrorType getType() {
    return type;
  }

  public static ConfigurationItemError createNotFoundError(String message) {
    return new ConfigurationItemError(NOT_FOUND, message);
  }

  public static ConfigurationItemError createConfigError(String message) {
    return new ConfigurationItemError(CONFIG_ERROR, message);
  }
}

package spectacular.backend.interfaces;

import java.util.Optional;
import org.springframework.http.MediaType;

public class InterfaceFileContents {
  private final String contents;
  private final String filePath;
  private final MediaType mediaTypeGuess;

  /**
   * Constructs a new InterfaceFileContents object.
   *
   * @param contents the decoded contents of the interface file retrieved from git
   * @param filePath the file path of the interface file in the git repository
   */
  public InterfaceFileContents(String contents, String filePath) {
    this.contents = contents;
    this.filePath = filePath;
    this.mediaTypeGuess = guessFileMediaType(filePath);
  }

  public String getContents() {
    return contents;
  }

  public String getFilePath() {
    return filePath;
  }

  public MediaType getMediaTypeGuess() {
    return mediaTypeGuess;
  }

  private static MediaType guessFileMediaType(String filePath) {
    var fileExtension = getFileExtension(filePath);
    if (fileExtension.isPresent()) {
      if (fileExtension.get().equalsIgnoreCase("yaml") || fileExtension.get().equalsIgnoreCase("yml")) {
        return MediaType.parseMediaType("application/yaml");
      } else if (fileExtension.get().equalsIgnoreCase("json")) {
        return MediaType.APPLICATION_JSON;
      }
    }
    return MediaType.TEXT_PLAIN;
  }

  private static Optional<String> getFileExtension(String filePath) {
    return Optional.ofNullable(filePath)
        .filter(f -> f.contains("."))
        .map(f -> f.substring(filePath.lastIndexOf(".") + 1));
  }
}

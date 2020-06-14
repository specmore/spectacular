package spectacular.backend.interfaces;

import java.util.Optional;

public class InterfaceFileContents {
  private final String contents;
  private final Optional<String> fileExtension;

  public InterfaceFileContents(String contents, Optional<String> fileExtension) {
    this.contents = contents;
    this.fileExtension = fileExtension;
  }

  public String getContents() {
    return contents;
  }

  public Optional<String> getFileExtension() {
    return fileExtension;
  }
}

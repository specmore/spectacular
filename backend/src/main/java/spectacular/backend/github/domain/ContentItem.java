package spectacular.backend.github.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Base64;

public class ContentItem {
  private final String name;
  private final String path;
  private final String sha;
  private final String type;
  private final URI html_url;
  private final String content;
  private final String encoding;
  private OffsetDateTime lastModified;

  public ContentItem(@JsonProperty("name") String name,
                     @JsonProperty("path") String path,
                     @JsonProperty("sha") String sha,
                     @JsonProperty("type") String type,
                     @JsonProperty("html_url") URI html_url,
                     @JsonProperty("content") String content,
                     @JsonProperty("encoding") String encoding) {
    this.name = name;
    this.path = path;
    this.sha = sha;
    this.type = type;
    this.html_url = html_url;
    this.content = content;
    this.encoding = encoding;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public String getSha() {
    return sha;
  }

  public String getType() {
    return type;
  }

  public URI getHtml_url() {
    return html_url;
  }

  public String getContent() {
    return content;
  }

  public String getEncoding() {
    return encoding;
  }

  @JsonIgnore
  public String getDecodedContent() throws UnsupportedEncodingException {
    if (encoding.equalsIgnoreCase("base64")) {
      byte[] decodedBytes = Base64.getMimeDecoder().decode(content);
      return new String(decodedBytes);
    }

    throw new UnsupportedEncodingException(encoding);
  }

  public OffsetDateTime getLastModified() {
    return lastModified;
  }

  public void setLastModified(OffsetDateTime lastModified) {
    this.lastModified = lastModified;
  }
}

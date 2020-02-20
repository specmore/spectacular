package spectacular.github.service.github.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContentItem {
    private final String name;
    private final String path;
    private final String type;
    private final String html_url;
    private final String content;
    private final String encoding;

    public ContentItem(@JsonProperty("name") String name,
                       @JsonProperty("path") String path,
                       @JsonProperty("type") String type,
                       @JsonProperty("html_url") String html_url,
                       @JsonProperty("content") String content,
                       @JsonProperty("encoding") String encoding) {
        this.name = name;
        this.path = path;
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

    public String getType() {
        return type;
    }

    public String getHtml_url() {
        return html_url;
    }

    public String getContent() {
        return content;
    }

    public String getEncoding() {
        return encoding;
    }
}

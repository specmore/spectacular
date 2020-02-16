package spectacular.github.service.specs.openapi;

import java.util.List;

public class OpenApiSpec {
    private final String title;
    private final String version;
    private final List<OpenApiOperation> operations;

    public OpenApiSpec(String title, String version, List<OpenApiOperation> operations) {
        this.title = title;
        this.version = version;
        this.operations = operations;
    }

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public List<OpenApiOperation> getOperations() { return operations; }
}

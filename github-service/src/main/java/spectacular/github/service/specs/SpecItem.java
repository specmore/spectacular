package spectacular.github.service.specs;

import spectacular.github.service.common.Repository;
import spectacular.github.service.specs.openapi.OpenApiSpecParseResult;

public class SpecItem {
    private final Repository repository;
    private final String filePath;
    private final OpenApiSpecParseResult parseResult;

    public SpecItem(Repository repository, String filePath, OpenApiSpecParseResult parseResult) {
        this.repository = repository;
        this.filePath = filePath;
        this.parseResult = parseResult;
    }

    public Repository getRepository() {
        return repository;
    }

    public String getFilePath() {
        return filePath;
    }

    public OpenApiSpecParseResult getParseResult() {
        return parseResult;
    }
}

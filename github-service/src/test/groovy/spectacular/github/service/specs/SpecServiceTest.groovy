package spectacular.github.service.specs

import org.springframework.web.client.HttpClientErrorException
import spectacular.github.service.common.Repository
import spectacular.github.service.github.RestApiClient
import spectacular.github.service.github.domain.ContentItem
import spock.lang.Specification

class SpecServiceTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def specService = new SpecService(restApiClient)

    def "Get spec item for spec repo and file path returns spec item"() {
        given: "a spec file repo and path"
        def specFileRepo = new Repository("test-owner", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"

        and: "the spec file has a valid yaml content"
        def specFileContent = "openapi: 3.0.1\n" +
                "info:\n" +
                "  title: An empty API spec\n" +
                "  version: \"0.1.0\"\n" +
                "  contact: \n" +
                "    name: \"The team name going to implement this spec\"\n" +
                "    url: \"https://github.com/test-owner/actual-api-implementation-repository\"\n" +
                "tags: \n" +
                "  - name: Sample Resource\n" +
                "    description: \"Sample Resource description\"\n" +
                "paths: {}\n" +
                "components:\n" +
                "  schemas: {}"
        def contentItem = new ContentItem("htmlUrl", specFilePath, "file", "some url", Base64.getEncoder().encodeToString(specFileContent.getBytes()), "base64")

        when: "the spec item is retrieved"
        def specItem = specService.getSpecItem(specFileRepo, specFilePath)

        then: "a spec item is returned with the spec file's repository, filepath and html url"
        specItem
        specItem.getRepository() == specFileRepo
        specItem.getFilePath() == specFilePath
        specItem.getHtmlUrl() == "some url"

        and: "the content of the spec file is retrieved from github"
        1 * restApiClient.getRepositoryContent(specFileRepo, specFilePath, null) >> contentItem

        and: "the spec item contains a parse results of the content"
        specItem.getParseResult()

        and: "the parse result has no errors and an open api spec result"
        !specItem.getParseResult().hasErrors()
        specItem.getParseResult().getOpenApiSpec()

        and: "the openapi spec has a title and version set"
        specItem.getParseResult().getOpenApiSpec().getTitle() == "An empty API spec"
        specItem.getParseResult().getOpenApiSpec().getVersion() == "0.1.0"
    }

    def "Get spec item returns parse error for spec file contents not found"() {
        given: "a spec file repo and path"
        def specFileRepo = new Repository("test-owner", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"

        when: "the spec item is retrieved"
        def specItem = specService.getSpecItem(specFileRepo, specFilePath)

        then: "a spec item is returned with the spec file's repository and filepath"
        specItem
        specItem.getRepository() == specFileRepo
        specItem.getFilePath() == specFilePath

        and: "the content of the spec file not found on github"
        1 * restApiClient.getRawRepositoryContent(specFileRepo, specFilePath, null) >> {
            throw new HttpClientErrorException.NotFound("not found", null, null, null)
        }

        and: "the spec item contains a parse results of the content"
        specItem.getParseResult()

        and: "the parse result has a no content found error"
        specItem.getParseResult().hasErrors()
        specItem.getParseResult().getErrors()[0] == "The spec file could not be found."

        and: "the parse result has no open api spec"
        !specItem.getParseResult().getOpenApiSpec()
    }
}

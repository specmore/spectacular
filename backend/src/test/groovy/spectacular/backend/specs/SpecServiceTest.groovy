package spectacular.backend.specs

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import spectacular.backend.github.RestApiClient
import spock.lang.Specification

import java.time.ZoneId
import java.time.ZonedDateTime

class SpecServiceTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def specService = new SpecService(restApiClient)

    def "Get spec item for spec repo and file path returns spec item"() {
        given: "a spec file repo, path and ref"
        def specFileRepo = new spectacular.backend.common.Repository("test-owner", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"
        def ref = "xyz"

        and: "the spec file has a valid yaml content"
        def encodedContent = "b3BlbmFwaTogMy4wLjEKaW5mbzoKICB0aXRsZTogQW4gZW1wdHkgQVBJIHNw\n" +
                "ZWMKICB2ZXJzaW9uOiAiMC4xLjAiCiAgY29udGFjdDogCiAgICBuYW1lOiAi\n" +
                "VGhlIHRlYW0gbmFtZSBnb2luZyB0byBpbXBsZW1lbnQgdGhpcyBzcGVjIgog\n" +
                "ICAgdXJsOiAiaHR0cHM6Ly9naXRodWIuY29tL3Rlc3Qtb3duZXIvYWN0dWFs\n" +
                "LWFwaS1pbXBsZW1lbnRhdGlvbi1yZXBvc2l0b3J5Igp0YWdzOiAKICAtIG5h\n" +
                "bWU6IFNhbXBsZSBSZXNvdXJjZQogICAgZGVzY3JpcHRpb246ICJTYW1wbGUg\n" +
                "UmVzb3VyY2UgZGVzY3JpcHRpb24iCnBhdGhzOiB7fQpjb21wb25lbnRzOgog\n" +
                "IHNjaGVtYXM6IHt9"
        def contentItem = new spectacular.backend.github.domain.ContentItem("htmlUrl", specFilePath, "some sha", "file", "some url", encodedContent, "base64")

        when: "the spec item is retrieved"
        def specItem = specService.getSpecItem(specFileRepo, specFilePath, ref)

        then: "a spec item is returned with the spec file's repository, filepath, ref and html url"
        specItem
        specItem.getRepository() == specFileRepo
        specItem.getFilePath() == specFilePath
        specItem.getRef() == ref
        specItem.getHtmlUrl() == "some url"

        and: "the content of the spec file is retrieved from github"
        1 * restApiClient.getRepositoryContent(specFileRepo, specFilePath, ref) >> contentItem

        and: "the spec item contains a parse results of the content"
        specItem.getParseResult()

        and: "the parse result has no errors and an open api spec result"
        !specItem.getParseResult().hasErrors()
        specItem.getParseResult().getOpenApiSpec()

        and: "the openapi spec has a title and version set"
        specItem.getParseResult().getOpenApiSpec().getTitle() == "An empty API spec"
        specItem.getParseResult().getOpenApiSpec().getVersion() == "0.1.0"
    }

    def "Get spec item for spec repo and file path returns spec item with last modified date for content item with last modified date"() {
        given: "a spec file repo, path and ref"
        def specFileRepo = new spectacular.backend.common.Repository("test-owner", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"
        def ref = "xyz"

        and: "a content item with a last modified date for the spec file"
        def lastModifiedDate = ZonedDateTime.of(2020, 2, 16, 20, 46, 03, 0, ZoneId.of("GMT")) //"Sun, 16 Feb 2020 20:46:03 GMT"
        def contentItem = new spectacular.backend.github.domain.ContentItem("htmlUrl", specFilePath, "some sha", "file", "some url", "", "base64")
        contentItem.setLastModified(lastModifiedDate.toInstant())

        when: "the spec item is retrieved"
        def specItem = specService.getSpecItem(specFileRepo, specFilePath, ref)

        then: "the content item of the spec file is retrieved from github"
        1 * restApiClient.getRepositoryContent(specFileRepo, specFilePath, ref) >> contentItem

        and: "a spec item is returned with the content item's last modified date"
        specItem
        specItem.getLastModified() == lastModifiedDate.toInstant()
    }

    def "Get spec item returns parse error for spec file contents not found"() {
        given: "a spec file repo, path and ref"
        def specFileRepo = new spectacular.backend.common.Repository("test-owner", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"
        def ref = "xyz"

        when: "the spec item is retrieved"
        def specItem = specService.getSpecItem(specFileRepo, specFilePath, ref)

        then: "a spec item is returned with the spec file's repository and filepath"
        specItem
        specItem.getRepository() == specFileRepo
        specItem.getFilePath() == specFilePath

        and: "the content of the spec file not found on github"
        1 * restApiClient.getRepositoryContent(specFileRepo, specFilePath, ref) >> {
            throw HttpClientErrorException.create(HttpStatus.NOT_FOUND, "not found", null, null, null)
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
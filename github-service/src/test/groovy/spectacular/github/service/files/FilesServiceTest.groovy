package spectacular.github.service.files

import spectacular.github.service.catalogues.Catalogue
import spectacular.github.service.catalogues.CatalogueManifest
import spectacular.github.service.catalogues.CatalogueService
import spectacular.github.service.catalogues.SpecFileLocation
import spectacular.github.service.common.Repository
import spectacular.github.service.github.RestApiClient
import spock.lang.Specification

class FilesServiceTest extends Specification {
    def catalogueServiceMock = Mock(CatalogueService)
    def restApiMock = Mock(RestApiClient)
    def fileService = new FilesService(catalogueServiceMock, restApiMock)

    def "get file content for spec path in catalogue manifest from different repo returns the raw file contents from github"() {
        given: "a user with access to the catalogue repo"
        def username = "test-user"

        and: "a spec file repo and path with content"
        def specFileRepo = new Repository("test-owner2", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"
        def specFileContent = "test content"

        and: "a catalogue repo with a manifest containing the requested spec file"
        def catalogueRepo = new Repository("test-owner", "catalogue-repo")
        def specFileLocation = new SpecFileLocation(specFileRepo.getNameWithOwner(), specFilePath)
        def catalogueManifest = new CatalogueManifest("test manifest", "test description", [specFileLocation])
        def catalogue = new Catalogue(catalogueRepo, catalogueManifest, null, null)

        when: "retrieving the spec file contents"
        def result = fileService.getFileContent(catalogueRepo, specFileRepo, specFilePath, null, username)

        then: "the catalogue is retrieved for the repo and user"
        1 * catalogueServiceMock.getCatalogueForRepoAndUser(catalogueRepo, username) >> catalogue

        and: "the contents is retrieved from the spec file repo and path from the github api"
        1 * restApiMock.getRawRepositoryContent(specFileRepo, specFilePath, null) >> specFileContent
        result == specFileContent
    }

    def "get file content for spec path in catalogue manifest in same repo returns the raw file contents from github"() {
        given: "a user with access to the catalogue repo"
        def username = "test-user"

        and: "a spec file repo and path with content"
        def specFileRepo = new Repository("test-owner2", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"
        def specFileContent = "test content"

        and: "a catalogue manifest in the same repo containing the requested spec file but with out a repo in the location"
        def specFileLocation = new SpecFileLocation((String)null, specFilePath)
        def catalogueManifest = new CatalogueManifest("test manifest", "test description", [specFileLocation])
        def catalogue = new Catalogue(specFileRepo, catalogueManifest, null, null)

        when: "retrieving the spec file contents"
        def result = fileService.getFileContent(specFileRepo, specFileRepo, specFilePath, null, username)

        then: "the catalogue is retrieved for the repo and user"
        1 * catalogueServiceMock.getCatalogueForRepoAndUser(specFileRepo, username) >> catalogue

        and: "the contents is retrieved from the spec file repo and path from the github api"
        1 * restApiMock.getRawRepositoryContent(specFileRepo, specFilePath, null) >> specFileContent
        result == specFileContent
    }

    def "get file content retrieves content from the ref specified"() {
        given: "a user with access to the catalogue repo"
        def username = "test-user"

        and: "a spec file repo and path with content"
        def specFileRepo = new Repository("test-owner2", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"
        def specFileContent = "test content"

        and: "a catalogue manifest in the same repo containing the requested spec file but with out a repo in the location"
        def specFileLocation = new SpecFileLocation((String)null, specFilePath)
        def catalogueManifest = new CatalogueManifest("test manifest", "test description", [specFileLocation])
        def catalogue = new Catalogue(specFileRepo, catalogueManifest, null, null)

        and: "a ref specified for a test branch"
        def refName = "test-branch"

        when: "retrieving the spec file contents"
        def result = fileService.getFileContent(specFileRepo, specFileRepo, specFilePath, refName, username)

        then: "the catalogue is retrieved for the repo and user"
        1 * catalogueServiceMock.getCatalogueForRepoAndUser(specFileRepo, username) >> catalogue

        and: "the contents is retrieved from the github api using the ref specified"
        1 * restApiMock.getRawRepositoryContent(specFileRepo, specFilePath, "test-branch") >> specFileContent
        result == specFileContent
    }

    def "get file content for spec path not contained in catalogue manifest returns null contents"() {
        given: "a user with access to the catalogue repo"
        def username = "test-user"

        and: "a spec file repo and path with content"
        def specFileRepo = new Repository("test-owner2", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"
        def specFileContent = "test content"

        and: "a catalogue repo with a manifest not containing the requested spec file"
        def catalogueRepo = new Repository("test-owner", "catalogue-repo")
        def specFileLocation = new SpecFileLocation("another-owner/another-repo", "another/spec-file.yaml")
        def catalogueManifest = new CatalogueManifest("test manifest", "test description", [specFileLocation])
        def catalogue = new Catalogue(catalogueRepo, catalogueManifest, null, null)

        when: "retrieving the spec file contents"
        def result = fileService.getFileContent(catalogueRepo, specFileRepo, specFilePath, null, username)

        then: "the catalogue is retrieved for the repo and user"
        1 * catalogueServiceMock.getCatalogueForRepoAndUser(catalogueRepo, username) >> catalogue

        and: "no contents is retrieved for the spec file repo and path from the github api"
        0 * restApiMock.getRawRepositoryContent(specFileRepo, specFilePath, null) >> specFileContent
        !result
    }

    def "get file content for catalogue that the user has no access to or does not exist returns null contents"() {
        given: "a user with access to the catalogue repo"
        def username = "test-user"

        and: "a spec file repo and path with content"
        def specFileRepo = new Repository("test-owner2", "spec-repo");
        def specFilePath = "test-specs/example-spec.yaml"
        def specFileContent = "test content"

        and: "a catalogue repo the user does not have access to or does not exist"
        def catalogueRepo = new Repository("test-owner", "catalogue-repo")

        when: "retrieving the spec file contents"
        def result = fileService.getFileContent(catalogueRepo, specFileRepo, specFilePath, null, username)

        then: "the catalogue cannot be retrieved the repo and user"
        1 * catalogueServiceMock.getCatalogueForRepoAndUser(catalogueRepo, username) >> null

        and: "no contents is retrieved for the spec file repo and path from the github api"
        0 * restApiMock.getRawRepositoryContent(specFileRepo, specFilePath, null) >> specFileContent
        !result
    }
}

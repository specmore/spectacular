package spectacular.backend.common

import spock.lang.Specification

class CatalogueIdTest extends Specification {
    def "CreateFrom valid yml file path encodedId"() {
        given: "A catalogue Id"
        def repositoryId = new RepositoryId("test-owner", "test-repo")
        def catalogueId = new CatalogueId(repositoryId, "x/y/z/some-file-path.yml", "some-name")

        and: "combined id"
        def combined = catalogueId.getCombined()

        when: "creating a new catalogueId from the combined"
        def newCatalogue = CatalogueId.createFrom(combined)

        then: "the repository is the same"
        newCatalogue.getRepositoryId() == repositoryId

        and: "the path is the same"
        newCatalogue.getPath() == "x/y/z/some-file-path.yml"

        and: "the catalogue name is the same"
        newCatalogue.getCatalogueName() == "some-name"
    }
}

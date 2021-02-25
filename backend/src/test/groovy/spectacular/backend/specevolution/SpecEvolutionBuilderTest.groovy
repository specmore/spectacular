package spectacular.backend.specevolution

import spectacular.backend.cataloguemanifest.model.ReleaseTagConfig
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.Tag
import spock.lang.Specification

class SpecEvolutionBuilderTest extends Specification {
    def mainBranchName = "main"

    def evolutionBranchBuilder = Mock(EvolutionBranchBuilder)
    def restApiClient = Mock(RestApiClient)

    def specEvolutionBuilder = new SpecEvolutionBuilder(restApiClient, evolutionBranchBuilder)

    def "GenerateSpecEvolution returns all tags if no release tag config is set"() {
        given: "a spec evolution config with no tags config set"
        def specEvolutionConfig = new SpecEvolutionConfig()

        and: "several tags on the spec repository"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def tags = [
                new Tag("tag-123"),
                new Tag("x-tag-456"),
                new Tag("y-tag-789")
        ]

        when: "the spec evolution is built"
        def specEvolution = specEvolutionBuilder.generateSpecEvolution("interfaceX", specEvolutionConfig, specRepoId, mainBranchName)

        then: "the tags for the repository are retrieved"
        1 * restApiClient.getRepositoryTags(specRepoId) >> tags

        and: "the evolutionBranchBuilder is called for the main branch with all the tags"
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, mainBranchName, tags)
    }

    def "GenerateSpecEvolution returns all tags if release tag config is set without a prefix"() {
        given: "a spec evolution config with no tags config set"
        def tagConfig = new ReleaseTagConfig()
        def specEvolutionConfig = new SpecEvolutionConfig().withReleaseTagConfig(tagConfig)

        and: "several tags on the spec repository"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def tags = [
                new Tag("tag-123"),
                new Tag("x-tag-456"),
                new Tag("y-tag-789")
        ]

        when: "the spec evolution is built"
        def specEvolution = specEvolutionBuilder.generateSpecEvolution("interfaceX", specEvolutionConfig, specRepoId, mainBranchName)

        then: "the tags for the repository are retrieved"
        1 * restApiClient.getRepositoryTags(specRepoId) >> tags

        and: "the evolutionBranchBuilder is called for the main branch with all the tags"
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, mainBranchName, tags)
    }

    def "GenerateSpecEvolution returns only matching tags if release tag config is set with a prefix"() {
        given: "a spec evolution config with no tags config set"
        def tagConfig = new ReleaseTagConfig().withTagPrefix("x-")
        def specEvolutionConfig = new SpecEvolutionConfig().withReleaseTagConfig(tagConfig)

        and: "several tags on the spec repository"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def tags = [
                new Tag("tag-123"),
                new Tag("x-tag-456"),
                new Tag("y-tag-789")
        ]

        when: "the spec evolution is built"
        def specEvolution = specEvolutionBuilder.generateSpecEvolution("interfaceX", specEvolutionConfig, specRepoId, mainBranchName)

        then: "the tags for the repository are retrieved"
        1 * restApiClient.getRepositoryTags(specRepoId) >> tags

        and: "the evolutionBranchBuilder is called for the main branch with only the matching tags"
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, mainBranchName, [tags[1]])
    }
}

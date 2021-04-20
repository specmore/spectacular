package spectacular.backend.specevolution

import spectacular.backend.cataloguemanifest.model.MainBranchConfig
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.Repository
import spock.lang.Specification

class SpecEvolutionConfigResolverTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def specEvolutionConfigResolver = new SpecEvolutionConfigResolver(restApiClient)

    def "ResolveConfig defaults the main branch name to 'main' if no main branch config and default branch on repo is set"() {
        given: "a spec evolution config with no main branch config set"
        def specEvolutionConfig = new SpecEvolutionConfig()

        and: "a spec repository with no default branch"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def specRepoDetails = new Repository(1, specRepoId.nameWithOwner, null, null)

        when: "the spec evolution config is resolved"
        def result = specEvolutionConfigResolver.resolveConfig(specEvolutionConfig, specRepoId)

        then: "the repository details are retrieved"
        1 * restApiClient.getRepository(specRepoId) >> specRepoDetails

        and: "the resolved main branch name defaults to main"
        result.getMainBranchConfig().getBranchName() == "main"
    }

    def "ResolveConfig defaults the main branch name to 'main' if the main branch config is set without a branch name and default branch on repo is not set"() {
        given: "a spec evolution config with no main branch config set"
        def mainBranchConfig = new MainBranchConfig()
        def specEvolutionConfig = new SpecEvolutionConfig().withMainBranchConfig(mainBranchConfig)

        and: "a spec repository with no default branch"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def specRepoDetails = new Repository(1, specRepoId.nameWithOwner, null, null)

        when: "the spec evolution config is resolved"
        def result = specEvolutionConfigResolver.resolveConfig(specEvolutionConfig, specRepoId)

        then: "the repository details are retrieved"
        1 * restApiClient.getRepository(specRepoId) >> specRepoDetails

        and: "the resolved main branch name defaults to main"
        result.getMainBranchConfig().getBranchName() == "main"
    }

    def "ResolveConfig uses the default branch name of the repo if the main branch config is not set"() {
        given: "a spec evolution config with no main branch config set"
        def specEvolutionConfig = new SpecEvolutionConfig()

        and: "a spec repository with a default branch"
        def defaultBranchName = "default"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def specRepoDetails = new Repository(1, specRepoId.nameWithOwner, null, defaultBranchName)

        when: "the spec evolution config is resolved"
        def result = specEvolutionConfigResolver.resolveConfig(specEvolutionConfig, specRepoId)

        then: "the repository details are retrieved"
        1 * restApiClient.getRepository(specRepoId) >> specRepoDetails

        and: "the resolved main branch name is the repository default branch"
        result.getMainBranchConfig().getBranchName() == defaultBranchName
    }

    def "ResolveConfig uses the main branch config if it is set"() {
        given: "a spec evolution config with a main branch name config set"
        def mainBranchName = "a-main-branch"
        def mainBranchConfig = new MainBranchConfig().withBranchName(mainBranchName)
        def specEvolutionConfig = new SpecEvolutionConfig().withMainBranchConfig(mainBranchConfig)

        and: "a spec repository"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")

        when: "the spec evolution config is resolved"
        def result = specEvolutionConfigResolver.resolveConfig(specEvolutionConfig, specRepoId)

        then: "no repository details are retrieved"
        0 * restApiClient.getRepository(specRepoId)

        and: "the resolved main branch name to the config's main branch name"
        result.getMainBranchConfig().getBranchName() == mainBranchName
    }
}

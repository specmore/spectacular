package spectacular.backend.specevolution

import spectacular.backend.api.model.EvolutionItem
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.Tag
import spectacular.backend.github.refs.BranchRef
import spectacular.backend.github.refs.RefRepository
import spock.lang.Specification

class SpecEvolutionBuilderTest extends Specification {
    def interfaceName = "interfaceX"
    def specEvolutionConfig = new SpecEvolutionConfig()
    def specFilePath = "spec/file.yaml"
    def specRepoId = RepositoryId.createForNameWithOwner("test/repo")

    def evolutionBranchBuilder = Mock(EvolutionBranchBuilder)

    def specEvolutionBuilder = new SpecEvolutionBuilder(evolutionBranchBuilder)

    def "GenerateSpecEvolution returns a SpecEvolution item with the interface name and config used"() {
        given: "spec evolution data with config"
        def specEvolutionData = new SpecEvolutionData(Optional.empty(), Collections.emptyList(), Collections.emptyList(), specEvolutionConfig)

        when: "the spec evolution is built"
        def specEvolution = specEvolutionBuilder.generateSpecEvolution(interfaceName, specRepoId, specFilePath, specEvolutionData)

        then: "the spec evolution returned has the interface name"
        specEvolution.getInterfaceName() == interfaceName

        and: "has the spec config"
        specEvolution.getConfigUsed() == specEvolutionConfig
    }

    def "GenerateSpecEvolution returns a main branch if a main branch was found"() {
        given: "a main branch was found"
        def mainBranch = new BranchRef("main", null, null)

        when: "the spec evolution is built"
        def specEvolutionData = new SpecEvolutionData(Optional.of(mainBranch), Collections.emptyList(), Collections.emptyList(), specEvolutionConfig)
        def specEvolution = specEvolutionBuilder.generateSpecEvolution(interfaceName, specRepoId, specFilePath, specEvolutionData)

        then: "the evolutionBranchBuilder is called for the main branch"
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, mainBranch.getName(), _, _) >> []
    }

    def "GenerateSpecEvolution returns release branches if release branches were found"() {
        given: "release branches were found"
        def releaseBranch1 = new BranchRef("release1", null, null)
        def releaseBranch2 = new BranchRef("release2", null, null)

        when: "the spec evolution is built"
        def specEvolutionData = new SpecEvolutionData(Optional.empty(), Collections.emptyList(), [ releaseBranch1, releaseBranch2 ], specEvolutionConfig)
        def specEvolution = specEvolutionBuilder.generateSpecEvolution(interfaceName, specRepoId, specFilePath, specEvolutionData)

        then: "the evolutionBranchBuilder is called for each release branch"
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, releaseBranch1.getName(), _, _) >> []
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, releaseBranch2.getName(), _, _) >> []
    }

    def "GenerateSpecEvolution uses a tag only once per branch"() {
        given: "two release branches were found"
        def releaseBranch1 = new BranchRef("release1", null, null)
        def releaseBranch2 = new BranchRef("release2", null, null)

        and: "two tags were found"
        def tag1 = new Tag("tag1")
        def tag2 = new Tag("tag2")

        and: "only the first tag is on the first release branch"
        def tag1EvolutionItem = new EvolutionItem().tag(tag1.getName());
        def releaseBranch1EvolutionItems = [tag1EvolutionItem]

        when: "the spec evolution is built"
        def specEvolutionData = new SpecEvolutionData(Optional.empty(), [tag1, tag2], [releaseBranch1, releaseBranch2], specEvolutionConfig)
        def specEvolution = specEvolutionBuilder.generateSpecEvolution(interfaceName, specRepoId, specFilePath, specEvolutionData)

        then: "the evolutionBranchBuilder is called for the first release branch with both tags"
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, releaseBranch1.getName(), [tag1, tag2], _) >> releaseBranch1EvolutionItems

        and: "the evolutionBranchBuilder is called for the second release branch with the unused tag"
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, releaseBranch2.getName(), [tag2], _) >> []
    }
}

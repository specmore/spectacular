package spectacular.backend.specevolution

import spectacular.backend.api.model.EvolutionItem
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.refs.BranchRef
import spectacular.backend.github.refs.TagRef
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
        given: "branch data for a main branch is found"
        def mainBranchRef = new BranchRef("main", null, null)
        def mainBranchData = new BranchData(mainBranchRef, Collections.emptyList())
        def specEvolutionData = new SpecEvolutionData(Optional.of(mainBranchData), Collections.emptyList(), Collections.emptyList(), specEvolutionConfig)


        when: "the spec evolution is built"
        def specEvolution = specEvolutionBuilder.generateSpecEvolution(interfaceName, specRepoId, specFilePath, specEvolutionData)

        then: "the evolutionBranchBuilder is called for the main branch"
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, mainBranchRef, _, _) >> []
    }

    def "GenerateSpecEvolution returns release branches if release branches were found"() {
        given: "branch data for release branches were found"
        def releaseBranchRef1 = new BranchRef("release1", null, null)
        def releaseBranchData1 = new BranchData(releaseBranchRef1, Collections.emptyList())
        def releaseBranchRef2 = new BranchRef("release2", null, null)
        def releaseBranchData2 = new BranchData(releaseBranchRef2, Collections.emptyList())
        def specEvolutionData = new SpecEvolutionData(Optional.empty(), Collections.emptyList(), [ releaseBranchData1, releaseBranchData2 ], specEvolutionConfig)

        when: "the spec evolution is built"
        def specEvolution = specEvolutionBuilder.generateSpecEvolution(interfaceName, specRepoId, specFilePath, specEvolutionData)

        then: "the evolutionBranchBuilder is called for each release branch"
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, releaseBranchRef1, _, _) >> []
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, releaseBranchRef2, _, _) >> []
    }

    def "GenerateSpecEvolution uses a tag only once per branch"() {
        given: "branch data for release branches were found"
        def releaseBranchRef1 = new BranchRef("release1", null, null)
        def releaseBranchData1 = new BranchData(releaseBranchRef1, Collections.emptyList())
        def releaseBranchRef2 = new BranchRef("release2", null, null)
        def releaseBranchData2 = new BranchData(releaseBranchRef2, Collections.emptyList())

        and: "two tags were found"
        def tagRef1 = new TagRef("tag1", "commit1")
        def tagRef2 = new TagRef("tag2", "commit2")

        and: "only the first tag is on the first release branch"
        def tag1EvolutionItem = new EvolutionItem().tags([tagRef1.getName()]);
        def releaseBranch1EvolutionItems = [tag1EvolutionItem]

        when: "the spec evolution is built"
        def specEvolutionData = new SpecEvolutionData(Optional.empty(), [tagRef1, tagRef2], [releaseBranchData1, releaseBranchData2], specEvolutionConfig)
        def specEvolution = specEvolutionBuilder.generateSpecEvolution(interfaceName, specRepoId, specFilePath, specEvolutionData)

        then: "the evolutionBranchBuilder is called for the first release branch with both tags"
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, releaseBranchRef1, [tagRef1, tagRef2], _) >> releaseBranch1EvolutionItems

        and: "the evolutionBranchBuilder is called for the second release branch with the unused tag"
        1 * evolutionBranchBuilder.generateEvolutionItems(specRepoId, releaseBranchRef2, [tagRef2], _) >> []
    }
}

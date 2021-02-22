package spectacular.backend.specevolution


import spectacular.backend.common.RepositoryId
import spectacular.backend.github.RestApiClient
import spectacular.backend.github.domain.Comparison
import spectacular.backend.github.domain.Tag
import spock.lang.Specification

class EvolutionBranchBuilderTest extends Specification {
    def restApiClient = Mock(RestApiClient)
    def evolutionBranchBuilder = new EvolutionBranchBuilder(restApiClient)

    def "GenerateEvolutionItems only returns evolution items for tags behind the branch"() {
        given: "a spec file repository and branch"
        def specFileRepoId = RepositoryId.createForNameWithOwner("test-owner/test-repo")
        def branchName = "test-branch"

        and: "a tag behind the branch head"
        def behindTag = new Tag("behindTag")
        def behindTagComparison = new Comparison(null, "behind", 0, 1, 1)

        and: "a tag ahead of the branch head"
        def aheadTag = new Tag("aheadTag")
        def aheadTagComparison = new Comparison(null, "ahead", 1, 0, 1)

        when: "generating the evolution items for the branch"
        def evolutionItems = evolutionBranchBuilder.generateEvolutionItems(specFileRepoId, branchName, [behindTag, aheadTag])

        then: "both tags are compared to the main branch"
        1 * restApiClient.getComparison(specFileRepoId, branchName, "behindTag") >> behindTagComparison
        1 * restApiClient.getComparison(specFileRepoId, branchName, "aheadTag") >> aheadTagComparison

        and: "the spec evolution's main branch has an item for the behind tag"
        evolutionItems.size() == 1
        evolutionItems.first().tag == "behindTag"
    }
}

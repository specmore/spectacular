package spectacular.backend.specevolution

import spectacular.backend.api.model.EvolutionBranch
import spectacular.backend.api.model.EvolutionItem
import spectacular.backend.api.model.PullRequest
import spectacular.backend.api.model.SpecEvolution
import spectacular.backend.api.model.SpecItem
import spock.lang.Specification

class SpecEvolutionSummaryMapperTest extends Specification {
    def specEvolutionSummaryMapper = new SpecEvolutionSummaryMapper()

    def aBranchWithPullRequestEvolutionItems(String branchName, int numberPrItems) {
        def prEvolutionItems = (1..numberPrItems).collect{
            def prBranchName = "prBranch-$it"
            def pullRequest = new PullRequest().branchName(prBranchName)
            return new EvolutionItem().ref(prBranchName).pullRequest(pullRequest)
        }
        return new EvolutionBranch().branchName(branchName).evolutionItems(prEvolutionItems)
    }

    def "MapSpecEvolutionToSummary returns a SpecEvolutionSummary with the same interfaceName"() {
        given: "a specEvolution with an interfaceName"
        def specEvolution = new SpecEvolution().interfaceName('testInterface1')

        when: "the SpecEvolution object is mapped to a SpecEvolutionSummary object"
        def summary = specEvolutionSummaryMapper.mapSpecEvolutionToSummary(specEvolution)

        then: "the SpecEvolutionSummary has the same interfaceName"
        summary.getInterfaceName() == specEvolution.getInterfaceName()
    }

    def "MapSpecEvolutionToSummary sets the latestAgreed from the head evolutionItem on the main evolution branch"() {
        given: "a SpecItem"
        def specItem = new SpecItem();

        and: "an EvolutionBranch with a branch head evolutionItem for the SpecItem"
        def mainBranchName = "mainBranch"
        def branchHeadEvolutionItem = new EvolutionItem().ref(mainBranchName).branchName(mainBranchName).specItem(specItem)
        def mainEvolutionBranch = new EvolutionBranch().branchName(mainBranchName).addEvolutionItemsItem(branchHeadEvolutionItem)

        and: "a SpecEvolution with the EvolutionBranch as the main branch"
        def specEvolution = new SpecEvolution().main(mainEvolutionBranch)

        when: "the SpecEvolution object is mapped to a SpecEvolutionSummary object"
        def summary = specEvolutionSummaryMapper.mapSpecEvolutionToSummary(specEvolution)

        then: "the SpecEvolutionSummary has the SpecItem set as the latestAgreed SpecItem"
        summary.getLatestAgreed() == specItem
    }

    def "MapSpecEvolutionToSummary calculates upcomingReleaseCount from number of release branches"() {
        given: "a specEvolution with 2 release branches"
        def releaseEvolutionBranch1 = new EvolutionBranch().branchName("releaseBranch1")
        def releaseEvolutionBranch2 = new EvolutionBranch().branchName("releaseBranch2")
        def specEvolution = new SpecEvolution().releases([releaseEvolutionBranch1, releaseEvolutionBranch2])

        when: "the SpecEvolution object is mapped to a SpecEvolutionSummary object"
        def summary = specEvolutionSummaryMapper.mapSpecEvolutionToSummary(specEvolution)

        then: "the SpecEvolutionSummary has a upcomingReleaseCount of 2"
        summary.getUpcomingReleaseCount() == 2
    }

    def "MapSpecEvolutionToSummary calculates proposedChangesCount from pullRequest evolutionItems across main and all release branches"() {
        given: "release evolution branches with 2 pull request items each"
        def releaseEvolutionBranches = (1..2).collect({
            def releaseBranchName = "releaseBranch-$it"
            return aBranchWithPullRequestEvolutionItems(releaseBranchName, 2)
        })

        and: "a main evolution branch wit 1 pull request item"
        def mainEvolutionBranch = aBranchWithPullRequestEvolutionItems("mainBranch", 1)

        and: "a specEvolution with all the branches"
        def specEvolution = new SpecEvolution().main(mainEvolutionBranch).releases(releaseEvolutionBranches)

        when: "the SpecEvolution object is mapped to a SpecEvolutionSummary object"
        def summary = specEvolutionSummaryMapper.mapSpecEvolutionToSummary(specEvolution)

        then: "the SpecEvolutionSummary has a proposedChangesCount of 5"
        summary.getProposedChangesCount() == 5
    }

    def "MapSpecEvolutionToSummary calculates agreedVersionTagCount from main branch evolution items with tags"() {
        given: "a main evolution branch with a head evolution item with a tag"
        def mainBranchName = "mainBranch"
        def mainBranchHeadEvolutionItem = new EvolutionItem().ref(mainBranchName).branchName(mainBranchName).tags(["latestAgreedTag"])
        def mainEvolutionBranch = new EvolutionBranch().branchName(mainBranchName).addEvolutionItemsItem(mainBranchHeadEvolutionItem)

        and: "1 older evolution item with tags"
        def anotherTagEvolutionItem = new EvolutionItem().ref("olderAgreedVersionTag").tags(["olderAgreedVersionTag"])
        mainEvolutionBranch = mainEvolutionBranch.addEvolutionItemsItem(anotherTagEvolutionItem)

        and: "a specEvolution with the main branch"
        def specEvolution = new SpecEvolution().main(mainEvolutionBranch)

        when: "the SpecEvolution object is mapped to a SpecEvolutionSummary object"
        def summary = specEvolutionSummaryMapper.mapSpecEvolutionToSummary(specEvolution)

        then: "the SpecEvolutionSummary has a agreedVersionTagCount of 2"
        summary.getAgreedVersionTagCount() == 2
    }
}

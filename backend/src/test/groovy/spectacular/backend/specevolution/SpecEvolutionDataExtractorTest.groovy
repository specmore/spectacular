package spectacular.backend.specevolution

import spectacular.backend.cataloguemanifest.model.MainBranchConfig
import spectacular.backend.cataloguemanifest.model.ReleaseBranchConfig
import spectacular.backend.cataloguemanifest.model.ReleaseTagConfig
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.pullrequests.PullRequestRepository
import spectacular.backend.github.refs.BranchRef
import spectacular.backend.github.refs.RefRepository
import spectacular.backend.github.refs.TagRef
import spock.lang.Specification

class SpecEvolutionDataExtractorTest extends Specification {
    def refRepository = Mock(RefRepository)
    def pullRequestRepository = Mock(PullRequestRepository)
    def specEvolutionDataExtractor = new SpecEvolutionDataExtractor(refRepository, pullRequestRepository)

    def specFilePath = "some/path/spec.yaml"
    def specRepoId = RepositoryId.createForNameWithOwner("test/repo")

    def "GetMainBranchAccordingToConfig selects the first exact matching branch to the main branch name"() {
        given: "a spec evolution config with a main branch name config set"
        def mainBranchName = "a-main-branch"
        def mainBranchConfig = new MainBranchConfig().withBranchName(mainBranchName)
        def specEvolutionConfig = new SpecEvolutionConfig().withMainBranchConfig(mainBranchConfig)

        and: "different branches matching the main branch name on the repository"
        def branches = [
                new BranchRef("a-main-branch", null, "commit1"),
                new BranchRef("a-main-branch-2", null, "commit2"),
        ]

        when: "the main branch data is extracted"
        def result = specEvolutionDataExtractor.getMainBranchAccordingToConfig(specEvolutionConfig, specRepoId, specFilePath)

        then: "branches for the repository are retrieved with the main branch name as the query filter"
        1 * refRepository.getBranchesForRepo(specRepoId, mainBranchName, specFilePath) >> branches

        and: "the pull requests for only one branch is retrieved"
        1 * pullRequestRepository.getPullRequestsForRepoAndFile(specRepoId, specFilePath, _)

        and: "the main branch returned is an exact match"
        result.get().getBranch().getName() == mainBranchName
    }

    def "GetMainBranchAccordingToConfig returns an empty result if no main branch match is found"() {
        given: "a spec evolution config with a main branch name config set that matches no branches"
        def mainBranchName = "a-main-branch"
        def mainBranchConfig = new MainBranchConfig().withBranchName(mainBranchName)
        def specEvolutionConfig = new SpecEvolutionConfig().withMainBranchConfig(mainBranchConfig)

        when: "the main branch data is extracted"
        def result = specEvolutionDataExtractor.getMainBranchAccordingToConfig(specEvolutionConfig, specRepoId, specFilePath)

        then: "branches for the repository are retrieved with the main branch name as the query filter"
        1 * refRepository.getBranchesForRepo(specRepoId, mainBranchName, specFilePath) >> []

        and: "no pull requests for are retrieved"
        0 * pullRequestRepository.getPullRequestsForRepoAndFile(specRepoId, specFilePath, _)

        and: "the main branch returned is empty"
        result.isEmpty()
    }

    def "GetRepoTagsAccordingToConfig gets all tags without a query filter if tag config is set without a prefix"() {
        given: "a spec evolution config with no tags prefix config set"
        def tagConfig = new ReleaseTagConfig()
        def specEvolutionConfig = new SpecEvolutionConfig().withReleaseTagConfig(tagConfig)

        and: "tags on the repository"
        def tags = [ new TagRef("tag-123", "commit1"), new TagRef("x-tag-456", "commit2") ]

        when: "the tag data is extracted"
        def result = specEvolutionDataExtractor.getRepoTagsAccordingToConfig(specEvolutionConfig, specRepoId)

        then: "tags for the repository are retrieved with no query filter"
        1 * refRepository.getTagsForRepo(specRepoId, null) >> tags

        and: "all the tags are returned"
        result == tags
    }

    def "GetRepoTagsAccordingToConfig get tags with a query filter if tag config is set with a prefix"() {
        given: "a spec evolution config with tags prefix config set"
        def tagPrefix = "x-"
        def tagConfig = new ReleaseTagConfig().withTagPrefix(tagPrefix)
        def specEvolutionConfig = new SpecEvolutionConfig().withReleaseTagConfig(tagConfig)

        and: "tags on the repository"
        def tags = [ new TagRef("tag-123", "commit1"), new TagRef("x-tag-456", "commit2") ]

        when: "the tag data is extracted"
        def result = specEvolutionDataExtractor.getRepoTagsAccordingToConfig(specEvolutionConfig, specRepoId)

        then: "tags for the repository are retrieved with the tag prefix query filter"
        1 * refRepository.getTagsForRepo(specRepoId, tagPrefix) >> tags
    }

    def "GetReleaseBranchesAccordingToConfig gets no release branches if release branch config is set without a prefix"() {
        given: "a spec evolution config with no release branch prefix config set"
        def releaseBranchConfig = new ReleaseBranchConfig()
        def specEvolutionConfig = new SpecEvolutionConfig().withReleaseBranchConfig(releaseBranchConfig)

        when: "the release branch data is extracted"
        def result = specEvolutionDataExtractor.getReleaseBranchesAccordingToConfig(specEvolutionConfig, specRepoId, specFilePath)

        then: "no branches for the repository are retrieved"
        0 * refRepository.getBranchesForRepo(specRepoId, _)

        and: "the result has no extracted release branches"
        result.isEmpty()
    }

    def "GetReleaseBranchesAccordingToConfig gets release branch with specified filter query if release branch config is set with a prefix"() {
        given: "a spec evolution config with a release branch prefix config set"
        def branchPrefix = "release/x-"
        def releaseBranchConfig = new ReleaseBranchConfig().withBranchPrefix(branchPrefix)
        def specEvolutionConfig = new SpecEvolutionConfig().withReleaseBranchConfig(releaseBranchConfig)

        and: "branches on the repository"
        def branches = [
                new BranchRef("release/x-branch-123", null, "commit1"),
                new BranchRef("release/x-branch-456", null, "commit2")
        ]

        when: "the release branch data is extracted"
        def result = specEvolutionDataExtractor.getReleaseBranchesAccordingToConfig(specEvolutionConfig, specRepoId, specFilePath)

        then: "branches for the repository are retrieved with the branchPrefix query filter"
        1 * refRepository.getBranchesForRepo(specRepoId, branchPrefix, specFilePath) >> branches

        and: "the pull requests for each matching branch is retrieved"
        2 * pullRequestRepository.getPullRequestsForRepoAndFile(specRepoId, specFilePath, _)

        and: "the result has the extracted release branches"
        result.size() == 2
    }
}

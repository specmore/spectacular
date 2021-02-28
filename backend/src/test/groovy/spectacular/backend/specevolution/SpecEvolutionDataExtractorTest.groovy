package spectacular.backend.specevolution

import spectacular.backend.cataloguemanifest.model.MainBranchConfig
import spectacular.backend.cataloguemanifest.model.ReleaseBranchConfig
import spectacular.backend.cataloguemanifest.model.ReleaseTagConfig
import spectacular.backend.cataloguemanifest.model.SpecEvolutionConfig
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.domain.Tag
import spectacular.backend.github.refs.BranchRef
import spectacular.backend.github.refs.RefRepository
import spock.lang.Specification

class SpecEvolutionDataExtractorTest extends Specification {
    def refRepository = Mock(RefRepository)
    def specEvolutionDataExtractor = new SpecEvolutionDataExtractor(refRepository)

    def specFilePath = "some/path/spec.yaml"

    def "GetMainBranchAccordingToConfig selects the first exact matching branch to the main branch name"() {
        given: "a spec evolution config with a main branch name config set"
        def mainBranchName = "a-main-branch"
        def mainBranchConfig = new MainBranchConfig().withBranchName(mainBranchName)
        def specEvolutionConfig = new SpecEvolutionConfig().withMainBranchConfig(mainBranchConfig)

        and: "a spec repository with branches"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def branches = [
                new BranchRef("a-main-branch", null, null),
                new BranchRef("a-main-branch-2", null, null),
        ]

        when: "the main branch data is extracted"
        def result = specEvolutionDataExtractor.getMainBranchAccordingToConfig(specEvolutionConfig, specRepoId, specFilePath)

        then: "branches for the repository are retrieved with the main branch name as the query filter"
        1 * refRepository.getBranchesForRepo(specRepoId, mainBranchName, specFilePath) >> branches

        and: "the main branch returned is an exact match"
        result.get().getName() == mainBranchName
    }

    def "GetRepoTagsAccordingToConfig gets all tags without a query filter if tag config is set without a prefix"() {
        given: "a spec evolution config with no tags prefix config set"
        def tagConfig = new ReleaseTagConfig()
        def specEvolutionConfig = new SpecEvolutionConfig().withReleaseTagConfig(tagConfig)

        and: "a spec repository with tags"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def tags = [ new Tag("tag-123"), new Tag("x-tag-456") ]

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

        and: "a spec repository with tags"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def tags = [ new Tag("tag-123"), new Tag("x-tag-456") ]

        when: "the tag data is extracted"
        def result = specEvolutionDataExtractor.getRepoTagsAccordingToConfig(specEvolutionConfig, specRepoId)

        then: "tags for the repository are retrieved with the tag prefix query filter"
        1 * refRepository.getTagsForRepo(specRepoId, tagPrefix) >> tags
    }

    def "GetReleaseBranchesAccordingToConfig gets no release branches if release branch config is set without a prefix"() {
        given: "a spec evolution config with no release branch prefix config set"
        def releaseBranchConfig = new ReleaseBranchConfig()
        def specEvolutionConfig = new SpecEvolutionConfig().withReleaseBranchConfig(releaseBranchConfig)

        and: "a spec repository"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")

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

        and: "a spec repository with branches"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def branches = [ new BranchRef("release/x-branch-123", null, null) ]

        when: "the release branch data is extracted"
        def result = specEvolutionDataExtractor.getReleaseBranchesAccordingToConfig(specEvolutionConfig, specRepoId, specFilePath)

        then: "branches for the repository are retrieved with the branchPrefix query filter"
        1 * refRepository.getBranchesForRepo(specRepoId, branchPrefix, specFilePath) >> branches

        and: "the result has the extracted release branches"
        result == branches
    }
}

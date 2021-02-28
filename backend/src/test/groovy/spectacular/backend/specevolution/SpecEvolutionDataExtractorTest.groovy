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

    def "GetEvolutionDataForSpecFile defaults the main branch name to 'main' if no main branch config is set"() {
        given: "a spec evolution config with no main branch config set"
        def specEvolutionConfig = new SpecEvolutionConfig()

        and: "a spec repository with branches"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def branches = []

        when: "the spec evolution data is extracted"
        def result = specEvolutionDataExtractor.getEvolutionDataForSpecFile(specRepoId, specFilePath, specEvolutionConfig)

        then: "branches for the repository are retrieved main as the query filter"
        1 * refRepository.getBranchesForRepo(specRepoId, "main", specFilePath) >> branches
    }

    def "GetEvolutionDataForSpecFile defaults the main branch name to 'main' if the main branch config is set without a branch name"() {
        given: "a spec evolution config with no main branch name config set"
        def mainBranchConfig = new MainBranchConfig()
        def specEvolutionConfig = new SpecEvolutionConfig().withMainBranchConfig(mainBranchConfig)

        and: "a spec repository with branches"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def branches = []

        when: "the spec evolution data is extracted"
        def result = specEvolutionDataExtractor.getEvolutionDataForSpecFile(specRepoId, specFilePath, specEvolutionConfig)

        then: "branches for the repository are retrieved main as the query filter"
        1 * refRepository.getBranchesForRepo(specRepoId, "main", specFilePath) >> branches
    }

    def "GetEvolutionDataForSpecFile gets branches using the main branch name config"() {
        given: "a spec evolution config with a main branch name config set"
        def mainBranchName = "a-main-branch"
        def mainBranchConfig = new MainBranchConfig().withBranchName(mainBranchName)
        def specEvolutionConfig = new SpecEvolutionConfig().withMainBranchConfig(mainBranchConfig)

        and: "a spec repository with branches"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def branches = []

        when: "the spec evolution data is extracted"
        def result = specEvolutionDataExtractor.getEvolutionDataForSpecFile(specRepoId, specFilePath, specEvolutionConfig)

        then: "branches for the repository are retrieved main as the query filter"
        1 * refRepository.getBranchesForRepo(specRepoId, mainBranchName, specFilePath) >> branches
    }

    def "GetEvolutionDataForSpecFile selects the first exact matching branch to the main branch name"() {
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

        when: "the spec evolution data is extracted"
        def result = specEvolutionDataExtractor.getEvolutionDataForSpecFile(specRepoId, specFilePath, specEvolutionConfig)

        then: "branches for the repository are retrieved main as the query filter"
        1 * refRepository.getBranchesForRepo(specRepoId, mainBranchName, specFilePath) >> branches

        and: "the main branch returned is an exact match"
        result.getMainBranch().get().getName() == mainBranchName
    }

    def "GetEvolutionDataForSpecFile gets all tags without a query filter if no tag config is set"() {
        given: "a spec evolution config with no tags config set"
        def specEvolutionConfig = new SpecEvolutionConfig()

        and: "a spec repository with tags"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def tags = [ new Tag("tag-123") ]

        and: "a main branch"
        refRepository.getBranchesForRepo(specRepoId, "main", specFilePath) >> [
                new BranchRef("main", null, null)
        ]

        when: "the spec evolution data is extracted"
        def result = specEvolutionDataExtractor.getEvolutionDataForSpecFile(specRepoId, specFilePath, specEvolutionConfig)

        then: "tags for the repository are retrieved with no query filter"
        1 * refRepository.getTagsForRepo(specRepoId, null) >> tags
    }

    def "GetEvolutionDataForSpecFile gets all tags without a query filter if tag config is set without a prefix"() {
        given: "a spec evolution config with no tags prefix config set"
        def tagConfig = new ReleaseTagConfig()
        def specEvolutionConfig = new SpecEvolutionConfig().withReleaseTagConfig(tagConfig)

        and: "a spec repository with tags"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def tags = [ new Tag("tag-123") ]

        and: "a main branch"
        refRepository.getBranchesForRepo(specRepoId, "main", specFilePath) >> [
                new BranchRef("main", null, null)
        ]

        when: "the spec evolution data is extracted"
        def result = specEvolutionDataExtractor.getEvolutionDataForSpecFile(specRepoId, specFilePath, specEvolutionConfig)

        then: "tags for the repository are retrieved with no query filter"
        1 * refRepository.getTagsForRepo(specRepoId, null) >> tags
    }

    def "GetEvolutionDataForSpecFile get tags with a query filter if tag config is set with a prefix"() {
        given: "a spec evolution config with tags prefix config set"
        def tagPrefix = "x-"
        def tagConfig = new ReleaseTagConfig().withTagPrefix(tagPrefix)
        def specEvolutionConfig = new SpecEvolutionConfig().withReleaseTagConfig(tagConfig)

        and: "a spec repository with tags"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def tags = [ new Tag("tag-123") ]

        and: "a main branch"
        refRepository.getBranchesForRepo(specRepoId, "main", specFilePath) >> [
                new BranchRef("main", null, null)
        ]

        when: "the spec evolution data is extracted"
        def result = specEvolutionDataExtractor.getEvolutionDataForSpecFile(specRepoId, specFilePath, specEvolutionConfig)

        then: "tags for the repository are retrieved with no query filter"
        1 * refRepository.getTagsForRepo(specRepoId, tagPrefix) >> tags

        and: "the result has the extracted tags"
        result.getTags() == tags
    }

    def "GetEvolutionDataForSpecFile gets no release branches if branch config is not set"() {
        given: "a spec evolution config with no branch config set"
        def specEvolutionConfig = new SpecEvolutionConfig()

        and: "a spec repository"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")

        and: "a main branch"
        refRepository.getBranchesForRepo(specRepoId, "main", specFilePath) >> [
                new BranchRef("main", null, null)
        ]

        when: "the spec evolution data is extracted"
        def result = specEvolutionDataExtractor.getEvolutionDataForSpecFile(specRepoId, specFilePath, specEvolutionConfig)

        then: "no branches for the repository are retrieved other than main"
        0 * refRepository.getBranchesForRepo(specRepoId, !"main")

        and: "the result has no extracted branches"
        result.getBranches().isEmpty()
    }

    def "GetEvolutionDataForSpecFile gets no branch if branch config is set without a prefix"() {
        given: "a spec evolution config with no branch prefix config set"
        def releaseBranchConfig = new ReleaseBranchConfig()
        def specEvolutionConfig = new SpecEvolutionConfig().withReleaseBranchConfig(releaseBranchConfig)

        and: "a spec repository"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")

        and: "a main branch"
        refRepository.getBranchesForRepo(specRepoId, "main", specFilePath) >> [
                new BranchRef("main", null, null)
        ]

        when: "the spec evolution data is extracted"
        def result = specEvolutionDataExtractor.getEvolutionDataForSpecFile(specRepoId, specFilePath, specEvolutionConfig)

        then: "no branches for the repository are retrieved other than main"
        0 * refRepository.getBranchesForRepo(specRepoId, !"main")

        and: "the result has no extracted branches"
        result.getBranches().isEmpty()
    }

    def "GetEvolutionDataForSpecFile gets branch with specified filter query if branch config is set with a prefix"() {
        given: "a spec evolution config with a  branch prefix config set"
        def branchPrefix = "release/x-"
        def releaseBranchConfig = new ReleaseBranchConfig().withBranchPrefix(branchPrefix)
        def specEvolutionConfig = new SpecEvolutionConfig().withReleaseBranchConfig(releaseBranchConfig)

        and: "a spec repository with branches"
        def specRepoId = RepositoryId.createForNameWithOwner("test/repo")
        def branches = [ new BranchRef("release/x-branch-123", null, null) ]

        and: "a main branch"
        refRepository.getBranchesForRepo(specRepoId, "main", specFilePath) >> [
                new BranchRef("main", null, null)
        ]

        when: "the spec evolution data is extracted"
        def result = specEvolutionDataExtractor.getEvolutionDataForSpecFile(specRepoId, specFilePath, specEvolutionConfig)

        then: "branches for the repository are retrieved with a default query filter"
        1 * refRepository.getBranchesForRepo(specRepoId, branchPrefix, specFilePath) >> branches

        and: "the result has the extracted branches"
        result.getBranches() == branches
    }
}

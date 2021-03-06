package spectacular.backend.catalogues


import spectacular.backend.api.model.Catalogue
import spectacular.backend.api.model.GetInterfaceResult
import spectacular.backend.api.model.SpecEvolutionSummary
import spectacular.backend.cataloguemanifest.catalogueentry.CatalogueEntryConfigurationResolver
import spectacular.backend.cataloguemanifest.interfaceentry.CatalogueInterfaceEntryConfigurationResolver
import spectacular.backend.cataloguemanifest.parse.CatalogueManifestContentItemParseResult
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemError
import spectacular.backend.cataloguemanifest.GetCatalogueManifestFileContentResult
import spectacular.backend.cataloguemanifest.model.CatalogueManifest
import spectacular.backend.cataloguemanifest.model.Interface
import spectacular.backend.cataloguemanifest.model.Interfaces
import spectacular.backend.common.CatalogueId
import spectacular.backend.common.CatalogueManifestId
import spectacular.backend.common.RepositoryId
import spectacular.backend.github.domain.ContentItem
import spectacular.backend.interfaces.GetInterfaceFileContentsResult
import spectacular.backend.interfaces.InterfaceService

import spock.lang.Specification

class CatalogueServiceTest extends Specification {
    def catalogueManifestYmlFilename = "spectacular-config.yml"
    def catalogueEntryConfigurationResolver = Mock(CatalogueEntryConfigurationResolver)
    def catalogueInterfaceEntryConfigurationResolver = Mock(CatalogueInterfaceEntryConfigurationResolver)
    def catalogueMapper = Mock(CatalogueMapper)
    def interfaceService = Mock(InterfaceService)
    def catalogueService = new CatalogueService(catalogueEntryConfigurationResolver, catalogueInterfaceEntryConfigurationResolver, catalogueMapper, interfaceService)

    def aUsername = "test-user"
    def anOrg = "test-org"

    def aCatalogueManifestId() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = catalogueManifestYmlFilename;
        return new CatalogueManifestId(catalogueRepo, catalogueManifestFile)
    }

    def aCatalogueId() {
        def catalogueRepo = new RepositoryId("test-owner","test-repo987")
        def catalogueManifestFile = catalogueManifestYmlFilename;
        def catalogueName = "testCatalogue1"
        return new CatalogueId(catalogueRepo, catalogueManifestFile, catalogueName)
    }

    def aCatalogueEntry(String interfaceEntryName, Interface interfaceEntry) {
        def interfaces = new Interfaces().withAdditionalProperty(interfaceEntryName, interfaceEntry)
        return new spectacular.backend.cataloguemanifest.model.Catalogue().withInterfaces(interfaces)
    }

    def aSuccessfulCatalogueEntryResult(CatalogueId catalogueId, spectacular.backend.cataloguemanifest.model.Catalogue catalogueEntry) {
        def catalogueEntryResult = Mock(CatalogueEntryConfigurationResolver.GetCatalogueEntryConfigurationResult);
        catalogueEntryResult.hasError() >> false
        catalogueEntryResult.getCatalogueEntry() >> catalogueEntry
        catalogueEntryResult.getCatalogueId() >> catalogueId
        return catalogueEntryResult;
    }

    def aCatalogueEntryResultWithError() {
        def catalogueEntryResult = Mock(CatalogueEntryConfigurationResolver.GetCatalogueEntryConfigurationResult);
        catalogueEntryResult.hasError() >> true
        catalogueEntryResult.getError() >> Mock(ConfigurationItemError)
        return catalogueEntryResult;
    }

    def aSuccessfulInterfaceEntryResult(Interface interfaceEntry) {
        def getInterfaceEntryConfigurationResult = Mock(CatalogueInterfaceEntryConfigurationResolver.GetInterfaceEntryConfigurationResult)
        getInterfaceEntryConfigurationResult.hasError() >> false
        getInterfaceEntryConfigurationResult.getInterfaceEntry() >> interfaceEntry
        return getInterfaceEntryConfigurationResult
    }

    def anInterfaceEntryResultWithError() {
        def getInterfaceEntryConfigurationResult = Mock(CatalogueInterfaceEntryConfigurationResolver.GetInterfaceEntryConfigurationResult)
        getInterfaceEntryConfigurationResult.hasError() >> true
        getInterfaceEntryConfigurationResult.getError() >> Mock(ConfigurationItemError)
        return getInterfaceEntryConfigurationResult
    }

    def "get catalogue for repository and valid user returns evolution summaries for each interface in manifest catalogue entry"() {
        given: "a location for a catalogue config"
        def catalogueId = aCatalogueId()

        and: "a catalogue config entry in the manifest file with an interface entry in it"
        def interfaceEntry = Mock(Interface)
        def interfaceEntryName = "testInterface1"
        def catalogue = aCatalogueEntry(interfaceEntryName, interfaceEntry)
        def getCatalogueEntryConfigurationResult = aSuccessfulCatalogueEntryResult(catalogueId, catalogue)
        def getInterfaceEntryConfigurationResult = aSuccessfulInterfaceEntryResult(interfaceEntry)

        and: "interface details for the interface entry with a spec evolution summary"
        def interfaceDetails = Mock(GetInterfaceResult)
        def interfaceSpecEvolutionSummary = Mock(SpecEvolutionSummary)
        interfaceDetails.getSpecEvolutionSummary() >> interfaceSpecEvolutionSummary

        and: "a catalogue API model representation of the catalogue manifest object without interface details"
        def catalogueDetails = Mock(Catalogue)

        when: "the get catalogue for user is called"
        def result = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "the catalogue entry configuration is resolved"
        1 * catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername) >> getCatalogueEntryConfigurationResult

        and: "the interface entry configuration is resolved"
        1 * catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(getCatalogueEntryConfigurationResult, interfaceEntryName) >> getInterfaceEntryConfigurationResult

        and: "the manifest catalogue entry object is mapped to an API catalogue model"
        1 * catalogueMapper.mapCatalogue(getCatalogueEntryConfigurationResult) >> catalogueDetails

        and: "the interface details are retrieved for each interface entry in the catalogue"
        1 * interfaceService.getInterfaceDetails(getInterfaceEntryConfigurationResult) >> interfaceDetails

        and: "the spec evolutions are added to the catalogue API model object"
        1 * catalogueDetails.specEvolutionSummaries([interfaceSpecEvolutionSummary]) >> catalogueDetails

        and: "the mapped catalogue API model object is returned"
        result.getCatalogueDetails() == catalogueDetails
    }

    def "get catalogue ignores interface entries in catalogue manifest with errors"() {
        given: "a location for a catalogue config"
        def catalogueId = aCatalogueId()

        and: "a catalogue config entry in the manifest file with an interface entry in it that has an error"
        def interfaceEntry = Mock(Interface)
        def interfaceEntryName = "testInterface1"
        def catalogue = aCatalogueEntry(interfaceEntryName, interfaceEntry)
        def getCatalogueEntryConfigurationResult = aSuccessfulCatalogueEntryResult(catalogueId, catalogue)
        def getInterfaceEntryConfigurationResult = anInterfaceEntryResultWithError()

        and: "a catalogue API model representation of the catalogue manifest object without interface details"
        def catalogueDetails = Mock(Catalogue)

        when: "the get catalogue for user is called"
        def result = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "the catalogue entry configuration is resolved"
        1 * catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername) >> getCatalogueEntryConfigurationResult

        and: "the interface entry configuration is resolved"
        1 * catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(getCatalogueEntryConfigurationResult, interfaceEntryName) >> getInterfaceEntryConfigurationResult

        and: "the manifest catalogue entry object is mapped to an API catalogue model"
        1 * catalogueMapper.mapCatalogue(getCatalogueEntryConfigurationResult) >> catalogueDetails

        and: "no interface details are retrieved for the null interface"
        0 * interfaceService.getInterfaceDetails(_)

        and: "the an empty list of spec evolutions are added to the catalogue API model object"
        1 * catalogueDetails.specEvolutionSummaries([]) >> catalogueDetails

        and: "the mapped catalogue API model object is returned"
        result.getCatalogueDetails() == catalogueDetails
    }

    def "get catalogue returns error result for a catalogue entry resolve error"() {
        given: "a location for a catalogue config"
        def catalogueId = aCatalogueId()

        and: "a catalogue config entry with an error"
        def getCatalogueEntryConfigurationResult = aCatalogueEntryResultWithError()

        when: "the get catalogue for user is called"
        def result = catalogueService.getCatalogueForUser(catalogueId, aUsername)

        then: "the catalogue entry configuration is resolved"
        1 * catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername) >> getCatalogueEntryConfigurationResult

        and: "no interface details are retrieved"
        0 * interfaceService.getInterfaceDetails(_)

        and: "a error result is returned with no catalogue details"
        result.getError()
        !result.getCatalogueDetails()
    }

    def "get interface details for valid repository, interface name and user successfully returns interface details"() {
        given: "a location for a catalogue config and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue config entry in the manifest file with the interface entry in it"
        def interfaceEntry = Mock(Interface)
        def catalogue = aCatalogueEntry(interfaceEntryName, interfaceEntry)
        def getCatalogueEntryConfigurationResult = aSuccessfulCatalogueEntryResult(catalogueId, catalogue)
        def getInterfaceEntryConfigurationResult = aSuccessfulInterfaceEntryResult(interfaceEntry)

        and: "interface details for the interface entry with a spec evolution summary"
        def interfaceDetails = Mock(GetInterfaceResult)
        def interfaceSpecEvolutionSummary = Mock(SpecEvolutionSummary)
        interfaceDetails.getSpecEvolutionSummary() >> interfaceSpecEvolutionSummary

        and: "a catalogue API model representation of the catalogue manifest object without interface details"
        def catalogueDetails = Mock(Catalogue)

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "the catalogue entry configuration is resolved"
        1 * catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername) >> getCatalogueEntryConfigurationResult

        and: "the interface entry configuration is resolved"
        1 * catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(getCatalogueEntryConfigurationResult, interfaceEntryName) >> getInterfaceEntryConfigurationResult

        and: "the interface details are retrieved for the interface entry in the catalogue"
        1 * interfaceService.getInterfaceDetails(getInterfaceEntryConfigurationResult) >> interfaceDetails

        and: "the manifest catalogue entry object is mapped to an API catalogue model"
        1 * catalogueMapper.mapCatalogue(getCatalogueEntryConfigurationResult) >> catalogueDetails

        and: "the catalogue API object is added to the interface details"
        1 * interfaceDetails.catalogue(catalogueDetails) >> interfaceDetails

        and: "the mapped catalogue API model object is returned"
        result.getInterfaceResult == interfaceDetails
    }

    def "get interface details returns error result for catalogue entry config that resolves with error"() {
        given: "a catalogue id and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue config entry with an error"
        def getCatalogueEntryConfigurationResult = aCatalogueEntryResultWithError()

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "the catalogue entry configuration is resolved"
        1 * catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername) >> getCatalogueEntryConfigurationResult

        and: "no interface entry configuration is resolved"
        0 * catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(_, _)

        and: "no interface details are retrieved"
        0 * interfaceService.getInterfaceDetails(_)

        and: "a error result is returned with no interface details"
        result.getError()
        !result.getGetInterfaceResult()
    }

    def "get interface details returns error result for interface entry config that resolves with error"() {
        given: "a catalogue id and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue config entry in the manifest file with an interface entry in it that has an error"
        def interfaceEntry = Mock(Interface)
        def catalogue = aCatalogueEntry(interfaceEntryName, interfaceEntry)
        def getCatalogueEntryConfigurationResult = aSuccessfulCatalogueEntryResult(catalogueId, catalogue)
        def getInterfaceEntryConfigurationResult = anInterfaceEntryResultWithError()

        when: "the get interface details for user is called"
        def result = catalogueService.getInterfaceDetails(catalogueId, interfaceEntryName, aUsername)

        then: "the catalogue entry configuration is resolved"
        1 * catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername) >> getCatalogueEntryConfigurationResult

        and: "the interface entry configuration is resolved"
        1 * catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(getCatalogueEntryConfigurationResult, interfaceEntryName) >> getInterfaceEntryConfigurationResult

        and: "no interface details are retrieved"
        0 * interfaceService.getInterfaceDetails(_)

        and: "a error result is returned with no interface details"
        result.getError()
        !result.getGetInterfaceResult()
    }

    def "get interface file contents for valid repository, interface name and user successfully returns file contents"() {
        given: "a catalogue id, interface entry name and ref"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"
        def ref = 'branch1'

        and: "a catalogue config entry in the manifest file with the interface entry in it"
        def interfaceEntry = Mock(Interface)
        def catalogue = aCatalogueEntry(interfaceEntryName, interfaceEntry)
        def getCatalogueEntryConfigurationResult = aSuccessfulCatalogueEntryResult(catalogueId, catalogue)
        def getInterfaceEntryConfigurationResult = aSuccessfulInterfaceEntryResult(interfaceEntry)

        and: "a file contents result for the interface entry"
        def getInterfaceFileContentsResult = Mock(GetInterfaceFileContentsResult)

        when: "the get interface file contents for ref and user is called"
        def result = catalogueService.getInterfaceFileContents(catalogueId, interfaceEntryName, ref, aUsername)

        then: "the catalogue entry configuration is resolved"
        1 * catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername) >> getCatalogueEntryConfigurationResult

        and: "the interface entry configuration is resolved"
        1 * catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(getCatalogueEntryConfigurationResult, interfaceEntryName) >> getInterfaceEntryConfigurationResult

        and: "the interface file contents are retrieved for the interface entry in the catalogue"
        1 * interfaceService.getInterfaceFileContents(catalogueId, interfaceEntry, ref) >> getInterfaceFileContentsResult

        and: "the interface file contents result is returned"
        result == getInterfaceFileContentsResult
    }

    def "get interface file contents returns error result for catalogue entry config that resolves with error"() {
        given: "a catalogue id, interface entry name and ref"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"
        def ref = 'branch1'

        and: "a catalogue config entry with an error"
        def getCatalogueEntryConfigurationResult = aCatalogueEntryResultWithError()

        when: "the get interface file contents for ref and user is called"
        def result = catalogueService.getInterfaceFileContents(catalogueId, interfaceEntryName, ref, aUsername)

        then: "the catalogue entry configuration is resolved"
        1 * catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername) >> getCatalogueEntryConfigurationResult

        and: "no interface entry configuration is resolved"
        0 * catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(_, _)

        and: "no interface file contents is retrieved"
        0 * interfaceService.getInterfaceFileContents(_, _, _)

        and: "a error result is returned"
        result.hasError()
    }

    def "get interface file contents returns error result for interface entry config that resolves with error"() {
        given: "a catalogue id, interface entry name and ref"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"
        def ref = 'branch1'

        and: "a catalogue config entry in the manifest file with an interface entry in it that has an error"
        def interfaceEntry = Mock(Interface)
        def catalogue = aCatalogueEntry(interfaceEntryName, interfaceEntry)
        def getCatalogueEntryConfigurationResult = aSuccessfulCatalogueEntryResult(catalogueId, catalogue)
        def getInterfaceEntryConfigurationResult = anInterfaceEntryResultWithError()

        when: "the get interface file contents for ref and user is called"
        def result = catalogueService.getInterfaceFileContents(catalogueId, interfaceEntryName, ref, aUsername)

        then: "the catalogue entry configuration is resolved"
        1 * catalogueEntryConfigurationResolver.getCatalogueEntryConfiguration(catalogueId, aUsername) >> getCatalogueEntryConfigurationResult

        and: "the interface entry configuration is resolved"
        1 * catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(getCatalogueEntryConfigurationResult, interfaceEntryName) >> getInterfaceEntryConfigurationResult

        and: "no interface file contents is retrieved"
        0 * interfaceService.getInterfaceFileContents(_, _, _)

        and: "a error result is returned"
        result.hasError()
    }

    def "find catalogues for valid user and org returns catalogues for each catalogue manifest file found with actual valid contents"() {
        given: "user and org"
        def username = aUsername
        def org = anOrg

        and: "a catalogue entry found in a manifest file"
        def catalogueId = aCatalogueId()
        def catalogue = Mock(spectacular.backend.cataloguemanifest.model.Catalogue)
        def getCatalogueEntryConfigurationResult = aSuccessfulCatalogueEntryResult(catalogueId, catalogue)

        and: "mapped catalogue API models for each catalogue entry in the manifest object"
        def mappedCatalogues = Mock(Catalogue)

        when: "the find catalogues for user and org is called"
        def result = catalogueService.findCataloguesForOrgAndUser(org, username)

        then: "catalogue entries configuration are found and resolved"
        1 * catalogueEntryConfigurationResolver.findCataloguesForOrgAndUser(org, username) >> [getCatalogueEntryConfigurationResult]

        and: "the manifest catalogue entry object is mapped to an API catalogue model"
        1 * catalogueMapper.mapCatalogue(getCatalogueEntryConfigurationResult) >> mappedCatalogues

        and: "the catalogues returned contain the mapped entries"
        result.size() == 1
        result.first() == mappedCatalogues
    }
}

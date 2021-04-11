package spectacular.backend.cataloguemanifest

import spectacular.backend.cataloguemanifest.catalogueentry.CatalogueEntryConfigurationResolver
import spectacular.backend.cataloguemanifest.configurationitem.ConfigurationItemErrorType
import spectacular.backend.cataloguemanifest.interfaceentry.CatalogueInterfaceEntryConfigurationResolver
import spectacular.backend.cataloguemanifest.model.Catalogue
import spectacular.backend.cataloguemanifest.model.Interface
import spectacular.backend.cataloguemanifest.model.Interfaces
import spectacular.backend.cataloguemanifest.model.SpecFileLocation
import spectacular.backend.common.CatalogueId
import spectacular.backend.common.CatalogueManifestId
import spectacular.backend.common.RepositoryId
import spock.lang.Specification

class CatalogueInterfaceEntryConfigurationResolverTest extends Specification {
    def catalogueInterfaceEntryConfigurationResolver = new CatalogueInterfaceEntryConfigurationResolver()

    def catalogueManifestYmlFilename = "spectacular-config.yml"

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

    def aCatalogueManifest(String interfaceEntryName, Interface interfaceEntry) {
        def interfaces = new Interfaces().withAdditionalProperty(interfaceEntryName, interfaceEntry)
        return new Catalogue().withInterfaces(interfaces)
    }

    def aSuccessfulCatalogueEntryResult(CatalogueId catalogueId, Catalogue catalogueEntry) {
        def catalogueEntryResult = Mock(CatalogueEntryConfigurationResolver.GetCatalogueEntryConfigurationResult);
        catalogueEntryResult.getCatalogueEntry() >> catalogueEntry
        catalogueEntryResult.getCatalogueId() >> catalogueId
        return catalogueEntryResult;
    }

    def "GetCatalogueInterfaceEntryConfiguration for valid interface config and user successfully returns interface entry"() {
        given: "a location for a catalogue config and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue config entry in the manifest file with the interface entry in it"
        def interfaceEntry = new Interface()
        def catalogue = aCatalogueManifest(interfaceEntryName, interfaceEntry)
        def getCatalogueEntryConfigurationResult = aSuccessfulCatalogueEntryResult(catalogueId, catalogue)

        and: "a spec file location on the interface entry"
        def specFileLocation = new SpecFileLocation()
        interfaceEntry.setSpecFile(specFileLocation)

        when: "the interface configuration is retrieved"
        def result = catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(getCatalogueEntryConfigurationResult, interfaceEntryName)

        then: "the result has no error"
        !result.getError()

        and: "the interface entry is returned"
        result.getInterfaceName() == interfaceEntryName
        result.getInterfaceEntry() == interfaceEntry
    }

    def "GetCatalogueInterfaceEntryConfiguration for catalogue entry with no interfaces returns not found error result"() {
        given: "a location for a catalogue config and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue config entry in the manifest file no interface entries in it"
        def catalogue = new Catalogue()
        def getCatalogueEntryConfigurationResult = aSuccessfulCatalogueEntryResult(catalogueId, catalogue)

        when: "the interface configuration is retrieved"
        def result = catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(getCatalogueEntryConfigurationResult, interfaceEntryName)

        then: "the result has a not found error"
        result.getError().getType() == ConfigurationItemErrorType.NOT_FOUND

        and: "no the interface entry is returned"
        !result.getInterfaceEntry()
    }

    def "GetCatalogueInterfaceEntryConfiguration for catalogue entry with missing interface entry returns not found error result"() {
        given: "a location for a catalogue config and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue config entry in the manifest file with other interface entries in it"
        def otherInterfaceEntry = new Interface()
        def catalogue = aCatalogueManifest("otherInterfaceName", otherInterfaceEntry)
        def getCatalogueEntryConfigurationResult = aSuccessfulCatalogueEntryResult(catalogueId, catalogue)

        when: "the interface configuration is retrieved"
        def result = catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(getCatalogueEntryConfigurationResult, interfaceEntryName)

        then: "the result has a not found error"
        result.getError().getType() == ConfigurationItemErrorType.NOT_FOUND

        and: "no the interface entry is returned"
        !result.getInterfaceEntry()
    }

    def "GetCatalogueInterfaceEntryConfiguration for null interface entry returns config error result"() {
        given: "a location for a catalogue config and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue config entry in the manifest file with a null interface entry in it"
        def catalogue = aCatalogueManifest(interfaceEntryName, null)
        def getCatalogueEntryConfigurationResult = aSuccessfulCatalogueEntryResult(catalogueId, catalogue)

        when: "the interface configuration is retrieved"
        def result = catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(getCatalogueEntryConfigurationResult, interfaceEntryName)

        then: "the result has a config error"
        result.getError().getType() == ConfigurationItemErrorType.CONFIG_ERROR

        and: "no the interface entry is returned"
        !result.getInterfaceEntry()
    }

    def "GetCatalogueInterfaceEntryConfiguration for interface entry with missing spec file location config returns config error result"() {
        given: "a location for a catalogue config and interface entry name"
        def catalogueId = aCatalogueId()
        def interfaceEntryName = "testInterface1"

        and: "a catalogue config entry in the manifest file with the interface entry in it"
        def interfaceEntry = new Interface()
        def catalogue = aCatalogueManifest(interfaceEntryName, interfaceEntry)
        def getCatalogueEntryConfigurationResult = aSuccessfulCatalogueEntryResult(catalogueId, catalogue)

        when: "the interface configuration is retrieved"
        def result = catalogueInterfaceEntryConfigurationResolver.getCatalogueInterfaceEntryConfiguration(getCatalogueEntryConfigurationResult, interfaceEntryName)

        then: "the result has a config error"
        result.getError().getType() == ConfigurationItemErrorType.CONFIG_ERROR

        and: "no the interface entry is returned"
        !result.getInterfaceEntry()
    }
}

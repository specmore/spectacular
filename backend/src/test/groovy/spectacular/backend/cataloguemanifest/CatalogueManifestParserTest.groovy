package spectacular.backend.cataloguemanifest

import spock.lang.Specification

class CatalogueManifestParserTest extends Specification {
    def "FindAndParseCatalogueInManifestFileContents for valid catalogue manifest"() {
        given: "a valid catalogue manifest YAML content in the manifest file"
        def yamlManifest = "spectacular: '0.1'\n" +
                "catalogues:\n" +
                "  testCatalogue1:\n" +
                "    title: \"Test Catalogue 1\"\n" +
                "    description: \"Specifications for all the interfaces across system X.\"\n" +
                "    interfaces:\n" +
                "      interface1:\n" +
                "        specFile:\n" +
                "          filePath: \"specs/example-template.yaml\"\n" +
                "      interface2:\n" +
                "        specFile:\n" +
                "          filePath: \"specs/example-spec.yaml\"\n" +
                "          repo: \"test-owner2/specs-test2\""

        when: "finding and parsing a specific catalogue name in the manifest YAML"
        def result = CatalogueManifestParser.findAndParseCatalogueInManifestFileContents(yamlManifest, "testCatalogue1");

        then: "the catalogue is found"
        result.catalogue
        result.catalogue.getTitle() == "Test Catalogue 1"

        and: "there is no error"
        !result.error
    }

    def "FindAndParseCatalogueInManifestFileContents for catalogue manifest with no catalogues field"() {
        given: "a valid catalogue manifest YAML content in the manifest file"
        def yamlManifest = "spectacular: '0.1'"

        when: "finding and parsing a specific catalogue name in the manifest YAML"
        def result = CatalogueManifestParser.findAndParseCatalogueInManifestFileContents(yamlManifest, "testCatalogue1");

        then: "the catalogue is not found"
        !result.catalogue

        and: "there is an error"
        result.error == "Unable to find 'catalogues' root node catalogue manifest yaml file."
    }

    def "FindAndParseCatalogueInManifestFileContents for catalogue manifest with missing catalogue"() {
        given: "a valid catalogue manifest YAML content in the manifest file"
        def yamlManifest = "spectacular: '0.1'\n" +
                "catalogues:\n" +
                "  testCatalogue1:\n" +
                "    title: \"Test Catalogue 1\"\n" +
                "    description: \"Specifications for all the interfaces across system X.\"\n" +
                "    interfaces:\n" +
                "      interface1:\n" +
                "        specFile:\n" +
                "          filePath: \"specs/example-template.yaml\"\n" +
                "      interface2:\n" +
                "        specFile:\n" +
                "          filePath: \"specs/example-spec.yaml\"\n" +
                "          repo: \"test-owner2/specs-test2\""

        when: "finding and parsing a specific catalogue name not in the manifest YAML"
        def result = CatalogueManifestParser.findAndParseCatalogueInManifestFileContents(yamlManifest, "anotherCatalogue");

        then: "the catalogue is not found"
        !result.catalogue

        and: "there is an error"
        result.error == "Unable to find catalogue node 'anotherCatalogue' in 'catalogues' node catalogue manifest yaml file."
    }
}

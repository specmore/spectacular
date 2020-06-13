package spectacular.backend.cataloguemanifest

import spock.lang.Specification

class CatalogueManifestParserTest extends Specification {
    def catalogueManifestParser = new CatalogueManifestParser()

    def aValidYamlCatalogueManifestFileContents = "spectacular: '0.1'\n" +
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

    def aCatalogueManifestWithMissingVersionHeader = "catalogues:\n" +
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

    def aCatalogueManifestWithInvalidFields = "spectacular: '0.1'\n" +
            "catalogues:\n" +
            "  testCatalogue1:\n" +
            "    randomField: 12345"

    def aCatalogueManifestWithMissingCatalogueTitle = "spectacular: '0.1'\n" +
            "catalogues:\n" +
            "  testCatalogue1:\n" +
            "    description: \"Specifications for all the interfaces across system X.\"\n" +
            "    interfaces:\n" +
            "      interface1:\n" +
            "        specFile:\n" +
            "          filePath: \"specs/example-template.yaml\"\n" +
            "      interface2:\n" +
            "        specFile:\n" +
            "          filePath: \"specs/example-spec.yaml\"\n" +
            "          repo: \"test-owner2/specs-test2\""

    def "parseManifestFileContents returns parse error for invalid catalogue manifest with missing version header"() {
        given: "an invalid catalogue manifest with missing version header"
        def yamlManifest = aCatalogueManifestWithMissingVersionHeader

        when: "parsing the manifest file contents"
        def result = catalogueManifestParser.parseManifestFileContents(yamlManifest);

        then: "no catalogue manifest is returned"
        !result.catalogueManifest

        and: "there is a parse error"
        result.error == "The following validation errors were found with the catalogue manifest file: spectacular must not be null"
    }

    def "FindAndParseCatalogueInManifestFileContents for valid catalogue manifest"() {
        given: "a valid catalogue manifest YAML content in the manifest file"
        def yamlManifest = aValidYamlCatalogueManifestFileContents

        when: "finding and parsing a specific catalogue name in the manifest YAML"
        def result = catalogueManifestParser.findAndParseCatalogueInManifestFileContents(yamlManifest, "testCatalogue1");

        then: "the catalogue is found"
        result.catalogue
        result.catalogue.getTitle() == "Test Catalogue 1"

        and: "there is no error"
        !result.error
    }

    def "FindAndParseCatalogueInManifestFileContents returns parse error for unknown fields in catalogue manifest"() {
        given: "an invalid catalogue manifest YAML content"
        def yamlManifest = aCatalogueManifestWithInvalidFields

        when: "finding and parsing a specific catalogue name in the manifest YAML"
        def result = catalogueManifestParser.findAndParseCatalogueInManifestFileContents(yamlManifest, "testCatalogue1");

        then: "the catalogue is not found"
        !result.catalogue

        and: "there is a parse error"
        result.error == "A mapping error occurred while parsing the catalogue manifest yaml file. The following field is invalid: spectacular.backend.cataloguemanifest.model.Catalogue[\"randomField\"]"
    }

    def "FindAndParseCatalogueInManifestFileContents returns parse error for missing required fields in catalogue manifest"() {
        given: "an invalid catalogue manifest YAML content"
        def yamlManifest = aCatalogueManifestWithMissingCatalogueTitle

        when: "finding and parsing a specific catalogue name in the manifest YAML"
        def result = catalogueManifestParser.findAndParseCatalogueInManifestFileContents(yamlManifest, "testCatalogue1");

        then: "the catalogue is not found"
        !result.catalogue

        and: "there is a parse error"
        result.error == "The following validation errors were found with catalogue entry 'testCatalogue1': title must not be null"
    }

    def "FindAndParseCatalogueInManifestFileContents for catalogue manifest with no catalogues field"() {
        given: "an empty manifest file"
        def yamlManifest = "spectacular: '0.1'"

        when: "finding and parsing a specific catalogue name in the manifest YAML"
        def result = catalogueManifestParser.findAndParseCatalogueInManifestFileContents(yamlManifest, "testCatalogue1");

        then: "the catalogue is not found"
        !result.catalogue

        and: "there is no error"
        !result.error
    }

    def "FindAndParseCatalogueInManifestFileContents for catalogue manifest with missing catalogue"() {
        given: "a valid catalogue manifest YAML content in the manifest file"
        def yamlManifest = aValidYamlCatalogueManifestFileContents

        when: "finding and parsing a specific catalogue name not in the manifest YAML"
        def result = catalogueManifestParser.findAndParseCatalogueInManifestFileContents(yamlManifest, "anotherCatalogue");

        then: "the catalogue is not found"
        !result.catalogue

        and: "there is no error"
        !result.error
    }
}

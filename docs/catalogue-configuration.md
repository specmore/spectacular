# Catalogue Configuration

- [Catalogue Configuration](#catalogue-configuration)
  - [Catalogue Manifest Files](#catalogue-manifest-files)
    - [Name and Location](#name-and-location)
    - [Version](#version)
    - [Format and Schemas](#format-and-schemas)
      - [CatalogueManifest Object](#cataloguemanifest-object)
      - [Catalogue Object](#catalogue-object)
      - [Interface Object](#interface-object)
      - [SpecFileLocation Object](#specfilelocation-object)

## Catalogue Manifest Files
Spectacular is configured to find and display all your interface specification files using of Catalogue Manifest Files.

### Name and Location
Catalogue Manifest Files should be name `spectacular-config.yml` or `spectacular-config.yaml` and be place in the root of any Git repository that Spectacular has access to (i.e. been installed on). The `.yml` file takes precedence.

### Version
As new features are added to the Spectacular tooling that require changes to the Catalogue Manifest files, the Catalogue Manifest File specification is versioned appropriately using [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html) (semver). The `major`.`minor` portion of the semver (for example `2.0`) SHALL designate the Spectacular tooling feature set.

### Format and Schemas
Catalogue Manifest Files capture the Catalogue Configuration using the objects described below and are written using [YAML](https://yaml.org/). The root of the file is the [CatalogueManifest Object](#cataloguemanifest-object).

#### CatalogueManifest Object
This is the root object of the Catalogue Manifest File.

| Field Name | Type | Description |
| ---------- | ---- | ----------- |
| spectacular | `string` | **REQUIRED**. This string MUST be the `major`.`minor` portion of  [semantic version number](https://semver.org/spec/v2.0.0.html) of the [Catalogue Manifest version](#version) that the Catalogue Manifest file being created uses. |
| catalogues | Map[`string`, [Catalogue Object](#catalogue-object)] | A map containing the catalogues described in this manifest file. |

#### Catalogue Object
This object represents a Catalogue of interfaces specification files and the metadata associated with it.

| Field Name | Type | Description |
| ---------- | ---- | ----------- |
| title | `string` | **REQUIRED**. A short title of the catalogue. |
| interfaces | [[Interface Object](#interface-object)] | A map of the interfaces that this catalogue contains. |

#### Interface Object
This object represents an interface and the location of the specification file that describes it.

| Field Name | Type | Description |
| ---------- | ---- | ----------- |
| specFile | [SpecFileLocation Object](#specfilelocation-object) | **REQUIRED**. The location of the interface specification file (i.e. the OpenAPI `.yaml` file) for this interface. |

#### SpecFileLocation Object
This object represents the location of an interface specification file.

| Field Name | Type | Description |
| ---------- | ---- | ----------- |
| filePath | `string` | **REQUIRED**. The file path to the interface specification file (i.e. the OpenAPI `.yaml` file) relative to the root of the repository it is stored in. |
| repo | `string` | This is an optional field. By default, it is assumed the interface specification file is stored in the same repository as the catalogue manifest file. Use the `repo` field to specify an alternative repository location using the full `owner/name` repository identifier. |

# Catalogue Configuration

- [Catalogue Configuration](#catalogue-configuration)
  - [Catalogue Manifest Files](#catalogue-manifest-files)
    - [Name and Location](#name-and-location)
    - [Version](#version)
    - [Format and Schemas](#format-and-schemas)

## Catalogue Manifest Files
Spectacular is configured to find and display all your interface specification files using of Catalogue Manifest Files.

### Name and Location
Catalogue Manifest Files should be name `spectacular-config.yml` or `spectacular-config.yaml` and be place in the root of any Git repository that Spectacular has access to (i.e. been installed on). The `.yml` file takes precedence.

### Version
As new features are added to the Spectacular tooling that require changes to the Catalogue Manifest files, the Catalogue Manifest File specification is versioned appropriately using [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html) (semver). The `major`.`minor` portion of the semver (for example `2.0`) SHALL designate the Spectacular tooling feature set.

### Format and Schemas
Catalogue Manifest Files must be written using [YAML](https://yaml.org/) and follow the JSON/YAML schema described in the [catalogue-manifest.yaml schema file](../backend/specs/catalogue-manifest.yaml).

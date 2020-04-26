# Spectacular Domain Model
## Domain Overview
Spectacular tries to solve the following problems:
- Organising interface specification files into different Catalogues
- Capturing all the different versions of a interface specification and categorising them against what current stage of the evolutionary life cycle they are in

## Domain Model
The following domain model is Spectacular's attempt to model the problem space described above.
![domain model diagram](diagrams/domain-model.png)

### Catalogue
The `Catalogue` object represents a collection of interface specifications.
It is uniquely identified by the repository the manifest file that describes this catalogue was found in.

A reference is kept to the `CatalgoueManifest` object that was parsed to create the `Catalogue` instance, as well as any parsing error that was encountered.

For each interface specification in the catalogue a current snapshot of the different versions of the interface specification are captured together as a `SpecLog` item.

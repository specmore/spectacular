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

A reference is kept to the `CatalgoueManifest` object that was parsed to create the `Catalogue` instance, as well as any parsing error that were encountered.

For each interface specification in the catalogue a current snapshot of the different versions (or "evolution") of the interface specification are captured together as a `SpecLog` item.

### SpecLog
The `SpecLog` object attempts to represent a current snapshot of an interface's evolution. The different versions of the interface's specification file are taken from the VCS (git) history for the file and categories as follows:
- The "latest agreed" version is taken from the spec file's contents on the `master` branch
- Any "proposed changes" versions are taken from the spec file's contents on any branch that is currently requested to be merged into the `master` branch through an open Pull Request at present.

Each version of the spec file referenced is represented using a `SpecItem` object, except for versions that are for "proposed changes" which are wrapped in a `ProposedSpecChange` object.

### ProposedSpecChange
Proposed changes to an interface specification are an important part of the interface evolutionary life cycle. To facilitate the review, discussion and agreement to accept a proposal a "Pull Request" to merge changes into the `master` branch is used.

The `ProposedSpecChange` object is used to capture the details of a proposal using the following properties:
- A reference to an open `PullRequest` object where the changes to the interface specification are being discussed and agreed upon.
- A reference to a `SpecItem` object that captures the new proposed version of the specification file.

## PullRequest
The `PullRequest` object captures useful details about the Pull Request (PR) used to propose changes to an interface specification, such as:
- The unique PR number
- The title of the PR
- The source branch the PR is trying to merge into the `master` branch
- Any labels that have been associated to the PR
- The URL link to the PR in the VCS hosting tool.

## SpecItem
The `SpecItem` object represent a specific version of an interface specification file and the OpenAPI details described in its contents.

The specific version of the file can be uniquely identified as a combination of the following:
- The repository identifier and the file path where the specification file can be found
- The SHA identifier or, in most cases, a friendly git ref name to the git commit this version of the specification file can be found at in the commit history of the git repository.

The OpenAPI details described in the file's contents is captured as an `OpenApiSpecParseResults` object.

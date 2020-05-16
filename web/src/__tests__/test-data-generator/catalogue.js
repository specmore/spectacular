import Repository from './repository';

const generateCatalogueId = ({ repository = Repository.generateRepository() } = {}) => {
  const path = 'spectacular-config.yml';
  const encoded = btoa(`${repository.nameWithOwner}/${path}`);

  return {
    repository,
    path: 'spectacular-config.yml',
    encoded,
  };
};

const generateCatalogueManifest = ({ hasNoSpecFiles = false } = {}) => {
  const specFiles = hasNoSpecFiles ? null : [
    {
      repo: null,
      'file-path': 'specs/example-template.yaml',
    },
    {
      repo: {
        owner: 'test-owner', name: 'specs-test2', htmlUrl: null, nameWithOwner: 'test-owner/specs-test2',
      },
      'file-path': 'specs/example-spec.yaml',
    },
  ];

  return {
    name: 'Test Catalogue 1',
    description: 'Specifications for all the interfaces in the across the system X.',
    'spec-files': specFiles,
  };
};

const generateValidCatalogue = ({
  id = generateCatalogueId(),
  catalogueManifest = generateCatalogueManifest(),
  specLogs = null,
} = {}) => {
  const catalogue = {
    id,
    catalogueManifest,
    specLogs,
    error: null,
  };

  return catalogue;
};

const generateCatalogueWithError = (errorMessage) => {
  const catalogue = generateValidCatalogue({ catalogueManifest: null, specLogs: null });
  catalogue.error = errorMessage;

  return catalogue;
};

export default {
  generateCatalogueId,
  generateCatalogueManifest,
  generateValidCatalogue,
  generateCatalogueWithError,
};

const generateCatalogueId = ({ name = 'specs-test', owner = 'test-owner' } = {}) => {
  const nameWithOwner = `${owner}/${name}`;
  const path = 'spectacular-config.yml';
  const encoded = btoa(`${owner}/${name}/${path}`);

  return {
    repository: {
      owner,
      name,
      htmlUrl: `https://github.com/${owner}/${name}`,
      nameWithOwner,
    },
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

const generateValidCatalogue = ({ id = generateCatalogueId(), catalogueManifest = generateCatalogueManifest() } = {}) => {
  const catalogue = {
    id,
    catalogueManifest,
    specLogs: null,
    error: null,
  };

  return catalogue;
};

const generateCatalogueWithError = (errorMessage) => {
  const id = generateCatalogueId();

  const catalogue = {
    id,
    catalogueManifest: null,
    specLogs: null,
    error: errorMessage,
  };

  return catalogue;
};

export default {
  Catalogue: {
    generateCatalogueId,
    generateCatalogueManifest,
    generateValidCatalogue,
    generateCatalogueWithError,
  },
};

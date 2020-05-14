const catalogueId = {
  repository: {
    owner: 'test-owner',
    name: 'specs-test',
    htmlUrl: 'https://github.com/test-owner/specs-test',
    nameWithOwner: 'test-owner/specs-test',
  },
  path: 'spectacular-config.yml',
  encoded: 'cGJ1cmxzL3NwZWNzLXRlc3Qvc3BlY3RhY3VsYXItY29uZmlnLnltbA==',
};

const catalogueManifest = {
  name: 'Test Catalogue 1',
  description: 'Specifications for all the interfaces in the across the system X.',
  'spec-files': [
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
  ],
}

const generateValidCatalogue = () => {
  const catalogue = {
    id: catalogueId,
    catalogueManifest,
    specLogs: null,
    error: null,
  };

  return catalogue;
};

const generateCatalogueWithError = (errorMessage) => {
  const catalogue = {
    id: catalogueId,
    catalogueManifest: null,
    specLogs: null,
    error: errorMessage,
  };

  return catalogue;
};

export default {
  Catalogue: {
    generateValidCatalogue,
    generateCatalogueWithError,
  },
};

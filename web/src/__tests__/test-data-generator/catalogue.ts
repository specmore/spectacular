import { Catalogue, SpecEvolutionSummary } from '../../backend-api-client';

interface GenerateCatalogueParameters {
  fullPath?: string;
  name?: string;
  specEvolutionSummaries?: SpecEvolutionSummary[];
  topics?: string[];
}

const generateCatalogue = ({
  fullPath = 'test-owner/specs-test/spectacular-config.yml',
  name = 'testCatalogue1',
  specEvolutionSummaries,
  topics = ['test-topic-1'],
}: GenerateCatalogueParameters = {}): Catalogue => {
  const catalogue = {
    fullPath,
    name,
    encodedId: btoa(`${fullPath}/${name}`),
    title: 'Test Catalogue 1',
    description: 'Specifications for all the interfaces in the across the system X.',
    interfaceCount: 2,
    specEvolutionSummaries,
    topics,
  };

  return catalogue;
};

const generateCatalogueWithError = (parseError: string): Catalogue => {
  const catalogue = {
    fullPath: 'test-owner/specs-test/error-config.yml',
    name: 'error',
    encodedId: btoa('test-owner/specs-test/error-config.yml/error'),
    parseError,
  };

  return catalogue;
};

export default {
  generateCatalogue,
  generateCatalogueWithError,
};

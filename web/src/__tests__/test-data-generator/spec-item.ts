import { SpecItem } from '../../backend-api-client';

interface GenerateSpecItemParameters {
  repository?: string;
  filePath?: string;
  ref?: string;
  specFileTitle?: string;
  specFileVersion?: string;
}

const generateSpecItem = ({
  ref = 'master',
  filePath = 'specs/example-template.yaml',
  repository = 'test-owner/specs-test',
  specFileTitle = 'An empty API spec',
  specFileVersion = '0.1.0',
}: GenerateSpecItemParameters = {}): SpecItem => {
  const fullPath = `${repository}/${filePath}`;
  const id = `${repository}/${ref}/${filePath}`;

  return {
    id,
    fullPath,
    htmlUrl: 'test-url',
    ref,
    sha: 'e6f9f693f080018158d1dd0394c53ab354a8be42',
    lastModified: '2020-02-18T22:33:51Z',
    parseResult: { openApiSpec: { title: specFileTitle, version: specFileVersion, operations: [] }, errors: [] },
  };
};

const generateSpecItemWithError = (errorMessage: string): SpecItem => {
  const specItem = generateSpecItem();
  specItem.parseResult.openApiSpec = null;
  specItem.parseResult.errors = [errorMessage];

  return specItem;
};

export default {
  generateSpecItem,
  generateSpecItemWithError,
};

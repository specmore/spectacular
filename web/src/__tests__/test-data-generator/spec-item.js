import Repository from './repository';

const generateSpecItem = ({
  ref = 'master',
  filePath = 'specs/example-template.yaml',
  repository = Repository.generateRepository(),
} = {}) => {
  const id = `${repository.nameWithOwner}/${ref}/${filePath}`;

  return {
    id,
    repository,
    filePath,
    ref,
    sha: 'e6f9f693f080018158d1dd0394c53ab354a8be42',
    lastModified: '2020-02-18T22:33:51Z',
    parseResult: { openApiSpec: { title: 'An empty API spec', version: '0.1.0', operations: [] }, errors: [] },
  };
};

export default {
  generateSpecItem,
};

import Repository from './repository';
import SpecItem from './spec-item';

const generateSpecLog = ({
  filePath = 'specs/example-template.yaml',
  repository = Repository.generateRepository(),
  latestAgreed = SpecItem.generateSpecItem({ filePath, repository }),
} = {}) => {
  const id = `${repository.nameWithOwner}/${filePath}`;
  return {
    id,
    latestAgreed,
    proposedChanges: [],
  };
};

export default {
  generateSpecLog,
};

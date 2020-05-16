import SpecItem from './spec-item';

const generateSpecLog = ({
  latestAgreed = SpecItem.generateSpecItem(),
  proposedChanges = [],
} = {}) => {
  const id = `${latestAgreed.repository.nameWithOwner}/${latestAgreed.filePath}`;
  return {
    id,
    latestAgreed,
    proposedChanges,
  };
};

export default {
  generateSpecLog,
};

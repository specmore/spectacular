import PullRequest from './pull-request';
import SpecItem from './spec-item';

const generateProposedChange = ({
  pullRequest = PullRequest.generatePullRequest(),
  filePath = 'specs/example-template.yaml',
  specItem = SpecItem.generateSpecItem({ repository: pullRequest.repository, ref: pullRequest.branchName, filePath }),
} = {}) => {
  const id = pullRequest.number;

  return {
    id,
    pullRequest,
    specItem,
  };
};

export default {
  generateProposedChange,
};

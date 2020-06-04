import { ChangeProposal } from '../../__generated__/backend-api-client';

import PullRequest from './pull-request';
import SpecItem from './spec-item';

const generateChangeProposal = ({
  number = 99,
  pullRequest = PullRequest.generatePullRequest({ number }),
  specItem = SpecItem.generateSpecItem(),
} = {}): ChangeProposal => {
  const id = pullRequest.number;

  return {
    id,
    pullRequest,
    specItem,
  };
};

export default {
  generateChangeProposal,
};

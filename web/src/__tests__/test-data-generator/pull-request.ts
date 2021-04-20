import { PullRequest } from '../../backend-api-client';

interface GeneratePullRequestParameters {
  number?: number;
  branchName?: string;
  title?: string;
  repository?: string;
  labels?: string[];
}

const generatePullRequest = ({
  number = 1,
  branchName = `pr-branch-${number}`,
  title = 'Test PullRequest 1',
  repository = 'test-owner/specs-test',
  labels = ['project-x'],
}: GeneratePullRequestParameters = {}): PullRequest => {
  const url = `https://github.com/${repository}/pull/${number}`;

  return {
    number,
    branchName,
    title,
    url,
    labels,
    updatedAt: '2020-02-18T22:33:51Z',
  };
};

export default {
  generatePullRequest,
};

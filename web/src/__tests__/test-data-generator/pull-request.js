import Repository from './repository';

const generatePullRequest = ({
  number = 1,
  title,
  branchName = 'change-branch',
  repository = Repository.generateRepository(),
  labels = ['project-x'],
} = {}) => {
  const url = `https://github.com/${repository.owner}/${repository.name}/pull/${number}`;

  return {
    repository,
    branchName,
    number,
    url,
    labels,
    changedFiles: ['specs/example-template.yaml'],
    title,
  };
};

export default {
  generatePullRequest,
};

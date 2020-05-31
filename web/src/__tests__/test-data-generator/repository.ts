import { Repository } from '../../api-client/models';

const generateRepository = ({ name = 'specs-test', owner = 'test-owner' } = {}): Repository => ({
  owner,
  name,
  htmlUrl: `https://github.com/${owner}/${name}`,
  nameWithOwner: `${owner}/${name}`,
});

export default {
  generateRepository,
};

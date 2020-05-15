const generateRepository = ({ name = 'specs-test', owner = 'test-owner' } = {}) => ({
  owner,
  name,
  htmlUrl: `https://github.com/${owner}/${name}`,
  nameWithOwner: `${owner}/${name}`,
});

export default {
  generateRepository,
};

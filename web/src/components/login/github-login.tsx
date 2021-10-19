import React, { FunctionComponent } from 'react';

const GitHubLoginComponent: FunctionComponent = () => {
  window.location.replace('https://github.com');

  return (
    <div>
      Redirecting to GitHub Login...
    </div>
  );
};

export default GitHubLoginComponent;

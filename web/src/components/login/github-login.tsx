import React, { FunctionComponent } from 'react';
import { useLocation } from 'react-router-dom';
import { extractLoginCallbackURL, extractLoginRedirectReturnToPath } from '../../routes';

const GitHubLoginComponent: FunctionComponent = () => {
  const { search } = useLocation();
  const params = new URLSearchParams(search);
  const callbackCode = params.get('code');

  if (!callbackCode) {
    const randomNum = Math.floor(Math.random() * 99999999) + 100000000;
    const returnTo = extractLoginRedirectReturnToPath();
    const stateString = `${randomNum}+${returnTo}`;
    const stateEncoded = btoa(stateString);

    const loginCallbackUrl = extractLoginCallbackURL();

    const githubLoginPage = 'https://github.com/login/oauth/authorize';
    const githubLoginParams = new URLSearchParams();
    githubLoginParams.set('client_id', 'Iv1.8581b3b1c54b9e1c');
    githubLoginParams.set('redirect_uri', loginCallbackUrl);
    githubLoginParams.set('state', stateEncoded);

    const loginLocation = `${githubLoginPage}?${githubLoginParams.toString()}`;
    window.location.replace(loginLocation);

    return (
      <div>
        Redirecting to GitHub Login...
      </div>
    );
  }

  return (
    <div>
      Login successful. Fetching user details..
    </div>
  );
};

export default GitHubLoginComponent;

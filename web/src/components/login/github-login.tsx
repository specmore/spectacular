import React, { FunctionComponent } from 'react';
import { useLocation } from 'react-router-dom';
import { extractLoginCallbackURL, extractLoginRedirectReturnToPath } from '../../routes';
import LoginStateService from './login-state-service';

const GITHUB_LOGIN_LOCATION = 'https://github.com/login/oauth/authorize';

interface GitHubLoginComponentProps {
  clientId: string;
}

const GitHubLoginComponent: FunctionComponent<GitHubLoginComponentProps> = ({ clientId }) => {
  const { search } = useLocation();
  const params = new URLSearchParams(search);
  const callbackCode = params.get('code');
  const returnedState = params.get('state');

  if (!callbackCode) {
    const returnTo = extractLoginRedirectReturnToPath();
    const loginState = LoginStateService.generateAndStoreLoginState(returnTo);

    const loginCallbackUrl = extractLoginCallbackURL();

    const githubLoginParams = new URLSearchParams();
    githubLoginParams.set('client_id', clientId);
    githubLoginParams.set('redirect_uri', loginCallbackUrl);
    githubLoginParams.set('state', loginState);

    const loginLocation = `${GITHUB_LOGIN_LOCATION}?${githubLoginParams.toString()}`;
    window.location.replace(loginLocation);

    return (
      <div>
        Redirecting to GitHub Login...
      </div>
    );
  }

  const isReturnedStateValid = LoginStateService.isReturnedStateValid(returnedState);
  if (!isReturnedStateValid) {
    return (
      <div>
        Login error. State returned is invalid.
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

import React, { FunctionComponent } from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import { extractLoginRedirectReturnToPath } from '../../routes';
import { extractLoginCallbackURL } from './routes';
import LoginStateService from './login-state-service';
import GitHubLoginCreateUserSession from './github-login-create-user-session';
import GitHubLoginClearUserSession from './github-login-clear-user-session';

const GITHUB_LOGIN_LOCATION = 'https://github.com/login/oauth/authorize';

interface GitHubLoginComponentProps {
  clientId: string;
}

const GitHubLoginComponent: FunctionComponent<GitHubLoginComponentProps> = ({ clientId }) => {
  const history = useHistory();
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

    const githubRedirection = () => {
      const loginLocation = `${GITHUB_LOGIN_LOCATION}?${githubLoginParams.toString()}`;
      window.location.replace(loginLocation);
    };

    return (
      <GitHubLoginClearUserSession redirector={githubRedirection} />
    );
  }

  const isReturnedStateValid = LoginStateService.isReturnedStateValid(returnedState);
  if (!isReturnedStateValid) {
    return (
      <div data-testid="login-state-error-container">
        Login error. State returned is invalid.
      </div>
    );
  }

  const returnToLocation = LoginStateService.getReturnToLocation();
  const loginCompleteRedirection = () => {
    const redirectLocation = returnToLocation !== 'null' ? returnToLocation : '/';
    history.replace(redirectLocation);
  };

  return (
    <GitHubLoginCreateUserSession code={callbackCode} redirector={loginCompleteRedirection} />
  );
};

export default GitHubLoginComponent;

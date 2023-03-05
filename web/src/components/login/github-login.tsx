import React, { FunctionComponent, useEffect, useState } from 'react';
import { useHistory, useLocation } from 'react-router-dom';
import { extractLoginCallbackURL, extractLoginRedirectReturnToPath } from '../../routes';
import LoginStateService from './login-state-service';
import { useCreateUserSession, useDeleteUserSession } from '../../backend-api-client';

const GITHUB_LOGIN_LOCATION = 'https://github.com/login/oauth/authorize';

interface CreateUserSessionComponentProps {
  code: string;
  returnToLocation: string;
}

const GitHubLoginCreateUserSessionComponent: FunctionComponent<CreateUserSessionComponentProps> = ({ code, returnToLocation }) => {
  const history = useHistory();
  const [userDetails, setUserDetails] = useState(null);
  const createUserSession = useCreateUserSession({});
  const { mutate: postAppLoginRequest, loading, error } = createUserSession;
  const appLoginRequest = {
    userCode: code,
  };

  useEffect(() => {
    postAppLoginRequest(appLoginRequest).then((response) => {
      setUserDetails(response);
    });
  }, []);

  if (loading) {
    return (
      <div>
        GitHub login successful. Creating user session...
      </div>
    );
  }

  if (userDetails) {
    history.replace(returnToLocation);
    return (
      <div>
        User Session created for &lsquo;
        {userDetails.username}
        &rsquo;.
      </div>
    );
  }

  if (error) {
    // console.error(error);
    return (
      <div>
        GitHub login failed with error: &lsquo;
        {error.data.message}
        &rsquo;
      </div>
    );
  }

  return (
    <div>
      GitHub login successful. Processing...
    </div>
  );
};

interface GitHubLoginComponentProps {
  clientId: string;
}

const GitHubLoginComponent: FunctionComponent<GitHubLoginComponentProps> = ({ clientId }) => {
  const { search } = useLocation();
  const params = new URLSearchParams(search);
  const callbackCode = params.get('code');
  const returnedState = params.get('state');
  const deleteUserSession = useDeleteUserSession({});
  const [isUserLogoutComplete, setIsUserLogoutComplete] = useState(false);
  const { mutate: logoutRequest, loading, error } = deleteUserSession;

  if (!callbackCode) {
    const returnTo = extractLoginRedirectReturnToPath();
    const loginState = LoginStateService.generateAndStoreLoginState(returnTo);

    const loginCallbackUrl = extractLoginCallbackURL();

    const githubLoginParams = new URLSearchParams();
    githubLoginParams.set('client_id', clientId);
    githubLoginParams.set('redirect_uri', loginCallbackUrl);
    githubLoginParams.set('state', loginState);

    useEffect(() => {
      logoutRequest().then(() => setIsUserLogoutComplete(true));
    }, []);

    if (isUserLogoutComplete) {
      const loginLocation = `${GITHUB_LOGIN_LOCATION}?${githubLoginParams.toString()}`;
      window.location.replace(loginLocation);
    }

    return (
      <div>
        Logging out...
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

  const returnToLocation = LoginStateService.getReturnToLocation();

  return (
    <GitHubLoginCreateUserSessionComponent code={callbackCode} returnToLocation={returnToLocation} />
  );
};

export default GitHubLoginComponent;

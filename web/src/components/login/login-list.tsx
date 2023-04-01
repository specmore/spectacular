import React, { FunctionComponent, useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import {
  Header,
} from 'semantic-ui-react';
import { GitHubLoginButton } from '../../routes';
import { useDeleteUserSession } from '../../backend-api-client';

const LoginList: FunctionComponent = () => (
  <div data-testid="login-list-container">
    <Header>Welcome</Header>
    <p>Please log in using an option below to continue</p>
    <GitHubLoginButton />
  </div>
);

const LoggingOut: FunctionComponent = () => (
  <div data-testid="logging-out-container">
    Logging out...
  </div>
);

const LoginListContainer: FunctionComponent = () => {
  const deleteUserSession = useDeleteUserSession({});
  const [isUserLogoutComplete, setIsUserLogoutComplete] = useState(false);
  const { mutate: logoutRequest } = deleteUserSession;
  const { search } = useLocation();
  const params = new URLSearchParams(search);
  const isLogout = params.get('logout') != null;

  // console.log('isLogout: ', isLogout);
  // console.log('isUserLogoutComplete: ', isUserLogoutComplete);

  useEffect(() => {
    if (isLogout) logoutRequest().then(() => setIsUserLogoutComplete(true));
  }, []);

  if (isLogout && !isUserLogoutComplete) return (<LoggingOut />);

  return (<LoginList />);
};

export default LoginListContainer;

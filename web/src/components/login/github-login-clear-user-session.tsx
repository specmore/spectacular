import React, { FunctionComponent, useEffect, useState } from 'react';
import { useDeleteUserSession } from '../../backend-api-client';

interface GitHubLoginClearUserSessionComponentProps {
  redirector: VoidFunction;
}

const GitHubLoginClearUserSessionComponent: FunctionComponent<GitHubLoginClearUserSessionComponentProps> = ({ redirector }) => {
  const deleteUserSession = useDeleteUserSession({});
  const [isUserLogoutComplete, setIsUserLogoutComplete] = useState(false);
  const { mutate: logoutRequest } = deleteUserSession;

  useEffect(() => {
    logoutRequest().then(() => setIsUserLogoutComplete(true));
  }, []);

  if (!isUserLogoutComplete) {
    return (
      <div data-testid="logging-out-container">
        Logging out...
      </div>
    );
  }

  // invoke the redirection
  redirector();

  return (
    <div data-testid="redirecting-container">
      Redirecting to GitHub...
    </div>
  );
};

export default GitHubLoginClearUserSessionComponent;

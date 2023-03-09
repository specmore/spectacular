import React, { FunctionComponent, useEffect, useState } from 'react';
import { ErrorDetails, useCreateUserSession } from '../../backend-api-client';

interface CreateUserSessionComponentProps {
  code: string;
  redirector: VoidFunction;
}

const GitHubLoginCreateUserSessionComponent: FunctionComponent<CreateUserSessionComponentProps> = ({ code, redirector }) => {
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
      <div data-testid="creating-user-session-container">
        GitHub login successful. Creating user session...
      </div>
    );
  }

  if (userDetails) {
    redirector();
    return (
      <div data-testid="user-session-created-container">
        User Session created for &lsquo;
        {userDetails.username}
        &rsquo;.
      </div>
    );
  }

  if (error) {
    const errorDetails = error.data as ErrorDetails;
    return (
      <div data-testid="create-user-session-error-container">
        GitHub login failed with error: &lsquo;
        {errorDetails.message}
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

export default GitHubLoginCreateUserSessionComponent;

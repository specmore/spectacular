import React, { FunctionComponent } from 'react';
import { Dropdown } from 'semantic-ui-react';
import { useGetUserDetails } from '../../backend-api-client';
import { redirectToLogin } from '../../routes';

const UserMenuBarItem: FunctionComponent = () => {
  const getUserDetails = useGetUserDetails({});
  const { data: getUserDetailsResult, loading, error } = getUserDetails;

  if (getUserDetailsResult) {
    return (
      <Dropdown item pointing text={getUserDetailsResult.fullName} data-testid="user-menu-bar-item">
        <Dropdown.Menu>
          <Dropdown.Item as="a" href={getUserDetailsResult.profileImageUrl}>
            {`Signed in as ${getUserDetailsResult.username}`}
          </Dropdown.Item>
          <Dropdown.Item onClick={() => redirectToLogin()}>Sign out</Dropdown.Item>
        </Dropdown.Menu>
      </Dropdown>
    );
  }

  return null;
};

export default UserMenuBarItem;

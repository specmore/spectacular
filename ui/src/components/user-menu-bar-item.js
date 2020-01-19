import React, { useState, useEffect } from "react";
import { Dropdown, Icon } from 'semantic-ui-react';
import { fetchUserInfo } from '../api-client';

const UserMenuBarItem = () => {
    const [user, setUser] = useState(null);
    const [errorMessage, setErrorMessage] = useState(null);

    const fetchInfoUserData = async () => {
        try {
          const userInfoData = await fetchUserInfo();
          setUser(userInfoData);
        } catch (error) {
          console.error(error);
          setErrorMessage("An error occurred while fetching the logged in user info.");
        }
    };

    useEffect(() => {
        fetchInfoUserData();
    })

    if (user) {
        return (
            <Dropdown item pointing text={user.name}>
              <Dropdown.Menu>
                <Dropdown.Item>Signed in as {user.login}</Dropdown.Item>
                <Dropdown.Item>Sign out</Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>
        );
    };

    return null;
};

export default UserMenuBarItem;

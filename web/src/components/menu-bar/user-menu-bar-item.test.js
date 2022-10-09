import React from 'react';
import { render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import UserMenuBarItem from './user-menu-bar-item';
import { useGetUserDetails as useGetUserDetailsMock } from '../../backend-api-client';

jest.mock('../../backend-api-client');

describe('UserMenuBarItem component', () => {
  test('successful fetch displays user menu', async () => {
    // given a successful user details response
    const userDetails = {
      username: 'pburls',
      fullName: 'Patrick Burls',
      profileImageUrl: 'https://avatars2.githubusercontent.com/u/11502284?v=4',
    };

    // and a mocked successful catalogue response
    const userDetailsResponse = {
      data: userDetails,
    };

    useGetUserDetailsMock.mockReturnValueOnce(userDetailsResponse);

    // when user menu bar item component renders
    const { findByText } = render(<UserMenuBarItem />);

    // then it fetched the user details
    expect(useGetUserDetailsMock).toHaveBeenCalledTimes(1);

    // then it contains an item for each catalogue in the response
    expect(await findByText('Patrick Burls')).toBeInTheDocument();
    expect(await findByText(/pburls/)).toBeInTheDocument();

    // and it contains a sign out item
    expect(await findByText('Sign out')).toBeInTheDocument();
  });

  test('loader is shown before fetch result', async () => {
    // given a mocked catalogues response that is not yet resolved
    const getCatalogueResponse = {
      loading: true,
    };
    useGetUserDetailsMock.mockReturnValueOnce(getCatalogueResponse);

    // when user menu bar item component renders
    const { queryByText } = render(<UserMenuBarItem />);

    // nothing is rendered
    expect(queryByText('Sign out')).toBeNull();
  });
});

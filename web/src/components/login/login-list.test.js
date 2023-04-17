import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { act } from '@testing-library/react';
import { renderWithRouter } from '../../__tests__/test-utils';
import { useDeleteUserSession as useDeleteUserSessionMock } from '../../backend-api-client';
import LoginListContainer from './login-list';

jest.mock('../../backend-api-client');

describe('LoginListContainer component', () => {
  test('does a logout if the logout query parameter is given', async () => {
    // given an unresolved logout response promise
    let logoutRequestResolve;
    const logoutMutate = () => new Promise((resolve) => {
      logoutRequestResolve = resolve;
    });
    const deleteUserSession = { mutate: logoutMutate };

    useDeleteUserSessionMock.mockReturnValue(deleteUserSession);

    // and a logout query parameter
    const queryParameters = '?logout';

    // when LoginContainer component renders
    const { findByTestId, queryByTestId } = renderWithRouter(<LoginListContainer />, queryParameters);

    // then a logging out container is shown
    expect(await findByTestId('logging-out-container')).toBeInTheDocument();

    // and the login-list-container is not yet shown
    expect(queryByTestId('login-list-container')).not.toBeInTheDocument();

    // when the delete user mutate completes
    await act(async () => logoutRequestResolve());

    // then the logging out container is removed
    expect(queryByTestId('logging-out-container')).not.toBeInTheDocument();

    // and the login-list-container container is shown
    expect(await findByTestId('login-list-container')).toBeInTheDocument();
  });
});

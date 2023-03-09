import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { act } from '@testing-library/react';
import { renderWithRouter } from '../../__tests__/test-utils';
import { useDeleteUserSession as useDeleteUserSessionMock } from '../../backend-api-client';
import GitHubLoginClearUserSession from './github-login-clear-user-session';

jest.mock('../../backend-api-client');

describe('GitHubLoginClearUserSession component', () => {
  test('does a logout and redirect', async () => {
    // given an unresolved logout response promise
    let logoutRequestResolve;
    const logoutMutate = () => new Promise((resolve) => {
      logoutRequestResolve = resolve;
    });
    const deleteUserSession = { mutate: logoutMutate };

    useDeleteUserSessionMock.mockReturnValue(deleteUserSession);

    // and a redirector function
    const redirectorMock = jest.fn();

    // when GitHub Login Clear User Session component renders
    const { findByTestId, queryByTestId } = renderWithRouter(<GitHubLoginClearUserSession redirector={redirectorMock} />);

    // then a logging out container is shown
    expect(await findByTestId('logging-out-container')).toBeInTheDocument();

    // when the delete user mutate completes
    await act(async () => logoutRequestResolve());

    // then the redirector is invoked
    expect(redirectorMock.mock.calls).toHaveLength(1);

    // and the logging out container is removed
    expect(queryByTestId('logging-out-container')).not.toBeInTheDocument();

    // and the redirecting container is shown
    expect(await findByTestId('redirecting-container')).toBeInTheDocument();
  });
});

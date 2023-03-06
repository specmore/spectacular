import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { act } from '@testing-library/react';
import { renderWithRouter } from '../../__tests__/test-utils';
import GitHubLogin from './github-login';
import { useDeleteUserSession as useDeleteUserSessionMock } from '../../backend-api-client';

jest.mock('../../backend-api-client');

const replaceMock = jest.fn();

delete window.location;
window.location = { replace: replaceMock };

afterEach(() => {
  replaceMock.mockClear();
});

describe('GitHubLogin component', () => {
  test('initial navigation does a logout and redirect to the GitHub login screen', async () => {
    // given an unresolved logout response promise
    let logoutRequestResolve;
    const logoutMutate = () => new Promise((resolve, reject) => {
      logoutRequestResolve = resolve;
    });
    const deleteUserSession = { mutate: logoutMutate };

    useDeleteUserSessionMock.mockReturnValue(deleteUserSession);

    // when GitHub Login component renders
    const { findByTestId, queryByTestId } = renderWithRouter(<GitHubLogin clientId="testClientId" />);

    // then a logging out container is shown
    expect(await findByTestId('logging-out-container')).toBeInTheDocument();

    // when the delete user mutate completes
    await act(async () => logoutRequestResolve());

    // then the browser location is replaced to the GitHub Login location
    expect(replaceMock.mock.calls).toHaveLength(1);

    // and the logging out container is removed
    expect(queryByTestId('logging-out-container')).not.toBeInTheDocument();

    // and the redirecting container is shown
    expect(await findByTestId('redirecting-container')).toBeInTheDocument();
  });
});

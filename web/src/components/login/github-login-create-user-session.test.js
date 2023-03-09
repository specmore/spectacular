import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { renderWithRouter } from '../../__tests__/test-utils';
import { useCreateUserSession as useCreateUserSessionMock } from '../../backend-api-client';
import GitHubLoginCreateUserSession from './github-login-create-user-session';

jest.mock('../../backend-api-client');

describe('GitHubLoginCreateUserSession component', () => {
  test('uses the provided code to start the creation of a user session', async () => {
    // given a provided code
    const code = 'testCode';

    // and a create user session mutation that has started
    const postAppLoginRequest = () => new Promise(() => { });
    const createUserSession = { mutate: postAppLoginRequest, loading: true };

    useCreateUserSessionMock.mockReturnValue(createUserSession);

    // and a redirector function
    const redirectorMock = jest.fn();

    // when GitHub Login Create User Session component renders
    const { findByTestId } = renderWithRouter(<GitHubLoginCreateUserSession code={code} redirector={redirectorMock} />);

    // then a creating-user-session container is shown
    expect(await findByTestId('creating-user-session-container')).toBeInTheDocument();

    // and the redirector is not yet called
    expect(redirectorMock.mock.calls).toHaveLength(0);
  });

  test('shows an error if the user session creation fails', async () => {
    // given a provided code
    const code = 'testCode';

    // and a create user session mutation that has failed with an error
    const postAppLoginRequest = () => new Promise(() => { });
    const error = { data: { message: 'something failed' } };
    const createUserSession = { mutate: postAppLoginRequest, loading: false, error };

    useCreateUserSessionMock.mockReturnValue(createUserSession);

    // and a redirector function
    const redirectorMock = jest.fn();

    // when GitHub Login Create User Session component renders
    const { findByTestId } = renderWithRouter(<GitHubLoginCreateUserSession code={code} redirector={redirectorMock} />);

    // then a create-user-session-error container is shown
    expect(await findByTestId('create-user-session-error-container')).toBeInTheDocument();

    // and the redirector is not yet called
    expect(redirectorMock.mock.calls).toHaveLength(0);
  });

  test('shows a successful user session creation and redirects', async () => {
    // given a provided code
    const code = 'testCode';

    // and a user details response
    const userDetails = {
      username: 'testUser',
    };

    // and a create user session mutation that is complete
    const postAppLoginRequest = () => Promise.resolve(userDetails);
    const createUserSession = { mutate: postAppLoginRequest, loading: false };

    useCreateUserSessionMock.mockReturnValue(createUserSession);

    // and a redirector function
    const redirectorMock = jest.fn();

    // when GitHub Login Create User Session component renders
    const { findByTestId } = renderWithRouter(<GitHubLoginCreateUserSession code={code} redirector={redirectorMock} />);

    // then a user-session-created container is shown
    expect(await findByTestId('user-session-created-container')).toBeInTheDocument();

    // and the redirector is called
    expect(redirectorMock.mock.calls).toHaveLength(1);
  });
});

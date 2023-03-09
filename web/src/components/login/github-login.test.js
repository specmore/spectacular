import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { renderWithRouter } from '../../__tests__/test-utils';
import GitHubLogin from './github-login';
import LoginStateServiceMock from './login-state-service';
import GitHubLoginCreateUserSessionMock from './github-login-create-user-session';
import GitHubLoginClearUserSessionMock from './github-login-clear-user-session';

// mocks
jest.mock('./login-state-service');
jest.mock('./github-login-create-user-session', () => jest.fn(() => null));
jest.mock('./github-login-clear-user-session', () => jest.fn(() => null));

afterEach(() => {
  LoginStateServiceMock.isReturnedStateValid.mockClear();
  GitHubLoginCreateUserSessionMock.mockClear();
  GitHubLoginClearUserSessionMock.mockClear();
});

describe('GitHubLogin component', () => {
  test('initial navigation shows the GitHubLoginClearUserSession component', async () => {
    // given no query parameters

    // when GitHub Login component renders
    renderWithRouter(<GitHubLogin clientId="testClientId" />);

    // then the GitHubLoginClearUserSessionMock component is shown
    expect(GitHubLoginClearUserSessionMock).toHaveBeenCalledTimes(1);
  });

  test('after return from GitHub login with callback code and invalid state shows login error', async () => {
    // given a callback code and state in query parameters
    const callbackCode = 'codeX';
    const returnedState = 'stateX';
    const queryParameters = `?code=${callbackCode}&state=${returnedState}`;

    // and the state value does not match the original state parameter sent to GitHub
    LoginStateServiceMock.isReturnedStateValid.mockReturnValue(false);

    // when GitHub Login component renders
    const { findByTestId } = renderWithRouter(<GitHubLogin clientId="testClientId" />, queryParameters);

    // then a login state error message is shown
    expect(await findByTestId('login-state-error-container')).toBeInTheDocument();

    // and the GitHubLoginCreateUserSession component is not
    expect(GitHubLoginCreateUserSessionMock).not.toHaveBeenCalled();
  });

  test('after return from GitHub login with callback code and valid state shows create user session', async () => {
    // given a callback code and invalid state in query parameters
    const callbackCode = 'codeX';
    const returnedState = 'stateX';
    const queryParameters = `?code=${callbackCode}&state=${returnedState}`;

    // and the state value does match the original state parameter sent to GitHub
    LoginStateServiceMock.isReturnedStateValid.mockReturnValue(true);

    // when GitHub Login component renders
    const { queryByTestId } = renderWithRouter(<GitHubLogin clientId="testClientId" />, queryParameters);

    // then a login state error message is not shown
    expect(queryByTestId('login-state-error-container')).not.toBeInTheDocument();

    // and the GitHubLoginCreateUserSession component is shown
    expect(GitHubLoginCreateUserSessionMock).toHaveBeenCalledTimes(1);
  });
});

import React, { FunctionComponent } from 'react';
import { Button, Icon } from 'semantic-ui-react';
import { Link, useLocation } from 'react-router-dom';

// todo: move remaining login specific functions from the root routes file.

export const GITHUB_LOGIN_ROUTE = 'github';

export const extractLoginCallbackURL = (): string => {
  const { location } = window;
  return `${location.protocol}//${location.host}${location.pathname}`;
};

export const pathForGitHubLogin = (loginPath: string): string => `${loginPath}/${GITHUB_LOGIN_ROUTE}`;

interface GitHubLoginButtonProps {
  loginUrl: string;
}

export const GitHubLoginButton: FunctionComponent<GitHubLoginButtonProps> = ({ loginUrl }) => (
  <Button icon labelPosition="left" as={Link} to={`${loginUrl}/${GITHUB_LOGIN_ROUTE}`}>
    <Icon name="github" />
    GitHub Login
  </Button>
);

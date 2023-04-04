import React, { FunctionComponent } from 'react';
import {
  Switch,
  Route,
  useRouteMatch,
} from 'react-router-dom';
import {
  Image,
} from 'semantic-ui-react';
import FooterBar from '../footer-bar';
import SpectacularLogo from '../../assets/images/spectacular-logo.svg';
import './login.less';
import LoginList from './login-list';
import GitHubLogin from './github-login';
import { pathForGitHubLogin } from './routes';

interface LoginContainerProps {
  clientId: string;
}

const LoginContainer: FunctionComponent<LoginContainerProps> = ({ clientId }) => {
  const { path, url } = useRouteMatch();
  return (
    <div className="content-container">
      <div className="main-content login-container" data-testid="login-container">
        <div className="login-tile">
          <Image src={SpectacularLogo} className="logo-image" />
          <Switch>
            <Route exact path={path}>
              <LoginList loginUrl={url} />
            </Route>
            <Route path={pathForGitHubLogin(path)}>
              <GitHubLogin clientId={clientId} />
            </Route>
          </Switch>
        </div>
      </div>
      <FooterBar />
    </div>
  );
};

export default LoginContainer;

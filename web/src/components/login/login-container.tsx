import React, { FunctionComponent } from 'react';
import {
  Image,
} from 'semantic-ui-react';
import FooterBar from '../footer-bar';
import SpectacularLogo from '../../assets/images/spectacular-logo.svg';
import './login.less';
import LoginList from './login-list';

interface LoginContainerProps {
  clientId: string;
}

const LoginContainer: FunctionComponent<LoginContainerProps> = ({ clientId }) => (
  <div className="content-container">
    <div className="main-content login-container" data-testid="login-container">
      <div className="login-tile">
        <Image src={SpectacularLogo} className="logo-image" />
        <LoginList />
      </div>
    </div>
    <FooterBar />
  </div>
);

export default LoginContainer;

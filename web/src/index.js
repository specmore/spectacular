/* eslint-disable no-console */
import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter as Router } from 'react-router-dom';
import { RestfulProvider } from 'restful-react';
import InstallationContainer from './components/installation-container';
import MenuBar from './components/menu-bar';
import FooterBar from './components/footer-bar';
import './index.less';
import 'semantic-ui-less/semantic.less';

const onAPIError = (error) => {
  if (error.status === 401) {
    console.debug('expired token');
    console.debug(`current location:${window.location.pathname}`);

    const redirectParams = new URLSearchParams();
    redirectParams.append('backTo', window.location.pathname);

    window.location.assign(`/login/github?${redirectParams.toString()}`);
  }
};

const Index = () => (
  <RestfulProvider base="/api" onError={onAPIError}>
    <Router>
      <div className="content-container">
        <MenuBar />
        <div className="main-content">
          <InstallationContainer />
        </div>
        <FooterBar />
      </div>
    </Router>
  </RestfulProvider>
);

ReactDOM.render(<Index />, document.getElementById('root'));

console.info(`VERSION: ${VERSION}`);
console.info(`SHA: ${SHORTSHA}`);

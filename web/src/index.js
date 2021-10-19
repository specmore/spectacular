/* eslint-disable no-console */
import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import { QueryParamProvider } from 'use-query-params';
import { RestfulProvider } from 'restful-react';
import InstallationContainer from './components/installation-container';
import MenuBar from './components/menu-bar';
import FooterBar from './components/footer-bar';
import GitHubLogin from './components/login/github-login';
import './index.less';
import 'semantic-ui-less/semantic.less';
import { GITHUB_LOGIN_ROUTE, redirectToLogin } from './routes';

const onAPIError = (error) => {
  if (error.status === 401) {
    redirectToLogin();
  }
};

const Index = () => (
  <RestfulProvider base="/api" onError={onAPIError}>
    <Router>
      <QueryParamProvider ReactRouterRoute={Route}>
        <Switch>
          <Route exact path={GITHUB_LOGIN_ROUTE}>
            <GitHubLogin />
          </Route>
          <Route path="*">
            <div className="content-container">
              <MenuBar />
              <div className="main-content">
                <InstallationContainer />
              </div>
              <FooterBar />
            </div>
          </Route>
        </Switch>
      </QueryParamProvider>
    </Router>
  </RestfulProvider>
);

ReactDOM.render(<Index />, document.getElementById('root'));

console.info(`VERSION: ${VERSION}`);
console.info(`SHA: ${SHORTSHA}`);

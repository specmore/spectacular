/* eslint-disable no-console */
import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import { QueryParamProvider } from 'use-query-params';
import { RestfulProvider } from 'restful-react';
import './index.less';
import 'semantic-ui-less/semantic.less';
import { redirectToLogin } from './routes';
import AppContainer from './components/app/app-container';

const onAPIError = (error) => {
  if (error.status === 401) {
    redirectToLogin();
  }
};

const Index = () => (
  <RestfulProvider base="/api" onError={onAPIError}>
    <Router>
      <QueryParamProvider ReactRouterRoute={Route}>
        <AppContainer />
      </QueryParamProvider>
    </Router>
  </RestfulProvider>
);

ReactDOM.render(<Index />, document.getElementById('root'));

console.info(`VERSION: ${VERSION}`);
console.info(`SHA: ${SHORTSHA}`);

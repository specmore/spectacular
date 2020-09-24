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

const Index = () => (
  <RestfulProvider base="/api">
    <Router>
      <MenuBar />
      <InstallationContainer />
      <FooterBar />
    </Router>
  </RestfulProvider>
);

ReactDOM.render(<Index />, document.getElementById('root'));

console.info(`VERSION: ${VERSION}`);
console.info(`SHA: ${SHORTSHA}`);

import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';
import { RestfulProvider } from 'restful-react';
import InstallationWelcome from './components/installation-welcome';
import CatalogueContainer from './components/catalogue-container';
import NotFound from './components/not-found';
import MenuBar from './components/menu-bar';
import FooterBar from './components/footer-bar';
import LocationBar from './components/location-bar';
import './index.css';
import 'semantic-ui-less/semantic.less';
import { CATALOGUE_LIST_ROUTE, CATALOGUE_CONTAINER_ROUTE, CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE } from './routes';

const Index = () => (
  <RestfulProvider base="/api">
    <Router>
      <MenuBar />
      <LocationBar />
      <div style={{ paddingTop: '2em' }}>
        <div style={{ position: 'relative' }}>
          <Switch>
            <Route exact path={CATALOGUE_LIST_ROUTE}>
              <InstallationWelcome />
            </Route>
            <Route exact path={[CATALOGUE_CONTAINER_ROUTE, CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE]}>
              <CatalogueContainer />
            </Route>
            <Route path="*">
              <NotFound />
            </Route>
          </Switch>
        </div>
      </div>
      <FooterBar />
    </Router>
  </RestfulProvider>
);

ReactDOM.render(<Index />, document.getElementById('root'));

console.info(`VERSION: ${VERSION}`);
console.info(`SHA: ${SHORTSHA}`);

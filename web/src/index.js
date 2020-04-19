import React from "react";
import ReactDOM from "react-dom";
import InstallationWelcome from './components/installation-welcome';
import CatalogueContainer from './components/catalogue-container';
import NotFound from './components/not-found';
import MenuBar from './components/menu-bar';
import FooterBar from './components/footer-bar';
import './index.css';
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import { CATALOGUE_LIST_ROUTE, CATALOGUE_CONTAINER_ROUTE, CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE } from './routes';

const Index = () => (
<Router>
  <MenuBar/>
  <div style={{ paddingTop: '4em' }}>
    <div style={{ position: 'relative' }}>
      <Switch>
        <Route exact path={CATALOGUE_LIST_ROUTE}>
          <InstallationWelcome/>
        </Route>
        <Route exact path={[CATALOGUE_CONTAINER_ROUTE, CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE]}>
          <CatalogueContainer/>
        </Route>
        <Route path="*">
          <NotFound />
        </Route>
      </Switch>
    </div>
  </div>
  <FooterBar/>
</Router>);

ReactDOM.render(<Index />, document.getElementById("root"));

console.log(`VERSION: ${VERSION}`);
console.log(`SHA: ${SHORTSHA}`);

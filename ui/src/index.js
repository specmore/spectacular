import React from "react";
import ReactDOM from "react-dom";
import { Container } from 'semantic-ui-react';
import InstallationWelcome from './components/installation-welcome';
import CatalogueContainer from './components/catalogue-container';
import NotFound from './components/not-found';
import MenuBar from './components/menu-bar';
import FooterBar from './components/footer-bar';
import './index.css';
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import { CATALOGUE_CONTAINER_ROUTE } from './routes';

const Index = () => (
<Router>
  <MenuBar/>
  <Container style={{ marginTop: '4em' }}>
    <Switch>
      <Route exact path="/">
        <InstallationWelcome/>
      </Route>
      <Route path={CATALOGUE_CONTAINER_ROUTE}>
        <CatalogueContainer/>
      </Route>
      <Route path="*">
        <NotFound />
      </Route>
    </Switch>
  </Container>
  <FooterBar/>
</Router>);

ReactDOM.render(<Index />, document.getElementById("root"));

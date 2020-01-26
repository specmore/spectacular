import React from "react";
import ReactDOM from "react-dom";
import { Container, Segment } from 'semantic-ui-react';
import CatalogueList from './components/catalogue-list';
import MenuBar from './components/menu-bar';
import FooterBar from './components/footer-bar';
import './index.css';
import { BrowserRouter as Router } from "react-router-dom"

const Index = () => (
<Router>
  <MenuBar/>
  <Container style={{ marginTop: '4em' }}>
    <Segment>
      <CatalogueList installationId={6436743} configRepo={"pburls/specs-app"}/>
    </Segment>
  </Container>
  <FooterBar/>
</Router>);

ReactDOM.render(<Index />, document.getElementById("root"));

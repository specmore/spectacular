import React from "react";
import ReactDOM from "react-dom";
import { Container, Segment } from 'semantic-ui-react';
// import CatalogueList from './components/catalogue-list';
import InstanceList from './components/instance-list';
import MenuBar from './components/menu-bar';
import FooterBar from './components/footer-bar';
import './index.css';
import { BrowserRouter as Router } from "react-router-dom"

const Index = () => (
<Router>
  <MenuBar/>
  <Container style={{ marginTop: '4em' }}>
    <Segment>
      <InstanceList/>
    </Segment>
  </Container>
  <FooterBar/>
</Router>);

ReactDOM.render(<Index />, document.getElementById("root"));

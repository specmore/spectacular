import React from "react";
import ReactDOM from "react-dom";
import { Container, Segment } from 'semantic-ui-react';
import InstallationWelcome from './components/installation-welcome';
import MenuBar from './components/menu-bar';
import FooterBar from './components/footer-bar';
import './index.css';
import { BrowserRouter as Router } from "react-router-dom"

const Index = () => (
<Router>
  <MenuBar/>
  <Container style={{ marginTop: '4em' }}>
    <InstallationWelcome/>
  </Container>
  <FooterBar/>
</Router>);

ReactDOM.render(<Index />, document.getElementById("root"));

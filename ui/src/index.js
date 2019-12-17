import React from "react";
import ReactDOM from "react-dom";
import { Container, Segment } from 'semantic-ui-react';
import CatalogueList from './components/catalogue-list';
import './index.css';

const Index = () => (
<div>
    <Container>
      <Segment>
        <CatalogueList installationId={5521529} configRepo={"pburls/specs-app"}/>
      </Segment>
    </Container>
</div>);

ReactDOM.render(<Index />, document.getElementById("root"));

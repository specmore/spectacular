import React from "react";
import ReactDOM from "react-dom";
import { Container } from 'semantic-ui-react';
import CatalogueList from './components/catalogue-list';
import './index.css';

const Index = () => (
<div>
    <Container>
        <CatalogueList installationId={5521529} configRepo={"pburls/specs-app"}/>
    </Container>
</div>);

ReactDOM.render(<Index />, document.getElementById("root"));

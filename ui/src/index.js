import React from "react";
import ReactDOM from "react-dom";
import { Container } from 'semantic-ui-react';
import CatalogueList from './components/catalogue-list';
import './index.css';

const Index = () => (
<div>
    <Container>
        <CatalogueList/>
    </Container>
</div>);

ReactDOM.render(<Index />, document.getElementById("root"));

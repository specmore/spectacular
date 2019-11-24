import React from "react";
import ReactDOM from "react-dom";
import { Segment } from 'semantic-ui-react';
import './index.css';

console.log('hello world!');

const Index = () => (
<div>
    <Segment>
        Spectacular
    </Segment>
</div>);

ReactDOM.render(<Index />, document.getElementById("root"));

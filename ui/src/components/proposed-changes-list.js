import React from "react";
import { Header } from 'semantic-ui-react'
import ProposedChangeItem from './proposed-change-item';

const ProposedChangesList = ({proposedChanges}) => {
    return (
        <div data-testid='proposed-changes-list'>
            <Header as='h5' attached='top'>Open change proposals</Header>
            {proposedChanges.map((proposedChange, index) => (<ProposedChangeItem key={index} {...proposedChange} />))}
        </div>
    );
};

export default ProposedChangesList;
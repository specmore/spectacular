import React from "react";
import { Grid, Header } from 'semantic-ui-react'
import ProposedChangeItem from './proposed-change-item';

const ProposedChangesList = ({proposedChanges}) => {
    return (
        <div data-testid='proposed-changes-list'>
            <Header as='h5'>Open change proposals</Header>
            <Grid divided='vertically'>
                {proposedChanges.map((proposedChange, index) => (<ProposedChangeItem key={index} {...proposedChange} />))}
            </Grid>
        </div>
    );
};

export default ProposedChangesList;
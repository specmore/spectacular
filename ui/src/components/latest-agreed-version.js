import React from "react";
import { Header, Segment } from 'semantic-ui-react';
import SpecRevision from './spec-revision';

const LatestAgreedVersion = ({latestAgreedSpecItem}) => (
    <React.Fragment>
        <Header as='h5' attached='top'>Latest agreed version</Header>
        <Segment attached data-testid='latest-agreed-version'>
            <SpecRevision specItem={latestAgreedSpecItem} />
        </Segment>
    </React.Fragment>
);

export default LatestAgreedVersion;
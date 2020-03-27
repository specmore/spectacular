import React from "react";
import SpecRevision from './spec-revision';

const LatestAgreedVersion = ({latestAgreedSpecItem}) => (
    <React.Fragment>
        <SpecRevision specItem={latestAgreedSpecItem} />
    </React.Fragment>
);

export default LatestAgreedVersion;
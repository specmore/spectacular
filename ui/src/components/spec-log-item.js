import React from "react";
import { useParams } from 'react-router-dom';
import { Segment } from 'semantic-ui-react'

const SpecLogItem = ({children, specItem}) => {
    const { 0: selectedSpecLocation } = useParams();

    const specItemLocation = `${specItem.repository.nameWithOwner}/${specItem.ref}/${specItem.filePath}`;
    const isSelectedSpecItem = specItemLocation === selectedSpecLocation;

    return (
        <Segment attached data-testid='spec-log-item-segment' tertiary={isSelectedSpecItem}>
            {children}
        </Segment>
    );
};

export default SpecLogItem;
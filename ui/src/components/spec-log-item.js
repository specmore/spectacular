import React from "react";
import { useParams } from 'react-router-dom';
import { Segment } from 'semantic-ui-react';
import './spec-log-item.css';

const SpecLogItem = ({children, specItem}) => {
    const { 0: selectedSpecLocation } = useParams();

    const specItemLocation = `${specItem.repository.nameWithOwner}/${specItem.ref}/${specItem.filePath}`;
    const isSelectedSpecItem = specItemLocation === selectedSpecLocation;

    return (
        <Segment attached data-testid='spec-log-item-segment' className={'spec-log-item ' + (isSelectedSpecItem ? 'selected' : '')}>
            {children}
        </Segment>
    );
};

export default SpecLogItem;
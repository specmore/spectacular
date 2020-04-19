import React from 'react';
import { useParams } from 'react-router-dom';
import { Segment } from 'semantic-ui-react';
import './spec-log-item.css';
import { ViewSpecLinkButton } from '../routes';

const SpecLogItem = ({ children, specItem, type }) => {
  const { 0: selectedSpecLocation } = useParams();

  const specItemLocation = `${specItem.repository.nameWithOwner}/${specItem.ref}/${specItem.filePath}`;
  const isSelectedSpecItem = specItemLocation === selectedSpecLocation;
  const selectButton = (<ViewSpecLinkButton specFileLocation={specItemLocation} isSelected={isSelectedSpecItem} />);

  const testId = type ? `spec-log-item-segment-${type}` : 'spec-log-item-segment';

  return (
    <Segment attached data-testid={testId} className={`spec-log-item ${isSelectedSpecItem ? 'selected' : ''}`}>
      <div>
        {children}
      </div>
      <div style={{ overflow: 'auto' }}>
        {selectButton}
      </div>
    </Segment>
  );
};

export default SpecLogItem;

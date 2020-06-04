import React, { FunctionComponent } from 'react';
import { useParams } from 'react-router-dom';
import { Segment } from 'semantic-ui-react';
import './spec-log-item.css';
import { ViewSpecLinkButton } from '../routes';
import { SpecItem } from '../__generated__/backend-api-client';

interface SpecLogItemProps {
  specItem: SpecItem;
  type: string;
}

const SpecLogItem: FunctionComponent<SpecLogItemProps> = ({ children, specItem, type }) => {
  const { 0: selectedSpecItemId } = useParams();

  const isSelectedSpecItem = specItem.id === selectedSpecItemId;
  const selectButton = (<ViewSpecLinkButton specItemId={specItem.id} isSelected={isSelectedSpecItem} />);

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

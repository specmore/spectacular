import React, { FunctionComponent } from 'react';
import { useParams } from 'react-router-dom';
import { Segment } from 'semantic-ui-react';
import './spec-log-item.css';
import { ViewSpecLinkButton, useQuery } from '../routes';
import { SpecItem } from '../backend-api-client';

interface SpecLogItemProps {
  interfaceName: string;
  specItem: SpecItem;
  type: string;
}

const SpecLogItem: FunctionComponent<SpecLogItemProps> = ({
  children,
  interfaceName,
  specItem,
  type,
}) => {
  const { interfaceName: selectedInterfaceName } = useParams();
  const query = useQuery();

  const isSelectedSpecItem = interfaceName === selectedInterfaceName && query.get('ref') === specItem.ref;
  const selectButton = (<ViewSpecLinkButton interfaceName={interfaceName} refName={specItem.ref} isSelected={isSelectedSpecItem} />);

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

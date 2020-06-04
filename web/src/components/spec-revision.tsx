import React, { FunctionComponent } from 'react';
import {
  Label, List, Icon, Message,
} from 'semantic-ui-react';
import Moment from 'react-moment';
import { SemanticCOLORS } from 'semantic-ui-react/dist/commonjs/generic';
import { SpecItem } from '../__generated__/backend-api-client';

interface SpecItemProps {
  specItem: SpecItem;
}

const SpecItemError: FunctionComponent<SpecItemProps> = ({ specItem }) => (
  <Message icon negative data-testid="spec-item-error">
    <Icon name="warning sign" />
    <Message.Content>
      <Message.Header>
        The following errors occurred while processing the specification file
        {' '}
        <a href={specItem.htmlUrl} target="_blank" rel="noopener noreferrer">
          {`'${specItem.id}' in branch '${specItem.ref}'`}
        </a>
        :
      </Message.Header>
      <List bulleted>
        {specItem.parseResult.errors.map((error) => (<List.Item key={error}>{error}</List.Item>))}
      </List>
    </Message.Content>
  </Message>
);

interface SpecRevisionProps {
  specItem: SpecItem;
  branchColor: SemanticCOLORS;
}

const SpecRevision: FunctionComponent<SpecRevisionProps> = ({ specItem, branchColor }) => {
  const labelColor = branchColor || 'olive';

  if (specItem.parseResult.errors.length > 0) return (<SpecItemError specItem={specItem} />);

  return (
    <div>
      <Label color={labelColor} as="a" href={specItem.htmlUrl} target="_blank">
        <Icon name="code branch" />
        {specItem.ref}
      </Label>
      <Label circular>{specItem.parseResult.openApiSpec.version}</Label>
      <Moment fromNow>{specItem.lastModified}</Moment>
    </div>
  );
};

export default SpecRevision;

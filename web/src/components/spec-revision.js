import React from 'react';
import {
  Label, List, Icon, Message,
} from 'semantic-ui-react';
import Moment from 'react-moment';

const SpecItemError = ({ specItem }) => (
  <Message icon negative data-testid="spec-item-error">
    <Icon name="warning sign" />
    <Message.Content>
      <Message.Header>
        The following errors occurred while processing the specification file
        {' '}
        <a href={specItem.htmlUrl} target="_blank" rel="noopener noreferrer">
          {`'${specItem.repository.nameWithOwner}/${specItem.filePath}' in branch '${specItem.ref}'`}
        </a>
        :
      </Message.Header>
      <List bulleted>
        {specItem.parseResult.errors.map((error) => (<List.Item key={error}>{error}</List.Item>))}
      </List>
    </Message.Content>
  </Message>
);

const SpecRevision = ({ specItem, branchColor }) => {
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

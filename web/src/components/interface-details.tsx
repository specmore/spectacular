import React, { FunctionComponent } from 'react';
import { useParams } from 'react-router-dom';
import {
  Icon, Message, Header, List, Label, Button,
} from 'semantic-ui-react';
import { SpecLog, SpecItem } from '../backend-api-client';
import { useQuery, ViewSpecLinkButton } from '../routes';

interface SpecItemProps {
  specItem: SpecItem;
}

const InterfaceDetailsError: FunctionComponent<SpecItemProps> = ({ specItem }) => (
  <Message icon negative data-testid="spec-log-error">
    <Icon name="warning sign" />
    <Message.Content>
      <Message.Header>
        Spec Error
      </Message.Header>
      The following errors occurred while parsing the specification file
      {' '}
      <a href={specItem.htmlUrl} target="_blank" rel="noopener noreferrer">{specItem.fullPath}</a>
      <List bulleted>
        {specItem.parseResult.errors.map((error) => (<List.Item key={error}>{error}</List.Item>))}
      </List>
    </Message.Content>
  </Message>
);

interface InterfaceDetailsContainerProps {
  specLog: SpecLog;
  interfaceName: string;
}

const InterfaceDetailsContainer: FunctionComponent<InterfaceDetailsContainerProps> = ({ specLog, interfaceName }) => {
  const { interfaceName: selectedInterfaceName } = useParams();
  const query = useQuery();

  const specItem = specLog.latestAgreed;
  const isSelectedSpecItem = interfaceName === selectedInterfaceName && query.get('ref') === specItem.ref;
  const viewSpecButton = (<ViewSpecLinkButton interfaceName={interfaceName} refName={specItem.ref} isSelected={isSelectedSpecItem} />);

  if (specItem.parseResult.errors && specItem.parseResult.errors.length > 0) {
    return (<InterfaceDetailsError specItem={specItem} />);
  }

  return (
    <div data-testid="interface-details-container">
      <Header as="h2">{specItem.parseResult.openApiSpec.title}</Header>
      Latest agreed version
      <Label color="blue">{specItem.parseResult.openApiSpec.version}</Label>
      <Button
        icon="file code"
        circular
        size="mini"
        href={specItem.htmlUrl}
        target="_blank"
        rel="noopener noreferrer"
        color="grey"
      />
      {viewSpecButton}
    </div>
  );
};

export default InterfaceDetailsContainer;

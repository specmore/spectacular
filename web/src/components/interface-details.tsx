import React, { FunctionComponent } from 'react';
import {
  Icon, Message, Header, List, Label, Button,
} from 'semantic-ui-react';
import { SpecLog, SpecItem } from '../backend-api-client';

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
}

const InterfaceDetailsContainer: FunctionComponent<InterfaceDetailsContainerProps> = ({ specLog }) => {
  if (specLog.latestAgreed.parseResult.errors && specLog.latestAgreed.parseResult.errors.length > 0) {
    return (<InterfaceDetailsError specItem={specLog.latestAgreed} />);
  }

  return (
    <div data-testid="interface-details-container">
      <Header as="h2">{specLog.latestAgreed.parseResult.openApiSpec.title}</Header>
      Latest agreed version
      <Label color="blue">{specLog.latestAgreed.parseResult.openApiSpec.version}</Label>
      <Button
        icon="file code"
        circular
        size="mini"
        href={specLog.latestAgreed.htmlUrl}
        target="_blank"
        rel="noopener noreferrer"
        color="grey"
      />
      <Button
        icon="eye"
        circular
        size="mini"
      />
    </div>
  );
};

export default InterfaceDetailsContainer;

import React, { FunctionComponent } from 'react';
import {
  Icon, Message, Header, List, Label, Button, Grid,
} from 'semantic-ui-react';
import { SpecLog, SpecItem } from '../backend-api-client';
import { ViewSpecLinkButton, ViewSpecEvolutionLinkButton, OpenSpecItemContentPageButton } from '../routes';

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
  const specItem = specLog.latestAgreed;

  if (specItem.parseResult.errors && specItem.parseResult.errors.length > 0) {
    return (<InterfaceDetailsError specItem={specItem} />);
  }

  const proposedChangesCount = specLog.proposedChanges.length;

  return (
    <div data-testid="interface-details-container">
      <Header as="h2">{specItem.parseResult.openApiSpec.title}</Header>
      <Grid>
        <Grid.Row>
          <Grid.Column>
            <span>Latest agreed version</span>
            <Label color="blue">{specItem.parseResult.openApiSpec.version}</Label>
            <OpenSpecItemContentPageButton specItem={specItem} />
            <ViewSpecLinkButton refName={specItem.ref} interfaceName={interfaceName} />
          </Grid.Column>
        </Grid.Row>
        <Grid.Row>
          <Grid.Column>
            <span>Proposed Changes</span>
            <Label color="green">
              <Icon name="code branch" />
              {proposedChangesCount}
            </Label>
            <ViewSpecEvolutionLinkButton />
          </Grid.Column>
        </Grid.Row>
      </Grid>
    </div>
  );
};

export default InterfaceDetailsContainer;

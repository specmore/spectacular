import React, { FunctionComponent } from 'react';
import {
  Icon, Message, Header, List, Label, Grid,
} from 'semantic-ui-react';
import { SpecItem, SpecEvolutionSummary } from '../backend-api-client';
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
  specEvolutionSummary: SpecEvolutionSummary;
}

const InterfaceDetailsContainer: FunctionComponent<InterfaceDetailsContainerProps> = ({ specEvolutionSummary }) => {
  const specItem = specEvolutionSummary.latestAgreed;

  if (specItem.parseResult.errors && specItem.parseResult.errors.length > 0) {
    return (<InterfaceDetailsError specItem={specItem} />);
  }

  return (
    <div data-testid="interface-details-container">
      <Header as="h2">{specItem.parseResult.openApiSpec.title}</Header>
      <Grid>
        <Grid.Row>
          <Grid.Column width={4}>
            <span>Latest agreed version</span>
          </Grid.Column>
          <Grid.Column width={8}>
            <Label color="blue">{specItem.parseResult.openApiSpec.version}</Label>
          </Grid.Column>
          <Grid.Column width={4}>
            <ViewSpecLinkButton refName={specItem.ref} />
            <OpenSpecItemContentPageButton specItem={specItem} />
          </Grid.Column>
        </Grid.Row>
        <Grid.Row>
          <Grid.Column width={4}>
            <span>Proposed Changes</span>
          </Grid.Column>
          <Grid.Column width={8}>
            <Label color="green">
              <Icon name="code branch" />
              {specEvolutionSummary.proposedChangesCount}
            </Label>
          </Grid.Column>
          <Grid.Column width={4}>
            <ViewSpecEvolutionLinkButton />
          </Grid.Column>
        </Grid.Row>
      </Grid>
    </div>
  );
};

export default InterfaceDetailsContainer;

import React from 'react';
import {
  Icon, Image, Message, Segment, Header, Grid, Label,
} from 'semantic-ui-react';
import ImagePlaceHolder from '../assets/images/image-placeholder.png';
import SpecLog from './spec-log';

const CatalogueError = ({ error, repository }) => (
  <Message icon negative data-testid="catalogue-details-error-message">
    <Icon name="warning sign" />
    <Message.Content>
      <Message.Header>
        An error occurred while parsing the catalogue manifest file in
        <a href={repository.htmlUrl} target="_blank" rel="noopener noreferrer">{repository.nameWithOwner}</a>
      </Message.Header>
      {error}
    </Message.Content>
  </Message>
);

const CatalogueDetails = ({ repository, catalogueManifest, specLogs }) => (
  <div data-testid="catalogue-details-container">
    <Header as="h1" textAlign="center">{catalogueManifest.name}</Header>
    <Header as="h3" attached="top">
      <Icon name="info" />
      Catalogue Details
    </Header>
    <Segment attached data-testid="catalogue-details-segment">
      <Grid divided>
        <Grid.Row>
          <Grid.Column width={13}>
            <p>{catalogueManifest.description}</p>
          </Grid.Column>
          <Grid.Column width={3}>
            <Image src={ImagePlaceHolder} />
          </Grid.Column>
        </Grid.Row>
      </Grid>
      <Label as="a" href={repository.htmlUrl} target="_blank">
        <Icon name="github" />
        {repository.nameWithOwner}
      </Label>
    </Segment>
    <Header as="h3" attached="top">
      <Icon name="list" />
      Interfaces
    </Header>
    <Segment attached data-testid="catalogue-details-interface-list">
      {specLogs.map((specLog, index) => (<SpecLog key={index} specLog={specLog} />))}
    </Segment>
  </div>
);

const CatalogueDetailsContainer = ({ catalogue }) => {
  if (catalogue.error) return (<CatalogueError error={catalogue.error} repository={catalogue.repository} />);

  return (
    <CatalogueDetails
      repository={catalogue.repository}
      catalogueManifest={catalogue.catalogueManifest}
      specLogs={catalogue.specLogs}
    />
  );
};

export default CatalogueDetailsContainer;

import React, { FunctionComponent } from 'react';
import {
  Icon, Image, Message, Segment, Header, Grid, Label,
} from 'semantic-ui-react';
import ImagePlaceHolder from '../assets/images/image-placeholder.png';
import SpecLog from './spec-log';
import { Catalogue } from '../__generated__/backend-api-client';

interface CatalogueErrorProps {
  catalogue: Catalogue;
}

const CatalogueError: FunctionComponent<CatalogueErrorProps> = ({ catalogue }) => (
  <Message icon negative data-testid="catalogue-details-error-message">
    <Icon name="warning sign" />
    <Message.Content>
      <Message.Header>
        An error occurred while parsing the catalogue manifest file
        {' '}
        <a href={catalogue.htmlUrl} target="_blank" rel="noopener noreferrer">{catalogue.fullPath}</a>
      </Message.Header>
      {catalogue.parseError}
    </Message.Content>
  </Message>
);

interface CatalogueDetailsProps {
  catalogue: Catalogue;
}

const CatalogueDetails: FunctionComponent<CatalogueDetailsProps> = ({ catalogue }) => (
  <div data-testid="catalogue-details-container">
    <Header as="h1" textAlign="center">{catalogue.title}</Header>
    <Header as="h3" attached="top">
      <Icon name="info" />
      Catalogue Details
    </Header>
    <Segment attached data-testid="catalogue-details-segment">
      <Grid divided>
        <Grid.Row>
          <Grid.Column width={13}>
            <p>{catalogue.description}</p>
          </Grid.Column>
          <Grid.Column width={3}>
            <Image src={ImagePlaceHolder} />
          </Grid.Column>
        </Grid.Row>
      </Grid>
      <Label as="a" href={catalogue.htmlUrl} target="_blank">
        <Icon name="github" />
        {catalogue.fullPath}
      </Label>
    </Segment>
    <Header as="h3" attached="top">
      <Icon name="list" />
      Interfaces
    </Header>
    <Segment attached data-testid="catalogue-details-interface-list">
      {catalogue.specLogs.map((specLog) => (<SpecLog key={specLog.interfaceName} specLog={specLog} />))}
    </Segment>
  </div>
);

interface CatalogueDetailsContainerProps {
  catalogue: Catalogue;
}

const CatalogueDetailsContainer: FunctionComponent<CatalogueDetailsContainerProps> = ({ catalogue }) => {
  if (catalogue.parseError) return (<CatalogueError catalogue={catalogue} />);

  return (
    <CatalogueDetails catalogue={catalogue} />
  );
};

export default CatalogueDetailsContainer;

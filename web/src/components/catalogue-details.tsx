import React, { FunctionComponent } from 'react';
import {
  Icon, Message, Header, Item, Label,
} from 'semantic-ui-react';
import InterfaceListItem from './interface-list-item';
import { Catalogue } from '../backend-api-client';

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
  installationId: number;
  catalogue: Catalogue;
}

const CatalogueDetails: FunctionComponent<CatalogueDetailsProps> = ({ installationId, catalogue }) => (
  <div data-testid="catalogue-details-container">
    <Header as="h2">{catalogue.title}</Header>
    <div style={{ paddingBottom: '1em' }}>
      {catalogue.topics && catalogue.topics.map((topic) => (<Label key={topic} color="grey">{topic}</Label>))}
    </div>
    <p>{catalogue.description}</p>
    <Header as="h3">Interface List</Header>
    <Item.Group divided data-testid="catalogue-details-interface-list">
      {catalogue.specEvolutionSummaries.map((specEvolutionSummary) => (
        <InterfaceListItem
          key={specEvolutionSummary.interfaceName}
          installationId={installationId}
          catalogueEncodedId={catalogue.encodedId}
          specEvolutionSummary={specEvolutionSummary}
        />
      ))}
    </Item.Group>
  </div>
);

interface CatalogueDetailsContainerProps {
  installationId: number;
  catalogue: Catalogue;
}

const CatalogueDetailsContainer: FunctionComponent<CatalogueDetailsContainerProps> = ({ installationId, catalogue }) => {
  if (catalogue.parseError) return (<CatalogueError catalogue={catalogue} />);

  return (
    <CatalogueDetails installationId={installationId} catalogue={catalogue} />
  );
};

export default CatalogueDetailsContainer;

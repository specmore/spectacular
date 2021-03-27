import React, { FunctionComponent } from 'react';
import {
  Icon, Message, Header, Item,
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
  catalogue: Catalogue;
}

const CatalogueDetails: FunctionComponent<CatalogueDetailsProps> = ({ catalogue }) => (
  <div data-testid="catalogue-details-container">
    <Header as="h2">{catalogue.title}</Header>
    <p>{catalogue.description}</p>
    <Header as="h3">Interface List</Header>
    <Item.Group divided data-testid="catalogue-details-interface-list">
      {catalogue.specEvolutionSummaries.map((specEvolutionSummary) => (
        <InterfaceListItem
          key={specEvolutionSummary.interfaceName}
          catalogueEncodedId={catalogue.encodedId}
          specEvolutionSummary={specEvolutionSummary}
        />
      ))}
    </Item.Group>
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

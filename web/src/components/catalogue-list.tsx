import React, { FunctionComponent } from 'react';
import {
  Item, Segment, Message, Header, Container, Placeholder,
} from 'semantic-ui-react';
import CatalogueListItem from './catalogue-list-item';
import { useFindCataloguesForUser, Catalogue } from '../backend-api-client';
import LocationBar from './location-bar';

const CatalogueListLoading = () => (
  <Placeholder>
    <Placeholder.Header image>
      <Placeholder.Line />
      <Placeholder.Line />
    </Placeholder.Header>
  </Placeholder>
);

interface CatalogueListErrorProps {
  errorMessage: string;
}

const CatalogueListError: FunctionComponent<CatalogueListErrorProps> = ({ errorMessage }) => (
  <Message negative>
    <Message.Header>{errorMessage}</Message.Header>
  </Message>
);

interface CatalogueListProps {
  catalogues: Catalogue[];
}

const CatalogueList: FunctionComponent<CatalogueListProps> = ({ catalogues }) => (
  <Item.Group divided data-testid="catalogue-list-item-group">
    {catalogues.map((catalogue) => (<CatalogueListItem key={catalogue.encodedId} catalogue={catalogue} />))}
  </Item.Group>
);

interface CatalogueListContainerProps {
  org: string;
}

const CatalogueListContainer: FunctionComponent<CatalogueListContainerProps> = ({ org }) => {
  console.log('CatalogueListContainer function');
  const findCataloguesForUser = useFindCataloguesForUser({ queryParams: { org } });
  const { data: findCataloguesResult, loading, error } = findCataloguesForUser;

  let content = null;
  if (loading) {
    content = (<CatalogueListLoading />);
  } else if (error) {
    content = (<CatalogueListError errorMessage={error.message} />);
  } else {
    content = (<CatalogueList catalogues={findCataloguesResult.catalogues} />);
  }

  return (
    <>
      <LocationBar installationOwner={org} />
      <Segment vertical>
        <Container text>
          <Header as="h3">Available Interface Catalogues</Header>
          {content}
        </Container>
      </Segment>
    </>
  );
};

export default CatalogueListContainer;

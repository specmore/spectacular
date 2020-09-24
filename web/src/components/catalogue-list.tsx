import React, { FunctionComponent } from 'react';
import {
  Dimmer, Loader, Item, Segment, Message, Header, Container, Placeholder,
} from 'semantic-ui-react';
import EmptyCatalogueItemImage from '../assets/images/empty-catalogue-item.png';
import ImagePlaceHolder from '../assets/images/image-placeholder.png';
import CatalogueListItem from './catalogue-list-item';
import { useFindCataloguesForUser, Catalogue } from '../backend-api-client';

const CatalogueListLoading = () => (
  <Segment vertical>
    <Container text>
      <Placeholder>
        <Placeholder.Header image>
          <Placeholder.Line />
          <Placeholder.Line />
        </Placeholder.Header>
      </Placeholder>
    </Container>
  </Segment>
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
  <Segment vertical>
    <Container text>
      <Header as="h3">Available Interface Catalogues</Header>
      <Item.Group divided data-testid="catalogue-list-item-group">
        {catalogues.map((catalogue) => (<CatalogueListItem key={catalogue.encodedId} catalogue={catalogue} />))}
      </Item.Group>
    </Container>
  </Segment>
);

interface CatalogueListContainerProps {
  org: string;
}

const CatalogueListContainer: FunctionComponent<CatalogueListContainerProps> = ({ org }) => {
  const findCataloguesForUser = useFindCataloguesForUser({ queryParams: { org } });
  const { data: findCataloguesResult, loading, error } = findCataloguesForUser;

  if (loading) return (<CatalogueListLoading />);

  if (error) return (<CatalogueListError errorMessage={error.message} />);

  return (<CatalogueList catalogues={findCataloguesResult.catalogues} />);
};

export default CatalogueListContainer;

import React from 'react';
import {
  Dimmer, Loader, Item, Segment, Message, Header,
} from 'semantic-ui-react';
import EmptyCatalogueItemImage from '../assets/images/empty-catalogue-item.png';
import ImagePlaceHolder from '../assets/images/image-placeholder.png';
import CatalogueListItem from './catalogue-list-item';
import { useFindCataloguesForUser } from '../__generated__/backend-api-client';

const CatalogueListLoading = () => (
  <Segment vertical>
    <Dimmer inverted active>
      <Loader content="Loading" />
    </Dimmer>
    <Item.Group divided data-testid="catalogue-list-placeholder-item-group">
      <Item>
        <Item.Image size="tiny" src={ImagePlaceHolder} />
        <img src={EmptyCatalogueItemImage} alt="placeholder" />
      </Item>
    </Item.Group>
  </Segment>
);

const CatalogueListError = ({ errorMessage }) => (
  <Message negative>
    <Message.Header>{errorMessage}</Message.Header>
  </Message>
);

const CatalogueList = ({ catalogues }) => (
  <Segment vertical>
    <Header as="h4">The following specification catalogues are available to you:</Header>
    <Item.Group divided data-testid="catalogue-list-item-group">
      {catalogues.map((catalogue) => (<CatalogueListItem key={catalogue.id.encoded} catalogue={catalogue} />))}
    </Item.Group>
  </Segment>
);

const CatalogueListContainer = ({ org }) => {
  const findCataloguesForUser = useFindCataloguesForUser({ queryParams: { org } });
  const { data: catalogues, loading, error } = findCataloguesForUser;

  if (loading) return (<CatalogueListLoading />);

  if (error) return (<CatalogueListError errorMessage={error.message} />);

  return (<CatalogueList catalogues={catalogues.catalogues} />);
};

export default CatalogueListContainer;

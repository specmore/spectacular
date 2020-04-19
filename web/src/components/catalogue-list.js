import React, { useState, useEffect } from 'react';
import {
  Dimmer, Loader, Item, Segment, Message, Header,
} from 'semantic-ui-react';
import { fetchCatalogues } from '../api-client';
import EmptyCatalogueItemImage from '../assets/images/empty-catalogue-item.png';
import ImagePlaceHolder from '../assets/images/image-placeholder.png';
import CatalogueListItem from './catalogue-list-item';

const CatalogueListLoading = () => (
  <Segment vertical>
    <Dimmer inverted active>
      <Loader content="Loading" />
    </Dimmer>
    <Item.Group divided data-testid="catalogue-list-placeholder-item-group">
      <Item>
        <Item.Image size="tiny" src={ImagePlaceHolder} />
        <img src={EmptyCatalogueItemImage} />
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
      {catalogues.map((catalogue, index) => (<CatalogueListItem key={index} catalogue={catalogue} />))}
    </Item.Group>
  </Segment>
);

const CatalogueListContainer = ({ org }) => {
  const [catalogues, setCatalogues] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);

  const fetchCatalogueData = async (org) => {
    try {
      const cataloguesData = await fetchCatalogues(org);
      setCatalogues(cataloguesData.catalogues);
    } catch (error) {
      // console.error(error);
      setErrorMessage('An error occurred while fetching catalogues.');
    }
  };

  useEffect(() => {
    fetchCatalogueData(org);
  }, [org]);

  if (!catalogues && !errorMessage) return (<CatalogueListLoading />);

  if (errorMessage) return (<CatalogueListError errorMessage={errorMessage} />);

  return (<CatalogueList catalogues={catalogues} />);
};

export default CatalogueListContainer;

import React, { useState, useEffect } from "react";
import { Dimmer, Label, Loader, Icon, Item, Segment, Message, Header } from 'semantic-ui-react'
import { fetchCatalogues } from '../api-client';
import EmptyCatalogueItemImage from '../assets/images/empty-catalogue-item.png';
import ImagePlaceHolder from '../assets/images/image-placeholder.png';
import { Link } from "react-router-dom";


const CatalogueItem = ({repository, catalogueManifest, error}) => {
  if (error) {
    return (
      <Item>
        <Item.Content>
          <Item.Description>
            <Message icon negative>
              <Icon name='warning sign' />
              <Message.Content>
                <Message.Header>An error occurred while parsing the catalogue manifest file in <a href={repository.htmlUrl} target='_blank'>{repository.nameWithOwner}</a></Message.Header>
                {error}
              </Message.Content>
            </Message>
          </Item.Description>
        </Item.Content>
      </Item>
    );
  }

  return (
    <Item>
      <Item.Image size='tiny' src={ImagePlaceHolder} />
      <Item.Content>
        <Item.Header as={Link} to={repository.nameWithOwner}>{catalogueManifest.name}</Item.Header>
        <Item.Meta><a href={repository.htmlUrl} target='_blank'><Icon name="github"/> {repository.nameWithOwner}</a></Item.Meta>
        <Item.Description>
          {catalogueManifest.description}
        </Item.Description>
        <Item.Extra>
          <Label color='teal'>
            <Icon name='file alternate' />{catalogueManifest["spec-files"].length} specs
          </Label>
        </Item.Extra>
      </Item.Content>
    </Item>
  );
};

const CatalogueList = ({org}) => {
  const [catalogues, setCatalogues] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);

  const fetchCatalogueData = async (org) => {
    try {
      const cataloguesData = await fetchCatalogues(org);
      setCatalogues(cataloguesData.catalogues);
    } catch (error) {
      console.error(error);
      setErrorMessage("An error occurred while fetching catalogues.");
    }
  }

  useEffect(() => {
    fetchCatalogueData(org);
  }, [org])

  if (!catalogues && !errorMessage) {
    return (
      <React.Fragment>
        <Dimmer inverted active>
          <Loader content='Loading' />
        </Dimmer>
        <Item.Group divided>
          <Item>
            <Item.Image size='tiny' src={ImagePlaceHolder} />
            <img src={EmptyCatalogueItemImage} />
          </Item>
        </Item.Group>
      </React.Fragment>
    );
  }

  if (errorMessage) {
    return (
      <Message negative>
        <Message.Header>{errorMessage}</Message.Header>
      </Message>
    );
  }

  return (
    <Segment vertical>
      <Header as='h4'>The following specification catalogues are available to you:</Header>
      <Item.Group divided>
        {catalogues.map((catalogue, index) => (<CatalogueItem key={index} {...catalogue} />))}
      </Item.Group>
    </Segment>
  );
};

export default CatalogueList;
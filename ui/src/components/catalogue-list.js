import React, { useState, useEffect } from "react";
import { Dimmer, Loader, Item, Segment, Message, Header } from 'semantic-ui-react'
import { fetchCatalogues } from '../api-client';
import EmptyCatalogueItemImage from '../assets/images/empty-catalogue-item.png';
import ImagePlaceHolder from '../assets/images/image-placeholder.png';


const CatalogueItem = ({repository, catalogueManifest}) => (
  <Item>
    <Item.Image size='tiny' src={ImagePlaceHolder} />
    <Item.Content>
      <Item.Header as='a'>{catalogueManifest.name}</Item.Header>
      <Item.Meta>{repository.name}</Item.Meta>
      <Item.Description>
        {catalogueManifest.description}
      </Item.Description>
      <Item.Extra></Item.Extra>
    </Item.Content>
  </Item>
);

const CatalogueList = ({org}) => {
  const [catalogues, setCatalogues] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);

  const fetchCatalogueData = async (org) => {
    try {
      const cataloguesData = await fetchCatalogues(org);
      // let cataloguesData = {
      //   catalogues: [{
      //     repository: {
      //       name: "test-owner/repo1",
      //       repo_url: "http://github.com/test-owner/repo1",
      //       repo_image_url: "",
      //     },
      //     catalogue_manifest: {
      //       name: "Test Catalogue 1",
      //       description: "A test catalogue of interface specifications for a systems",
      //       spec_files: [],
      //       errors: []
      //     },
      //   }, {
      //     repository: {
      //       name: "test-owner/repo2",
      //       repo_url: "http://github.com/test-owner/repo2",
      //       repo_image_url: "",
      //     },
      //     catalogue_manifest: {
      //       name: "Test Catalogue 2",
      //       description: "A test catalogue of interface specifications for a department and all its systems",
      //       spec_files: [],
      //       errors: []
      //     },
      //   }]
      // };
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
        <img src={EmptyCatalogueItemImage} />
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
    <Segment>
      <Header>The following specification catalogues are available to you:</Header>
      <Item.Group>
        {catalogues.map((catalogue, index) => (<CatalogueItem key={index} {...catalogue} />))}
      </Item.Group>
    </Segment>
  );
};

export default CatalogueList;
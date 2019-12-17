import React, { useState, useEffect } from "react";
import { Dimmer, Loader, Item, Segment, Message } from 'semantic-ui-react'
import { fetchCatalogueListForInstallationConfig } from '../api-client';
import { sleep } from '../utils';
import EmptyCatalogueItemImage from '../assets/images/empty-catalogue-item.png';


const CatalogueItem = ({name, repo}) => (
  <Item>
    <Item.Content>
      <Item.Header>{name}</Item.Header>
      <Item.Description>{repo}</Item.Description>
    </Item.Content>
  </Item>
);

const CatalogueList = ({installationId, configRepo}) => {
  const [catalogues, setCatalogues] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);

  const fetchCatalogueData = async (installationId, configRepo) => {
    try {
      const cataloguesData = await fetchCatalogueListForInstallationConfig(installationId, configRepo);
      setCatalogues(cataloguesData.catalogues);
    } catch (error) {
      console.error(error);
      setErrorMessage("An error occurred while fetching catalogues.");
    }
  }

  useEffect(() => {
    fetchCatalogueData(installationId, configRepo);
  }, [installationId, configRepo])

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
    <Item.Group>
      {catalogues.map((catalogue, index) => (<CatalogueItem key={index} {...catalogue} />))}
    </Item.Group>
  );
};

export default CatalogueList;
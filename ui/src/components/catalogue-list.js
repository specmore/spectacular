import React, { useState, useEffect } from "react";
import { Dimmer, Loader, Item, Segment } from 'semantic-ui-react'
import {fetchCatalogueListForInstallationConfig} from '../api-client';


const CatalogueItem = ({name, repo}) => (
  <Item>
    <Item.Content>
      <Item.Header>{name}</Item.Header>
      <Item.Meta>...</Item.Meta>
      <Item.Description>
          ...
      </Item.Description>
      <Item.Extra>{repo}</Item.Extra>
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
      <Segment>
        (<Dimmer inverted active>
          <Loader content='Loading' />
        </Dimmer>
      </Segment>
    );
  }

  return (
    <Segment>      
      <Item.Group>
        {catalogues.map(catalogue => (<CatalogueItem key={catalogue.repo} {...catalogue} />))}
      </Item.Group>
    </Segment>
  );
};

export default CatalogueList;
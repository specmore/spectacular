import React, { useState, useEffect } from "react";
import { Dimmer, Loader, Item, Segment, Message } from 'semantic-ui-react'
import { fetchInstances } from '../api-client';
import EmptyInstanceItemImage from '../assets/images/empty-catalogue-item.png';


const InstanceItem = ({instanceConfigManifest, repository}) => (
  <Item>
    <Item.Content>
      <Item.Header>{instanceConfigManifest.name}</Item.Header>
      <Item.Description>{repository.nameWithOwner}</Item.Description>
    </Item.Content>
  </Item>
);

const InstanceList = () => {
  const [instances, setInstances] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);

  const fetchInstanceData = async () => {
    try {
      const instancesData = await fetchInstances();
      setInstances(instancesData.instances);
    } catch (error) {
      //console.error(error);
      setErrorMessage("An error occurred while fetching instances.");
    }
  }

  useEffect(() => {
    fetchInstanceData();
  }, [])

  if (!instances && !errorMessage) {
    return (
      <React.Fragment>
        <Dimmer inverted active>
          <Loader content='Loading' />
        </Dimmer>
        <img src={EmptyInstanceItemImage} />
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
      {instances.map((instance, index) => (<InstanceItem key={index} {...instance} />))}
    </Item.Group>
  );
};

export default InstanceList;
import React, { FunctionComponent, useState, useEffect } from 'react';
import {
  Dimmer, Loader, Message, Container,
} from 'semantic-ui-react';
import { fetchInstallation } from '../api-client';
import EmptyWelcomeItemImage from '../assets/images/empty-catalogue-item.png';
import LocationBar from './location-bar';

const InstallationContainer: FunctionComponent = () => {
  const [installation, setInstallation] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);

  const fetchInstallationData = async () => {
    try {
      const installationData = await fetchInstallation();
      setInstallation(installationData);
    } catch (error) {
      // console.error(error);
      setErrorMessage('An error occurred while fetching installation details.');
    }
  };

  useEffect(() => {
    fetchInstallationData();
  }, []);

  if (!installation && !errorMessage) {
    return (
      <Container text>
        <Dimmer inverted active>
          <Loader content="Loading" />
        </Dimmer>
        <img src={EmptyWelcomeItemImage} alt="placeholder" />
      </Container>
    );
  }

  if (errorMessage) {
    return (
      <Container text style={{ paddingTop: '4em' }}>
        <Message negative>
          <Message.Header>{errorMessage}</Message.Header>
        </Message>
      </Container>
    );
  }

  return (
    <LocationBar installationOwner={installation.owner} />
  );
};

export default InstallationContainer;

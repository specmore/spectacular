import React, { FunctionComponent, useState, useEffect } from 'react';
import {
  Dimmer, Loader, Message, Container,
} from 'semantic-ui-react';
import { Switch, Route } from 'react-router-dom';
import { fetchInstallation } from '../api-client';
import EmptyWelcomeItemImage from '../assets/images/empty-catalogue-item.png';
import CatalogueContainer from './catalogue-container';
import CatalogueList from './catalogue-list';
import NotFound from './not-found';
import { CATALOGUE_LIST_ROUTE, CATALOGUE_CONTAINER_ROUTE, CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE } from '../routes';

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
    <>
      <Switch>
        <Route exact path={CATALOGUE_LIST_ROUTE}>
          <CatalogueList org={installation.owner} />
        </Route>
        <Route exact path={[CATALOGUE_CONTAINER_ROUTE, CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE]}>
          <CatalogueContainer />
        </Route>
        <Route path="*">
          <NotFound />
        </Route>
      </Switch>
    </>
  );
};

export default InstallationContainer;

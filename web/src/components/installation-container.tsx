import React, { FunctionComponent } from 'react';
import {
  Dimmer, Loader, Message, Container,
} from 'semantic-ui-react';
import { Switch, Route } from 'react-router-dom';
import EmptyWelcomeItemImage from '../assets/images/empty-catalogue-item.png';
import CatalogueContainer from './catalogue-container';
import CatalogueList from './catalogue-list';
import NotFound from './not-found';
import { CATALOGUE_LIST_ROUTE, CATALOGUE_CONTAINER_ROUTE, CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE } from '../routes';
import InterfaceContainer from './interface-container';
import { useGetInstallation } from '../backend-api-client';

const InstallationContainerLoading: FunctionComponent = () => (
  <Container text>
    <Dimmer inverted active>
      <Loader content="Loading" />
    </Dimmer>
    <img src={EmptyWelcomeItemImage} alt="placeholder" />
  </Container>
);

interface InstallationContainerErrorProps {
  errorMessage: string;
}

const InstallationContainerError: FunctionComponent<InstallationContainerErrorProps> = ({ errorMessage }) => (
  <Container text style={{ paddingTop: '4em' }}>
    <Message negative>
      <Message.Header>{errorMessage}</Message.Header>
    </Message>
  </Container>
);

const InstallationContainer: FunctionComponent = () => {
  const getInstallation = useGetInstallation({});
  const { data: getInstallationResult, loading, error } = getInstallation;

  if (loading) {
    return (<InstallationContainerLoading />);
  }

  if (error) {
    return (<InstallationContainerError errorMessage={error.message} />);
  }

  return (
    <>
      <Switch>
        <Route exact path={CATALOGUE_LIST_ROUTE}>
          <CatalogueList org={getInstallationResult.owner} />
        </Route>
        <Route exact path={[CATALOGUE_CONTAINER_ROUTE]}>
          <CatalogueContainer org={getInstallationResult.owner} />
        </Route>
        <Route exact path={[CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE]}>
          <InterfaceContainer org={getInstallationResult.owner} />
        </Route>
        <Route path="*">
          <NotFound />
        </Route>
      </Switch>
    </>
  );
};

export default InstallationContainer;

import React, { FunctionComponent } from 'react';
import {
  Dimmer, Loader, Message, Container,
} from 'semantic-ui-react';
import { Switch, Route, useParams } from 'react-router-dom';
import EmptyWelcomeItemImage from '../assets/images/empty-catalogue-item.png';
import CatalogueContainer from './catalogue-container';
import CatalogueList from './catalogue-list/catalogue-list';
import { CATALOGUE_CONTAINER_ROUTE, CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE, INSTALLATION_CONTAINER_ROUTE } from '../routes';
import InterfaceContainer from './interface-container';
import { useGetInstallation } from '../backend-api-client';
import NotFound from './not-found';

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
  const { installationId } = useParams();
  const getInstallation = useGetInstallation({ installationId });
  const { data: getInstallationResult, loading, error } = getInstallation;

  if (loading) {
    return (<InstallationContainerLoading />);
  }

  if (error) {
    return (<InstallationContainerError errorMessage={error.message} />);
  }

  return (
    <Switch>
      <Route exact path={INSTALLATION_CONTAINER_ROUTE}>
        <CatalogueList
          installationId={getInstallationResult.id}
          org={getInstallationResult.owner}
        />
      </Route>
      <Route exact path={[CATALOGUE_CONTAINER_ROUTE]}>
        <CatalogueContainer
          installationId={getInstallationResult.id}
          org={getInstallationResult.owner}
        />
      </Route>
      <Route exact path={[CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE]}>
        <InterfaceContainer
          installationId={getInstallationResult.id}
          org={getInstallationResult.owner}
        />
      </Route>
      <Route path="*">
        <NotFound />
      </Route>
    </Switch>
  );
};

export default InstallationContainer;

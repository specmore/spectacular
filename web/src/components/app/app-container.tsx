import React, { FunctionComponent } from 'react';
import {
  Dimmer, Loader, Message, Container,
} from 'semantic-ui-react';
import {
  Switch,
  Route,
  useParams,
  useHistory,
  Redirect,
} from 'react-router-dom';
import EmptyWelcomeItemImage from '../../assets/images/empty-catalogue-item.png';
import InstallationSelectionContainer from '../installation-selection-container';
import InstallationContainer from '../installation-container';
import MenuBar from '../menu-bar';
import FooterBar from '../footer-bar';
import GitHubLogin from '../login/github-login';
import {
  GITHUB_LOGIN_ROUTE,
  INSTALLATION_CONTAINER_ROUTE,
  INSTALLATION_LIST_ROUTE,
  OLD_V2_CATALOGUE_CONTAINER_ROUTE,
  OldV2CatalogueContainerWithSpecLocationRouteParams,
  OLD_V2_CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE,
  CreateCatalogueContainerLocation,
  CreateInterfaceLocation,
} from '../../routes';
import { useGetAppDetails, useGetInstallations } from '../../backend-api-client';
import NotFound from '../not-found';

const AppContainerLoading: FunctionComponent = () => (
  <Container text style={{ paddingTop: '5em' }}>
    <Dimmer inverted active>
      <Loader content="Loading" />
    </Dimmer>
    <img src={EmptyWelcomeItemImage} alt="placeholder" />
  </Container>
);

interface AppContainerErrorProps {
  errorMessage: string;
}

const AppContainerError: FunctionComponent<AppContainerErrorProps> = ({ errorMessage }) => (
  <Container text style={{ paddingTop: '4em' }}>
    <Message negative>
      <Message.Header>{errorMessage}</Message.Header>
    </Message>
  </Container>
);

const ResolveInstallationContainer: FunctionComponent = () => {
  const { encodedId, interfaceName } = useParams<OldV2CatalogueContainerWithSpecLocationRouteParams>();
  const getInstallations = useGetInstallations({ queryParams: { catalogueEncodedId: encodedId } });
  const { data: getInstallationsResult, loading, error } = getInstallations;
  const history = useHistory();

  if (loading) {
    return (<AppContainerLoading />);
  }

  if (error) {
    return (<AppContainerError errorMessage={error.message} />);
  }

  if (getInstallationsResult.installations.length > 0) {
    const installationId = getInstallationsResult.installations[0].id;
    let location = CreateCatalogueContainerLocation(installationId, encodedId);
    if (interfaceName) {
      location = CreateInterfaceLocation(installationId, encodedId, interfaceName);
    }
    return (<Redirect to={location + history.location.search} />);
  }

  return (<AppContainerError errorMessage="The URL you have used is no longer support in this version." />);
};

const AppContainer: FunctionComponent = () => {
  const getAppDetails = useGetAppDetails({});
  const { data: getAppDetailsResult, loading, error } = getAppDetails;

  if (loading) {
    return (<AppContainerLoading />);
  }

  if (error) {
    return (<AppContainerError errorMessage={error.message} />);
  }

  return (
    <Switch>
      <Route exact path={GITHUB_LOGIN_ROUTE}>
        <GitHubLogin clientId={getAppDetailsResult.clientId} />
      </Route>
      <Route path="*">
        <div className="content-container">
          <MenuBar />
          <div className="main-content" data-testid="app-container">
            <Switch>
              <Route exact path={[OLD_V2_CATALOGUE_CONTAINER_ROUTE, OLD_V2_CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE]}>
                <ResolveInstallationContainer />
              </Route>
              <Route exact path={INSTALLATION_LIST_ROUTE}>
                <InstallationSelectionContainer />
              </Route>
              <Route path={INSTALLATION_CONTAINER_ROUTE}>
                <InstallationContainer />
              </Route>
              <Route path="*">
                <NotFound />
              </Route>
            </Switch>
          </div>
          <FooterBar />
        </div>
      </Route>
    </Switch>
  );
};

export default AppContainer;

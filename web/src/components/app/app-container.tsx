import React, { FunctionComponent } from 'react';
import {
  Dimmer, Loader, Message, Container,
} from 'semantic-ui-react';
import { Switch, Route } from 'react-router-dom';
import EmptyWelcomeItemImage from '../../assets/images/empty-catalogue-item.png';
import InstallationContainer from '../installation-container';
import MenuBar from '../menu-bar';
import FooterBar from '../footer-bar';
import GitHubLogin from '../login/github-login';
import { GITHUB_LOGIN_ROUTE } from '../../routes';
import { useGetAppDetails } from '../../backend-api-client';

const AppContainerLoading: FunctionComponent = () => (
  <Container text>
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
          <div className="main-content">
            <InstallationContainer />
          </div>
          <FooterBar />
        </div>
      </Route>
    </Switch>
  );
};

export default AppContainer;

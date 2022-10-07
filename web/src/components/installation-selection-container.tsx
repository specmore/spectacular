import React, { FunctionComponent } from 'react';
import { Link } from 'react-router-dom';
import {
  Dimmer, Loader, Message, Container, Item, Header, Segment,
} from 'semantic-ui-react';
import EmptyWelcomeItemImage from '../assets/images/empty-catalogue-item.png';
import { Installation, useGetInstallations } from '../backend-api-client';
import { CreateInstallationContainerLocation } from '../routes';

interface InstallationItemProps {
  installation: Installation;
}

const InstallationItem: FunctionComponent<InstallationItemProps> = ({ installation }) => {
  const installationLocation = CreateInstallationContainerLocation(installation.id);
  return (
    <Item data-testid="installation-list-item-container">
      <Item.Image size="tiny" src={installation.ownerImageUrl} />
      <Item.Content verticalAlign="middle">
        <Item.Header as={Link} to={installationLocation}>
          {installation.owner}
        </Item.Header>
      </Item.Content>
    </Item>
  );
};

interface InstallationListProps {
  installations: Installation[];
}

const InstallationList: FunctionComponent<InstallationListProps> = ({ installations }) => (
  <div className="installation-list-container">
    <div className="installation-list">
      <Item.Group divided data-testid="installation-list-item-group">
        {installations.map((installation) => (<InstallationItem key={installation.id} installation={installation} />))}
      </Item.Group>
    </div>
  </div>
);

const InstallationSelectionContainerLoading: FunctionComponent = () => (
  <Container text>
    <Dimmer inverted active>
      <Loader content="Loading Organisations" />
    </Dimmer>
    <img src={EmptyWelcomeItemImage} alt="placeholder" />
  </Container>
);

interface InstallationSelectionContainerErrorProps {
  errorMessage: string;
}

const InstallationSelectionContainerError: FunctionComponent<InstallationSelectionContainerErrorProps> = ({ errorMessage }) => (
  <Container text style={{ paddingTop: '4em' }}>
    <Message negative>
      <Message.Header>{errorMessage}</Message.Header>
    </Message>
  </Container>
);

const InstallationSelectionContainer: FunctionComponent = () => {
  const getInstallations = useGetInstallations({});
  const { data: getInstallationsResult, loading, error } = getInstallations;

  if (loading) {
    return (<InstallationSelectionContainerLoading />);
  }

  if (error) {
    return (<InstallationSelectionContainerError errorMessage={error.message} />);
  }

  return (
    <Segment vertical data-testid="installation-selection-container-segment" style={{ paddingTop: '5em' }}>
      <Container text>
        <Header as="h2">Welcome to Spectacular</Header>
        <p>Please select an Organisation to start browsing interface specifications</p>
        <Header as="h3">Organisations</Header>
        <InstallationList installations={getInstallationsResult.installations} />
      </Container>
    </Segment>
  );
};

export default InstallationSelectionContainer;

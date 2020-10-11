import React, { FunctionComponent } from 'react';
import {
  Message, Segment, Container, Placeholder, Header,
} from 'semantic-ui-react';
import { useParams } from 'react-router-dom';
import { useGetCatalogue } from '../backend-api-client';
import LocationBar from './location-bar';
import InterfaceDetails from './interface-details';

const InterfaceContainerLoading = () => (
  <>
    <Header as="h2">Loading Interface..</Header>
    <Placeholder>
      <Placeholder.Line />
      <Placeholder.Line />
    </Placeholder>
  </>
);

interface InterfaceContainerErrorProps {
  errorMessage: string;
}

const InterfaceContainerError: FunctionComponent<InterfaceContainerErrorProps> = ({ errorMessage }) => (
  <Message negative>
    <Message.Header>{errorMessage}</Message.Header>
  </Message>
);

interface InterfaceContainerProps {
  org: string;
}

const InterfaceContainer: FunctionComponent<InterfaceContainerProps> = ({ org }) => {
  const { encodedId, interfaceName } = useParams();
  console.log('InterfaceContainer rerender');
  const getCatalogue = useGetCatalogue({ encodedId });
  const { data: getCatalogueResult, loading, error } = getCatalogue;
  console.log('getCatalogueResult', getCatalogueResult);

  let catalogueTitle = null;
  let content = null;
  if (loading) {
    content = (<InterfaceContainerLoading />);
  } else if (error) {
    content = (<InterfaceContainerError errorMessage={error.message} />);
  } else {
    const specLog = getCatalogueResult.catalogue.specLogs.find((specLogItem) => specLogItem.interfaceName === interfaceName);
    catalogueTitle = getCatalogueResult.catalogue.title;
    content = (<InterfaceDetails specLog={specLog} />);
  }

  return (
    <>
      <LocationBar installationOwner={org} catalogueTitle={catalogueTitle} />
      <Segment vertical data-testid="catalogue-container-segment">
        <Container text>
          {content}
        </Container>
      </Segment>
    </>
  );
};

export default InterfaceContainer;

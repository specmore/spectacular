import React, { FunctionComponent } from 'react';
import {
  Message, Segment, Container, Placeholder, Header,
} from 'semantic-ui-react';
import { useParams } from 'react-router-dom';
import SwaggerUI from 'swagger-ui-react';
import 'swagger-ui-react/swagger-ui.css';
import { useGetCatalogue } from '../backend-api-client';
import LocationBar from './location-bar';
import InterfaceDetails from './interface-details';
import { useQuery, CloseSpecButton } from '../routes';

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

const createInterfaceFileContentsPath = (
  encodedId: string,
  interfaceName: string,
  refName: string,
) => `/api/catalogues/${encodedId}/interfaces/${interfaceName}/file?ref=${refName}`;

const InterfaceContainer: FunctionComponent<InterfaceContainerProps> = ({ org }) => {
  const { encodedId, interfaceName } = useParams();
  const query = useQuery();
  const refName = query.get('ref');

  console.log('InterfaceContainer rerender');
  console.log('refName', refName);
  const getCatalogue = useGetCatalogue({ encodedId });
  const { data: getCatalogueResult, loading, error } = getCatalogue;
  console.log('getCatalogueResult', getCatalogueResult);

  let catalogueTitle = null;
  let interfaceTitle = null;
  let content = null;
  let specPreview = null;
  if (loading) {
    content = (<InterfaceContainerLoading />);
  } else if (error) {
    content = (<InterfaceContainerError errorMessage={error.message} />);
  } else {
    const specLog = getCatalogueResult.catalogue.specLogs.find((specLogItem) => specLogItem.interfaceName === interfaceName);
    catalogueTitle = getCatalogueResult.catalogue.title;
    interfaceTitle = specLog.latestAgreed.parseResult.openApiSpec.title;
    content = (<InterfaceDetails specLog={specLog} interfaceName={interfaceName} />);

    if (refName) {
      const interfaceFileContentsPath = createInterfaceFileContentsPath(encodedId, interfaceName, query.get('ref'));
      specPreview = (
        <div data-testid="interface-container-swagger-ui">
          <CloseSpecButton />
          <SwaggerUI url={interfaceFileContentsPath} docExpansion="list" />
        </div>
      );
    }
  }

  return (
    <>
      <LocationBar installationOwner={org} catalogueTitle={catalogueTitle} catalogueEncodedId={encodedId} interfaceTitle={interfaceTitle} />
      <Segment vertical data-testid="interface-container-segment">
        <Container text>
          {content}
        </Container>
        {specPreview}
      </Segment>
    </>
  );
};

export default InterfaceContainer;

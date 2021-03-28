import React, { FunctionComponent } from 'react';
import {
  Message, Segment, Container, Placeholder, Header,
} from 'semantic-ui-react';
import { useParams } from 'react-router-dom';
import SwaggerUI from 'swagger-ui-react';
import 'swagger-ui-react/swagger-ui.css';
import { useGetInterfaceDetails } from '../backend-api-client';
import LocationBar from './location-bar';
import InterfaceDetails from './interface-details';
import { CloseSpecButton, getCurrentSpecRefViewed, isShowSpecEvolution } from '../routes';
import SpecEvolutionContainer from './spec-evolution/spec-evolution-container';

const InterfaceContainerLoading = () => (
  <>
    <Header as="h2">Loading Interface..</Header>
    <Placeholder data-testid="interface-container-placeholder">
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
  const refName = getCurrentSpecRefViewed();
  const showSpecEvolution = isShowSpecEvolution();

  const getInterfaceDetails = useGetInterfaceDetails({ encodedId, interfaceName });
  const { data: getInterfaceResult, loading, error } = getInterfaceDetails;

  let catalogue = null;
  let content = null;
  let specPreview = null;
  let specEvolution = null;
  if (loading) {
    content = (<InterfaceContainerLoading />);
  } else if (error) {
    content = (<InterfaceContainerError errorMessage={error.message} />);
  } else {
    catalogue = getInterfaceResult.catalogue;
    content = (<InterfaceDetails specEvolutionSummary={getInterfaceResult.specEvolutionSummary} />);

    if (refName) {
      const interfaceFileContentsPath = createInterfaceFileContentsPath(encodedId, interfaceName, refName);
      specPreview = (
        <div data-testid="interface-container-swagger-ui">
          <CloseSpecButton />
          <SwaggerUI url={interfaceFileContentsPath} docExpansion="list" />
        </div>
      );
    }

    if (showSpecEvolution) {
      specEvolution = (
        <Segment vertical>
          <Container text>
            <SpecEvolutionContainer specEvolution={getInterfaceResult.specEvolution} />
          </Container>
        </Segment>
      );
    }
  }

  return (
    <>
      <LocationBar installationOwner={org} catalogue={catalogue} />
      <div data-testid="interface-container-segment">
        <Segment vertical>
          <Container text>
            {content}
          </Container>
        </Segment>
        {specEvolution}
        {specPreview}
      </div>
    </>
  );
};

export default InterfaceContainer;

import React, { FunctionComponent } from 'react';
import {
  Dimmer, Loader, Message, Segment, Container,
} from 'semantic-ui-react';
import { useParams } from 'react-router-dom';
import SwaggerUI from 'swagger-ui-react';
import EmptyItemImage from '../assets/images/empty-catalogue-item.png';
import CatalogueDetails from './catalogue-details';
import 'swagger-ui-react/swagger-ui.css';
import './catalogue-container.css';
import { CloseSpecButton, BackToCatalogueListLinkButton, useQuery } from '../routes';
import { useGetCatalogue, Catalogue } from '../backend-api-client';

const CatalogueContainerLoading = () => (
  <Segment vertical textAlign="center">
    <Dimmer inverted active>
      <Loader content="Loading catalogue.." />
    </Dimmer>
    <img src={EmptyItemImage} data-testid="catalogue-container-placeholder-image" alt="placeholder" />
  </Segment>
);

interface CatalogueContainerErrorProps {
  errorMessage: string;
}

const CatalogueContainerError: FunctionComponent<CatalogueContainerErrorProps> = ({ errorMessage }) => (
  <Message negative>
    <Message.Header>{errorMessage}</Message.Header>
  </Message>
);

interface CatalogueContainerSegmentProps {
  catalogue: Catalogue;
}

const CatalogueContainerSegment: FunctionComponent<CatalogueContainerSegmentProps> = ({ catalogue }) => (
  <div data-testid="catalogue-container-segment" style={{ marginBottom: '10px' }}>
    <CatalogueDetails catalogue={catalogue} />
  </div>
);

const createInterfaceFileContentsPath = (
  encodedId: string,
  interfaceName: string,
  refName: string,
) => `/api/catalogues/${encodedId}/interfaces/${interfaceName}/file?ref=${refName}`;

const CatalogueContainer: FunctionComponent = () => {
  const { encodedId, interfaceName } = useParams();
  const query = useQuery();
  const getCatalogue = useGetCatalogue({ encodedId });
  const { data: getCatalogueResult, loading, error } = getCatalogue;

  const interfaceFileContentsPath = createInterfaceFileContentsPath(encodedId, interfaceName, query.get('ref'));

  if (loading) return (<CatalogueContainerLoading />);

  if (error) {
    return (
      <Container text>
        <BackToCatalogueListLinkButton />
        <CatalogueContainerError errorMessage={error.message} />
      </Container>
    );
  }

  if (!interfaceName) {
    return (
      <Container text>
        <BackToCatalogueListLinkButton />
        <CatalogueContainerSegment catalogue={getCatalogueResult.catalogue} />
      </Container>
    );
  }

  return (
    <div className="catalogue-container side-by-side-container">
      <Container text className="side-by-side-column">
        <BackToCatalogueListLinkButton />
        <CatalogueContainerSegment catalogue={getCatalogueResult.catalogue} />
      </Container>
      <div className="side-by-side-column" data-testid="catalogue-container-swagger-ui">
        <CloseSpecButton />
        <SwaggerUI url={interfaceFileContentsPath} />
      </div>
    </div>
  );
};

export default CatalogueContainer;

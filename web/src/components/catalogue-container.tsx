import React, { FunctionComponent } from 'react';
import {
  Message, Segment, Container, Placeholder, Header,
} from 'semantic-ui-react';
import { useParams } from 'react-router-dom';
import SwaggerUI from 'swagger-ui-react';
import CatalogueDetails from './catalogue-details';
import 'swagger-ui-react/swagger-ui.css';
import './catalogue-container.css';
import { CloseSpecButton, BackToCatalogueListLinkButton, useQuery } from '../routes';
import { useGetCatalogue, Catalogue } from '../backend-api-client';
import LocationBar from './location-bar';

const CatalogueContainerLoading = () => (
  <>
    <Header as="h3">Loading Catalogue</Header>
    <Placeholder>
      <Placeholder.Line />
      <Placeholder.Line />
    </Placeholder>
  </>
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

interface CatalogueContainerProps {
  org: string;
}

const CatalogueContainer: FunctionComponent<CatalogueContainerProps> = ({ org }) => {
  const { encodedId } = useParams();
  // const query = useQuery();
  const getCatalogue = useGetCatalogue({ encodedId });
  const { data: getCatalogueResult, loading, error } = getCatalogue;

  // const interfaceFileContentsPath = createInterfaceFileContentsPath(encodedId, interfaceName, query.get('ref'));

  let content = null;
  if (loading) {
    content = (<CatalogueContainerLoading />);
  } else if (error) {
    content = (<CatalogueContainerError errorMessage={error.message} />);
  } else {
    content = (<CatalogueDetails catalogue={getCatalogueResult.catalogue} />);
  }

  const catalogue = getCatalogueResult ? getCatalogueResult.catalogue : null;

  return (
    <>
      <LocationBar installationOwner={org} catalogue={catalogue} />
      <Segment vertical>
        <Container text>
          {content}
        </Container>
      </Segment>
    </>
  );

  // if (loading) return (<CatalogueContainerLoading />);

  // if (error) {
  //   return (
  //     <Container text>
  //       <BackToCatalogueListLinkButton />
  //       <CatalogueContainerError errorMessage={error.message} />
  //     </Container>
  //   );
  // }

  // if (!interfaceName) {
  //   return (
  //     <Container text>
  //       <CatalogueContainerSegment catalogue={getCatalogueResult.catalogue} />
  //     </Container>
  //   );
  // }

  // // return (
  // //   <div className="catalogue-container side-by-side-container">
  // //     <Container text className="side-by-side-column">
  // //       <BackToCatalogueListLinkButton />
  // //       <CatalogueContainerSegment catalogue={getCatalogueResult.catalogue} />
  // //     </Container>
  // //     <div className="side-by-side-column" data-testid="catalogue-container-swagger-ui">
  // //       <CloseSpecButton />
  // //       <SwaggerUI url={interfaceFileContentsPath} docExpansion="list" />
  // //     </div>
  // //   </div>
  // // );
};

export default CatalogueContainer;

import React, { FunctionComponent } from 'react';
import {
  Message, Segment, Container, Placeholder, Header,
} from 'semantic-ui-react';
import { useParams } from 'react-router-dom';
import CatalogueDetails from './catalogue-details';
import { useGetCatalogue, Catalogue } from '../backend-api-client';
import LocationBar from './location-bar';

const CatalogueContainerLoading = () => (
  <>
    <Header as="h2">Loading Catalogue..</Header>
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

interface CatalogueContainerProps {
  org: string;
}

const CatalogueContainer: FunctionComponent<CatalogueContainerProps> = ({ org }) => {
  const { encodedId } = useParams();
  const getCatalogue = useGetCatalogue({ encodedId });
  const { data: getCatalogueResult, loading, error } = getCatalogue;

  let content = null;
  if (loading) {
    content = (<CatalogueContainerLoading />);
  } else if (error) {
    content = (<CatalogueContainerError errorMessage={error.message} />);
  } else {
    content = (<CatalogueDetails catalogue={getCatalogueResult.catalogue} />);
  }

  const catalogueTitle = getCatalogueResult ? getCatalogueResult.catalogue.title : null;

  return (
    <>
      <LocationBar installationOwner={org} catalogueTitle={catalogueTitle} />
      <Segment vertical>
        <Container text>
          {content}
        </Container>
      </Segment>
    </>
  );
};

export default CatalogueContainer;

import React, { FunctionComponent } from 'react';
import { useParams } from 'react-router-dom';
import LocationBar from './location-bar';
import { useFindCataloguesForUser, useGetCatalogue, Catalogue } from '../backend-api-client';

interface CatalogueListContainerProps {
  org: string
}

const CatalogueListContainer: FunctionComponent<CatalogueListContainerProps> = ({ org }) => {
  const { encodedId } = useParams();
  const findCataloguesForUser = useFindCataloguesForUser({ queryParams: { org } });
  const { data: findCataloguesResult, loading: cataloguesLoading, error: cataloguesError } = findCataloguesForUser;
  const getCatalogue = useGetCatalogue({ encodedId });
  const { data: getCatalogueResult, loading: catalogueLoading, error: catalogueError } = getCatalogue;

  return (
    <>
      <LocationBar installationOwner={org} />
    </>
  );
};

export default CatalogueListContainer;

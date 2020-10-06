import React, { FunctionComponent, useState } from 'react';
import { useParams } from 'react-router-dom';
import CatalogueContainer from './catalogue-container';
import CatalogueList from './catalogue-list';
import LocationBar from './location-bar';
import { useFindCataloguesForUser, useGetCatalogue, Catalogue } from '../backend-api-client';

interface CatalogueListContainerProps {
  org: string
}

const CatalogueListContainer: FunctionComponent<CatalogueListContainerProps> = ({ org }) => {
  const { encodedId } = useParams();
  const findCataloguesForUser = useFindCataloguesForUser({ queryParams: { org }, lazy: true });
  const getCatalogue = useGetCatalogue({ encodedId, lazy: true });

  console.log(`CatalogueListContainer function, encodedId:${encodedId}`);

  // useEffect(() => {
  //   console.log(`CatalogueListContainer useEffect, encodedId:${encodedId}`);
  // }, []);

  if (!encodedId) {
    const {
      data: findCataloguesResult,
      loading: cataloguesLoading,
      error: cataloguesError,
      refetch: cataloguesRefetch,
    } = findCataloguesForUser;

    if (!findCataloguesResult && !cataloguesLoading && !cataloguesError) {
      cataloguesRefetch();
    }

    return (
      <>
        <LocationBar installationOwner={org} />
        <CatalogueList findCataloguesResult={findCataloguesResult} loading={cataloguesLoading} error={cataloguesError} />
      </>
    );
  }

  const {
    data: getCatalogueResult,
    loading: catalogueLoading,
    error: catalogueError,
    refetch: catalogueRefetch,
  } = getCatalogue;

  if (!getCatalogueResult && !catalogueLoading && !catalogueError) {
    catalogueRefetch();
  }

  return (
    <>
      <LocationBar installationOwner={org} />
      <CatalogueContainer getCatalogueResult={getCatalogueResult} loading={catalogueLoading} error={catalogueError} />
    </>
  );
};

export default CatalogueListContainer;

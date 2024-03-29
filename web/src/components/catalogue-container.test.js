import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import CatalogueContainer from './catalogue-container';
import CatalogueDetailsMock from './catalogue-details';
import { renderWithRouter } from '../__tests__/test-utils';
import {
  CATALOGUE_CONTAINER_ROUTE, CreateCatalogueContainerLocation,
} from '../routes';
import Generator from '../__tests__/test-data-generator';
import { useGetCatalogue as useGetCatalogueMock } from '../backend-api-client';

jest.mock('../backend-api-client');

// mock out the actual catalogue-details
jest.mock('./catalogue-details', () => jest.fn(() => null));

describe('CatalogueContainer component', () => {
  test('successful fetch displays catalogue', async () => {
    // given a repo for a catalogue
    const catalogue = Generator.Catalogue.generateCatalogue();

    // and a mocked successful catalogue response
    const catalogueResponse = {
      data: catalogue,
    };

    useGetCatalogueMock.mockReturnValueOnce(catalogueResponse);

    // when catalogue container component renders
    const { findByTestId } = renderWithRouter(
      <CatalogueContainer />,
      CreateCatalogueContainerLocation(catalogue.encodedId),
      CATALOGUE_CONTAINER_ROUTE,
    );

    // then a catalogue container should be found
    expect(await findByTestId('catalogue-container-segment')).toBeInTheDocument();

    // and it fetched the catalogue details
    expect(useGetCatalogueMock).toHaveBeenCalledTimes(1);

    // and CatalogueDetails should have been shown
    expect(CatalogueDetailsMock).toHaveBeenCalledTimes(1);

    // and the location bar should be shown
    expect(await findByTestId('location-bar')).toBeInTheDocument();
  });

  test('unsuccessful fetch displays error message', async () => {
    // given a fetch error
    const getCatalogueResponse = {
      error: {
        message: 'An error message.',
      },
    };
    useGetCatalogueMock.mockReturnValueOnce(getCatalogueResponse);

    // when catalogue container component renders
    const { findByText } = renderWithRouter(
      <CatalogueContainer />,
      CreateCatalogueContainerLocation('someEncodedCatalogueId'),
      CATALOGUE_CONTAINER_ROUTE,
    );

    // then it contains an error message
    expect(await findByText('An error message.')).toBeInTheDocument();
  });

  test('loader is shown before fetch result', async () => {
    // given a mocked catalogues response that is not yet resolved
    const getCatalogueResponse = {
      loading: true,
    };
    useGetCatalogueMock.mockReturnValueOnce(getCatalogueResponse);

    // when catalogue container component renders
    const { getByText, getByTestId } = renderWithRouter(
      <CatalogueContainer />,
      CreateCatalogueContainerLocation('someEncodedCatalogueId'),
      CATALOGUE_CONTAINER_ROUTE,
    );

    // then it contains a loading message
    expect(getByText('Loading Catalogue..')).toBeInTheDocument();

    // and it contains a placeholder image
    expect(getByTestId('catalogue-container-placeholder')).toBeInTheDocument();
  });
});

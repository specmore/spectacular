import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import axiosMock from 'axios';
import CatalogueContainer from './catalogue-container';
import CatalogueDetailsMock from './catalogue-details';
import { renderWithRouter } from '../__tests__/test-utils';
import {
  CATALOGUE_CONTAINER_ROUTE, CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE, CreateCatalogueContainerLocation, CreateViewSpecLocation,
} from '../routes';
import Generator from '../__tests__/test-data-generator';

jest.mock('axios');

// mock out the actual catalogue-details
jest.mock('./catalogue-details', () => jest.fn(() => null));

describe('CatalogueContainer component', () => {
  test('successful fetch displays catalogue', async () => {
    // given a repo for a catalogue
    const catalogue = Generator.Catalogue.generateValidCatalogue();
    const { owner, name } = catalogue.id.repository;

    // and a mocked successful catalogue response
    const catalogueResponse = {
      data: catalogue,
    };

    axiosMock.get.mockResolvedValueOnce(catalogueResponse);

    // when catalogue container component renders
    const { findByTestId } = renderWithRouter(<CatalogueContainer />,
      CreateCatalogueContainerLocation(owner, name),
      CATALOGUE_CONTAINER_ROUTE);

    // then a catalogue container should be found
    expect(await findByTestId('catalogue-container-segment')).toBeInTheDocument();

    // and it fetched the catalogue details
    expect(axiosMock.get.mock.calls[0][0]).toBe(`/api/catalogues/${owner}/${name}`);

    // and CatalogueDetails should have been shown
    expect(CatalogueDetailsMock).toHaveBeenCalledTimes(1);
  });

  test('unsuccessful fetch displays error message', async () => {
    // given a repo for a catalogue
    const owner = 'test-owner';
    const repo = 'repo1';

    // and a mocked error thrown
    axiosMock.get.mockImplementation(() => {
      throw new Error('test error');
    });

    // when catalogue container component renders
    const { findByText } = renderWithRouter(<CatalogueContainer />,
      CreateCatalogueContainerLocation(owner, repo),
      CATALOGUE_CONTAINER_ROUTE);

    // then it contains an error message
    expect(await findByText('An error occurred while fetching catalogue details.')).toBeInTheDocument();
  });

  test('loader is shown before fetch result', async () => {
    // given a repo for a catalogue
    const owner = 'test-owner';
    const repo = 'repo1';

    // and a mocked catalogues response that is not yet resolved
    const responsePromise = new Promise(() => {});
    axiosMock.get.mockImplementation(() => responsePromise);

    // when catalogue container component renders
    const { getByText, getByTestId } = renderWithRouter(<CatalogueContainer />,
      CreateCatalogueContainerLocation(owner, repo),
      CATALOGUE_CONTAINER_ROUTE);

    // then it contains a loading message
    expect(getByText(`Loading catalogue for ${owner}/${repo}`)).toBeInTheDocument();

    // and it contains a placeholder image
    expect(getByTestId('catalogue-container-placeholder-image')).toBeInTheDocument();
  });

  test('swagger UI is shown when a spec file location is set', async () => {
    // given a repo for a catalogue
    const catalogue = Generator.Catalogue.generateValidCatalogue();
    const { owner, name } = catalogue.id.repository;

    // and a mocked successful catalogue response
    const catalogueResponse = {
      data: catalogue,
    };

    // and a spec file location
    const specFileLocation = 'test-owner/specs-test2/specs/example-spec.yaml';

    axiosMock.get.mockResolvedValueOnce(catalogueResponse);

    // and a mocked spec file fetch response
    const responsePromise = Promise.resolve({ });
    global.fetch = jest.fn().mockImplementation(() => responsePromise);

    // when catalogue container component renders
    const { findByTestId } = renderWithRouter(<CatalogueContainer />,
      CreateViewSpecLocation(owner, name, specFileLocation),
      CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE);

    // then a catalogue container should be found
    expect(await findByTestId('catalogue-container-segment')).toBeInTheDocument();

    // and a swagger ui container should be found
    expect(await findByTestId('catalogue-container-swagger-ui')).toBeInTheDocument();

    // and file contents should have been fetched
    const url = `/api/catalogues/${owner}/${name}/files/test-owner/specs-test2/specs/example-spec.yaml`;
    expect(global.fetch).toHaveBeenCalledTimes(1);
    expect(global.fetch).toHaveBeenCalledWith(url,
      expect.objectContaining({ url }));

    global.fetch.mockClear();
  });
});

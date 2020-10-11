import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import InterfaceContainer from './interface-container';
import InterfaceDetailsMock from './interface-details';
import { renderWithRouter } from '../__tests__/test-utils';
import {
  CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE, CreateInterfaceLocation, CreateViewSpecLocation,
} from '../routes';
import Generator from '../__tests__/test-data-generator';
import { useGetCatalogue as useGetCatalogueMock } from '../backend-api-client';

jest.mock('../backend-api-client');

// mock out the actual interface-details
jest.mock('./interface-details', () => jest.fn(() => null));

describe('InterfaceContainer component', () => {
  test('successful fetch displays interface', async () => {
    // given a repo for a catalogue
    const interfaceName = 'someInterface1';
    const specLog1 = Generator.SpecLog.generateSpecLog({ interfaceName });
    const catalogue = Generator.Catalogue.generateCatalogue({ specLogs: [specLog1] });

    // and a mocked successful catalogue response
    const catalogueResponse = {
      data: { catalogue },
    };

    useGetCatalogueMock.mockReturnValue(catalogueResponse);

    // when interface container component renders
    const { findByTestId } = renderWithRouter(<InterfaceContainer org="test-org" />,
      CreateInterfaceLocation(catalogue.encodedId, interfaceName),
      CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE);

    // then a interface container should be found
    expect(await findByTestId('interface-container-segment')).toBeInTheDocument();

    // and it fetched the catalogue details
    expect(useGetCatalogueMock).toHaveBeenCalledTimes(1);

    // and InterfaceDetails should have been shown
    expect(InterfaceDetailsMock).toHaveBeenCalledTimes(1);
  });

  // test('unsuccessful fetch displays error message', async () => {
  //   // given a fetch error
  //   const getCatalogueResponse = {
  //     error: {
  //       message: 'An error message.',
  //     },
  //   };
  //   useGetCatalogueMock.mockReturnValueOnce(getCatalogueResponse);

  //   // when catalogue container component renders
  //   const { findByText } = renderWithRouter(<CatalogueContainer />,
  //     CreateCatalogueContainerLocation('someEncodedCatalogueId'),
  //     CATALOGUE_CONTAINER_ROUTE);

  //   // then it contains an error message
  //   expect(await findByText('An error message.')).toBeInTheDocument();
  // });

  // test('loader is shown before fetch result', async () => {
  //   // given a mocked catalogues response that is not yet resolved
  //   const getCatalogueResponse = {
  //     loading: true,
  //   };
  //   useGetCatalogueMock.mockReturnValueOnce(getCatalogueResponse);

  //   // when catalogue container component renders
  //   const { getByText, getByTestId } = renderWithRouter(<CatalogueContainer />,
  //     CreateCatalogueContainerLocation('someEncodedCatalogueId'),
  //     CATALOGUE_CONTAINER_ROUTE);

  //   // then it contains a loading message
  //   expect(getByText('Loading catalogue..')).toBeInTheDocument();

  //   // and it contains a placeholder image
  //   expect(getByTestId('catalogue-container-placeholder-image')).toBeInTheDocument();
  // });

  test('swagger UI is shown when a spec file ref is set', async () => {
    // given a repo for a catalogue
    const interfaceName = 'someInterface1';
    const specLog1 = Generator.SpecLog.generateSpecLog({ interfaceName });
    const catalogue = Generator.Catalogue.generateCatalogue({ specLogs: [specLog1] });

    // and a mocked successful catalogue response
    const catalogueResponse = {
      data: { catalogue },
    };

    useGetCatalogueMock.mockReturnValue(catalogueResponse);

    // and ref
    const refName = 'some-branch';

    // and a mocked spec file fetch response
    const responsePromise = Promise.resolve({ });
    global.fetch = jest.fn().mockImplementation(() => responsePromise);

    // when interface container component renders
    const { findByTestId } = renderWithRouter(<InterfaceContainer org="test-org" />,
      CreateViewSpecLocation(catalogue.encodedId, interfaceName, refName),
      CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE);

    // then a catalogue container should be found
    expect(await findByTestId('interface-container-segment')).toBeInTheDocument();

    // and a swagger ui container should be found
    expect(await findByTestId('interface-container-swagger-ui')).toBeInTheDocument();

    // and file contents should have been fetched
    const url = `/api/catalogues/${catalogue.encodedId}/interfaces/${interfaceName}/file?ref=${refName}`;
    expect(global.fetch).toHaveBeenCalledTimes(1);
    expect(global.fetch).toHaveBeenCalledWith(url,
      expect.objectContaining({ url }));

    global.fetch.mockClear();
  });
});

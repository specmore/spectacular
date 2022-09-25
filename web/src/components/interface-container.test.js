import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import InterfaceContainer from './interface-container';
import InterfaceDetailsMock from './interface-details';
import SpecEvolutionMock from './spec-evolution/spec-evolution-container';
import { renderWithRouter } from '../__tests__/test-utils';
import {
  CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE,
  VIEW_SPEC_QUERY_PARAM_NAME,
  SHOW_EVOLUTION_QUERY_PARAM_NAME,
  SHOW_EVOLUTION_QUERY_PARAM_VALUES,
  CreateInterfaceLocation,
} from '../routes';
import { useGetInterfaceDetails as useGetInterfaceDetailsMock } from '../backend-api-client';

jest.mock('../backend-api-client');

// mock out the actual interface-details
jest.mock('./interface-details', () => jest.fn(() => null));

// mock out the actual spec-evolution-container
jest.mock('./spec-evolution/spec-evolution-container', () => jest.fn(() => null));

describe('InterfaceContainer component', () => {
  test('successful fetch displays interface', async () => {
    // given a unique catalogueId and interfaceName
    const catalogueId = 'someEncodedCatalogueId';
    const interfaceName = 'someInterface1';

    // and a mocked successful interface details response
    const getInterfaceResult = {
      data: {
        specEvolutionSummary: {},
        specEvolution: {},
      },
    };

    useGetInterfaceDetailsMock.mockReturnValue(getInterfaceResult);

    // when interface container component renders
    const { findByTestId } = renderWithRouter(
      <InterfaceContainer org="test-org" />,
      CreateInterfaceLocation(catalogueId, interfaceName),
      CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE,
    );

    // then a interface container should be found
    expect(await findByTestId('interface-container-segment')).toBeInTheDocument();

    // and it fetched the interface details
    expect(useGetInterfaceDetailsMock).toHaveBeenCalledTimes(1);

    // and InterfaceDetails should have been shown
    expect(InterfaceDetailsMock).toHaveBeenCalledTimes(1);
  });

  test('unsuccessful fetch displays error message', async () => {
    // given a fetch error
    const getInterfaceResult = {
      error: {
        message: 'An error message.',
      },
    };
    useGetInterfaceDetailsMock.mockReturnValueOnce(getInterfaceResult);

    // when interface container component renders
    const { findByText } = renderWithRouter(
      <InterfaceContainer org="test-org" />,
      CreateInterfaceLocation('someEncodedCatalogueId', 'someInterfaceName'),
      CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE,
    );

    // then it contains an error message
    expect(await findByText('An error message.')).toBeInTheDocument();
  });

  test('loader is shown before fetch result', async () => {
    // given a mocked get interface result response that is not yet resolved
    const getInterfaceResult = {
      loading: true,
    };
    useGetInterfaceDetailsMock.mockReturnValueOnce(getInterfaceResult);

    // when interface container component renders
    const { getByText, getByTestId } = renderWithRouter(
      <InterfaceContainer org="test-org" />,
      CreateInterfaceLocation('someEncodedCatalogueId', 'someInterfaceName'),
      CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE,
    );

    // then it contains a loading message
    expect(getByText('Loading Interface..')).toBeInTheDocument();

    // and it contains a placeholder image
    expect(getByTestId('interface-container-placeholder')).toBeInTheDocument();
  });

  test('spec evolution is shown when a selected', async () => {
    // given a unique catalogueId and interfaceName
    const catalogueId = 'someEncodedCatalogueId';
    const interfaceName = 'someInterface1';

    // and a mocked successful interface details response
    const getInterfaceResult = {
      data: {
        specEvolutionSummary: {},
        specEvolution: {},
      },
    };

    useGetInterfaceDetailsMock.mockReturnValue(getInterfaceResult);

    // and show spec evolution is set
    const interfaceLocation = CreateInterfaceLocation(catalogueId, interfaceName);
    const location = `${interfaceLocation}?${SHOW_EVOLUTION_QUERY_PARAM_NAME}=${SHOW_EVOLUTION_QUERY_PARAM_VALUES.SHOW}`;

    // when interface container component renders
    const { findByTestId } = renderWithRouter(
      <InterfaceContainer org="test-org" />,
      location,
      CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE,
    );

    // then a interface container should be found
    expect(await findByTestId('interface-container-segment')).toBeInTheDocument();

    // and SpecEvolutionMock should have been shown
    expect(SpecEvolutionMock).toHaveBeenCalledTimes(1);
  });

  test('swagger UI is shown when a spec file ref is set', async () => {
    // given a unique catalogueId and interfaceName
    const catalogueId = 'someEncodedCatalogueId';
    const interfaceName = 'someInterface1';

    // and a mocked successful interface details response
    const getInterfaceResult = {
      data: {
        specEvolutionSummary: {},
        specEvolution: {},
      },
    };

    useGetInterfaceDetailsMock.mockReturnValue(getInterfaceResult);

    // and interface location with ref
    const refName = 'some-branch';
    const interfaceLocation = CreateInterfaceLocation(catalogueId, interfaceName);
    const location = `${interfaceLocation}?${VIEW_SPEC_QUERY_PARAM_NAME}=${refName}`;

    // and a mocked spec file fetch response
    const responsePromise = Promise.resolve({ });
    global.fetch = jest.fn().mockImplementation(() => responsePromise);

    // when interface container component renders
    const { findByTestId } = renderWithRouter(
      <InterfaceContainer org="test-org" />,
      location,
      CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE,
    );

    // then a catalogue container should be found
    expect(await findByTestId('interface-container-segment')).toBeInTheDocument();

    // and a swagger ui container should be found
    expect(await findByTestId('interface-container-swagger-ui')).toBeInTheDocument();

    // and file contents should have been fetched
    const url = `/api/catalogues/${catalogueId}/interfaces/${interfaceName}/file?ref=${refName}`;
    expect(global.fetch).toHaveBeenCalledTimes(1);
    expect(global.fetch).toHaveBeenCalledWith(
      url,
      expect.objectContaining({ url }),
    );

    global.fetch.mockClear();
  });
});

import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import AppContainer from './app-container';
import { renderWithRouter } from '../../__tests__/test-utils';
import {
  useGetInstallations as useGetInstallationsMock,
  useGetAppDetails as useGetAppDetailsMock,
} from '../../backend-api-client';
import InstallationContainerMock from '../installation-container';

jest.mock('../../backend-api-client');

// mock out the actual menu-bar
jest.mock('../menu-bar', () => jest.fn(() => null));

// mock out the actual footer-bar
jest.mock('../footer-bar', () => jest.fn(() => null));

// mock out the actual installation-container
jest.mock('../installation-container', () => jest.fn(() => null));

afterEach(() => {
  useGetAppDetailsMock.mockClear();
  useGetInstallationsMock.mockClear();
  InstallationContainerMock.mockClear();
});

describe('AppContainer component', () => {
  test('retrieves the installationId for an old catalogue location and redirects to appropriate new installation location', async () => {
    // given a catalogue encodedId
    const encodedId = 'test-encodedId';

    // and an old catalogue container location
    const oldCatalogueContainerLocation = `/catalogue/${encodedId}`;

    // and a mocked successful app details response
    const getAppDetailsResult = {
      data: {
        clientId: 'test-client-id',
      },
    };

    useGetAppDetailsMock.mockReturnValue(getAppDetailsResult);

    // and a mocked successful installations query response for the given encodedId
    const useGetInstallationsResult = {
      data: {
        installations: [
          {
            id: 1234,
          },
        ],
      },
    };

    useGetInstallationsMock.mockReturnValue(useGetInstallationsResult);

    // when app container component renders with the old catalogue container location
    const { getByTestId } = renderWithRouter(<AppContainer />, oldCatalogueContainerLocation);

    // then a app container is found
    expect(getByTestId('app-container')).toBeInTheDocument();

    // and it gets the app details
    expect(useGetAppDetailsMock).toHaveBeenCalledTimes(1);

    // and it tries to find an installation for the encodedId
    expect(useGetInstallationsMock).toHaveBeenCalledWith({ queryParams: { catalogueEncodedId: encodedId } });

    // and it tried to render the installation-container
    expect(InstallationContainerMock).toHaveBeenCalledTimes(1);
  });

  test('fails to find the installationId for an old catalogue location and shows an error', async () => {
    // given a catalogue encodedId
    const encodedId = 'test-encodedId';

    // and an old catalogue container location
    const oldCatalogueContainerLocation = `/catalogue/${encodedId}`;

    // and a mocked successful app details response
    const getAppDetailsResult = {
      data: {
        clientId: 'test-client-id',
      },
    };

    useGetAppDetailsMock.mockReturnValue(getAppDetailsResult);

    // and a mocked successful installations query response for the given encodedId
    const useGetInstallationsResult = {
      data: {
        installations: [],
      },
    };

    useGetInstallationsMock.mockReturnValue(useGetInstallationsResult);

    // when app container component renders with the old catalogue container location
    const { getByTestId, getByText } = renderWithRouter(<AppContainer />, oldCatalogueContainerLocation);

    // then a app container is found
    expect(getByTestId('app-container')).toBeInTheDocument();

    // and it gets the app details
    expect(useGetAppDetailsMock).toHaveBeenCalledTimes(1);

    // and it tries to find an installation for the encodedId
    expect(useGetInstallationsMock).toHaveBeenCalledWith({ queryParams: { catalogueEncodedId: encodedId } });

    // and it does not render the installation-container
    expect(InstallationContainerMock).toHaveBeenCalledTimes(0);

    // and it shows an error message
    expect(getByText('The URL you have used is no longer support in this version.')).toBeInTheDocument();
  });

  test('retrieves the installationId for an old interface location and redirects to appropriate new installation location', async () => {
    // given a catalogue encodedId and interfaceName
    const encodedId = 'test-encodedId';
    const interfaceName = 'testInterface123';

    // and an old catalogue container location
    const oldCatalogueContainerLocation = `/catalogue/${encodedId}/interface/${interfaceName}?ref=test-tag`;

    // and a mocked successful app details response
    const getAppDetailsResult = {
      data: {
        clientId: 'test-client-id',
      },
    };

    useGetAppDetailsMock.mockReturnValue(getAppDetailsResult);

    // and a mocked successful installations query response for the given encodedId
    const useGetInstallationsResult = {
      data: {
        installations: [
          {
            id: 1234,
          },
        ],
      },
    };

    useGetInstallationsMock.mockReturnValue(useGetInstallationsResult);

    // when app container component renders with the old catalogue container location
    const { getByTestId } = renderWithRouter(<AppContainer />, oldCatalogueContainerLocation);

    // then a app container is found
    expect(getByTestId('app-container')).toBeInTheDocument();

    // and it gets the app details
    expect(useGetAppDetailsMock).toHaveBeenCalledTimes(1);

    // and it tries to find an installation for the encodedId
    expect(useGetInstallationsMock).toHaveBeenCalledWith({ queryParams: { catalogueEncodedId: encodedId } });

    // and it tried to render the installation-container
    expect(InstallationContainerMock).toHaveBeenCalledTimes(1);
  });
});

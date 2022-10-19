import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import AppContainer from './app-container';
import { renderWithRouter } from '../../__tests__/test-utils';
import {
  useGetInstallations as useGetInstallationsMock,
  useGetAppDetails as useGetAppDetailsMock,
} from '../../backend-api-client';
import MenuBarMock from '../menu-bar';
import FooterBarMock from '../footer-bar';
import InstallationContainerMock from '../installation-container';

jest.mock('../../backend-api-client');

// mock out the actual menu-bar
jest.mock('../menu-bar', () => jest.fn(() => null));

// mock out the actual footer-bar
jest.mock('../footer-bar', () => jest.fn(() => null));

// mock out the actual installation-container
jest.mock('../installation-container', () => jest.fn(() => null));

describe('AppContainer component', () => {
  test('retrieves the installationId for old catalogue location and redirects to appropriate new installation location', async () => {
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

    // when app container component renders with the old catalogue container location
    const { getByTestId, getByText } = renderWithRouter(<AppContainer />, oldCatalogueContainerLocation);

    // then a app container is found
    expect(getByTestId('app-container')).toBeInTheDocument();

    // and it gets the app details
    expect(useGetAppDetailsMock).toHaveBeenCalledTimes(1);

    // and it tried to render the installation-container
    expect(InstallationContainerMock).toHaveBeenCalledTimes(1);
  });
});

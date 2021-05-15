import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import CatalogueList from './catalogue-list';
import CatalogueListItemMock from './catalogue-list-item';
import TopicSelectionListMock from './topic-selection-list';
import { renderWithRouter } from '../../__tests__/test-utils';
import Generator from '../../__tests__/test-data-generator';
import { useFindCataloguesForUser as useFindCataloguesForUserMock } from '../../backend-api-client';

jest.mock('../../backend-api-client');

// mocks
jest.mock('./catalogue-list-item', () => jest.fn(() => null));
jest.mock('./topic-selection-list', () => jest.fn(() => null));

afterEach(() => {
  CatalogueListItemMock.mockClear();
  TopicSelectionListMock.mockClear();
});

describe('CatalogueList component', () => {
  test('successful fetch displays catalogue items', async () => {
    // given a mocked successful catalogues response with 2 catalogues
    const catalogue1 = Generator.Catalogue.generateCatalogue({ name: 'testCatalogue1' });
    const catalogue2 = Generator.Catalogue.generateCatalogue({ name: 'testCatalogue2' });
    const cataloguesResponse = {
      data: {
        catalogues: [catalogue1, catalogue2],
      },
    };

    useFindCataloguesForUserMock.mockReturnValueOnce(cataloguesResponse);

    // when catalogue list component renders
    const { findByTestId } = renderWithRouter(<CatalogueList org="test-org" />);

    // then a catalogue list item group should be found
    expect(await findByTestId('catalogue-list-item-group')).toBeInTheDocument();

    // and the 2 CatalogueListItem should have been created
    expect(CatalogueListItemMock).toHaveBeenCalledTimes(2);

    // and the TopicSelectionList is shown
    expect(TopicSelectionListMock).toHaveBeenCalledWith(
      { catalogues: cataloguesResponse.data.catalogues },
      {},
    );
  });

  test('unsuccessful fetch displays error message', async () => {
    // given a fetch error
    const cataloguesResponse = {
      error: {
        message: 'An error message.',
      },
    };
    useFindCataloguesForUserMock.mockReturnValueOnce(cataloguesResponse);

    // when catalogue list component renders
    const { findByText } = renderWithRouter(<CatalogueList />);

    // then it contains an error message
    expect(await findByText('An error message.')).toBeInTheDocument();
  });

  test('loader is shown before fetch result', async () => {
    // given a mocked catalogues response that is not yet resolved
    const cataloguesResponse = {
      loading: true,
    };
    useFindCataloguesForUserMock.mockReturnValueOnce(cataloguesResponse);

    // when catalogue list component renders
    const { getByTestId } = renderWithRouter(<CatalogueList />);

    // then  it contains a place holder item
    expect(getByTestId('catalogue-list-placeholder')).toBeInTheDocument();
  });

  test('filters out catalogue items based on selected topics', async () => {
    // given a mocked successful catalogues response with 2 catalogues
    const catalogue1 = Generator.Catalogue.generateCatalogue({ name: 'testCatalogue1', topics: ['topic1'] });
    const catalogue2 = Generator.Catalogue.generateCatalogue({ name: 'testCatalogue2' });
    const cataloguesResponse = {
      data: {
        catalogues: [catalogue1, catalogue2],
      },
    };

    useFindCataloguesForUserMock.mockReturnValueOnce(cataloguesResponse);

    // when catalogue list component renders
    const { findByTestId } = renderWithRouter(<CatalogueList org="test-org" />, '?topics=topic1');

    // then a catalogue list item group should be found
    expect(await findByTestId('catalogue-list-item-group')).toBeInTheDocument();

    // and only the catalogue1 CatalogueListItem should have been created
    expect(CatalogueListItemMock).toHaveBeenCalledTimes(1);
    expect(CatalogueListItemMock).toHaveBeenCalledWith(
      { catalogue: catalogue1 },
      {},
    );

    // and only catalogue1 is sent to the TopicSelectionList
    expect(TopicSelectionListMock).toHaveBeenCalledWith(
      { catalogues: [catalogue1] },
      {},
    );
  });
});

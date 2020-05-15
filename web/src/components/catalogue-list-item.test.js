import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import CatalogueListItem from './catalogue-list-item';
import { renderWithRouter } from '../__tests__/test-utils';
import Generator from '../__tests__/test-data';

describe('CatalogueListItem component', () => {
  test('renders catalogue list item details when no error is given', async () => {
    // given a catalogue data item
    const catalogue = Generator.Catalogue.generateValidCatalogue();

    // when catalogue list item component renders
    const { getByTestId, getByText } = renderWithRouter(<CatalogueListItem catalogue={catalogue} />);

    // then a catalogue list item details item is found
    expect(getByTestId('catalogue-list-item-details-item')).toBeInTheDocument();

    // and the name of the catalogue is shown
    expect(getByText('Test Catalogue 1')).toBeInTheDocument();

    // and the name of the repository is shown
    expect(getByText('test-owner/specs-test')).toBeInTheDocument();

    // and view catalogue button is found
    expect(getByTestId('view-catalogue-button')).toBeInTheDocument();
  });

  test('renders catalogue list item details with no spec files', async () => {
    // given a catalogue manifest with no files
    const catalogueManifest = Generator.Catalogue.generateCatalogueManifest({ hasNoSpecFiles: true });

    // and a catalogue data item with the manifest
    const catalogue = Generator.Catalogue.generateValidCatalogue({ catalogueManifest });

    // when catalogue list item component renders
    const { getByTestId, getByText } = renderWithRouter(<CatalogueListItem catalogue={catalogue} />);

    // then a catalogue list item details item is found
    expect(getByTestId('catalogue-list-item-details-item')).toBeInTheDocument();

    // and the label 0 specs is shown
    expect(getByText('0 specs')).toBeInTheDocument();
  });


  test('renders catalogue list item error item when an error are given', async () => {
    // given a catalogue manifest error
    const errorMessage = 'An error occurred while parsing the catalogue manifest yaml file. The following field is missing: bla bla bla';

    // and a catalogue data item with the error
    const catalogue = Generator.Catalogue.generateCatalogueWithError(errorMessage);

    // when catalogue list item component renders
    const { getByTestId, getByText, queryByTestId } = renderWithRouter(<CatalogueListItem catalogue={catalogue} />);

    // then a catalogue list item details item is found
    expect(getByTestId('catalogue-list-item-error-item')).toBeInTheDocument();

    // and the name of the repository is shown
    expect(getByText('test-owner/specs-test')).toBeInTheDocument();

    // and the error message is shown
    expect(getByText(errorMessage))
      .toBeInTheDocument();

    // and view catalogue button is not found
    expect(queryByTestId('view-catalogue-button')).not.toBeInTheDocument();
  });
});

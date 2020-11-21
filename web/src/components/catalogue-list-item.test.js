import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import CatalogueListItem from './catalogue-list-item';
import { renderWithRouter } from '../__tests__/test-utils';
import Generator from '../__tests__/test-data-generator';

describe('CatalogueListItem component', () => {
  test('renders catalogue list item details when no error is given', async () => {
    // given a catalogue data item
    const catalogue = Generator.Catalogue.generateCatalogue();

    // when catalogue list item component renders
    const { getByTestId, getByText } = renderWithRouter(<CatalogueListItem catalogue={catalogue} />);

    // then a catalogue list item details item is found
    expect(getByTestId('catalogue-list-item-details-item')).toBeInTheDocument();

    // and the name of the catalogue is shown
    expect(getByText('Test Catalogue 1')).toBeInTheDocument();
  });

  test('renders catalogue list item error item when an error is given', async () => {
    // given a catalogue manifest error
    const errorMessage = 'An error occurred while parsing the catalogue manifest yaml file. The following field is missing: bla bla bla';

    // and a catalogue data item with the error
    const catalogue = Generator.Catalogue.generateCatalogueWithError(errorMessage);

    // when catalogue list item component renders
    const { getByTestId, getByText } = renderWithRouter(<CatalogueListItem catalogue={catalogue} />);

    // then a catalogue list item details item is found
    expect(getByTestId('catalogue-list-item-error-item')).toBeInTheDocument();

    // and the full path to the catalogue manifest is shown
    expect(getByText('test-owner/specs-test/error-config.yml')).toBeInTheDocument();

    // and the error message is shown
    expect(getByText(errorMessage))
      .toBeInTheDocument();
  });
});

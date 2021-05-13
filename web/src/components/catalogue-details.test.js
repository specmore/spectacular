import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import CatalogueDetails from './catalogue-details';
import InterfaceListItemMock from './interface-list-item';
import { renderWithRouter } from '../__tests__/test-utils';
import Generator from '../__tests__/test-data-generator';

// mock out the actual spec-file-item
jest.mock('./interface-list-item', () => jest.fn(() => null));

describe('CatalogueDetails component', () => {
  test('renders catalogue details when no error is given', async () => {
    // given a catalogue data item with 2 spec files
    const specEvolutionSummary1 = Generator.SpecEvolutionSummary.generateSpecEvolutionSummary({ interfaceName: 'testInterface1' });
    const specEvolutionSummary2 = Generator.SpecEvolutionSummary.generateSpecEvolutionSummary({ interfaceName: 'testInterface2' });
    const catalogue = Generator.Catalogue.generateCatalogue({ specEvolutionSummaries: [specEvolutionSummary1, specEvolutionSummary2] });

    // when catalogue details component renders
    const { getByTestId, getByText } = renderWithRouter(<CatalogueDetails catalogue={catalogue} />);

    // then a catalogue details container is found
    expect(getByTestId('catalogue-details-container')).toBeInTheDocument();

    // and the name of the catalogue is shown
    expect(getByText('Test Catalogue 1')).toBeInTheDocument();

    // and the catalogue topics are shown
    expect(getByText('test-topic-1')).toBeInTheDocument();

    // and 2 interface items were created
    expect(InterfaceListItemMock).toHaveBeenCalledTimes(2);
  });


  test('renders a catalogue details error message when an error is given', async () => {
    // given a catalogue item with an error
    const errorMessage = 'An error occurred while parsing the catalogue manifest yaml file. The following field is missing: bla bla bla';
    const catalogue = Generator.Catalogue.generateCatalogueWithError(errorMessage);

    // when catalogue details component renders
    const { getByTestId, getByText } = renderWithRouter(<CatalogueDetails catalogue={catalogue} />);

    // then a catalogue details error message is found
    expect(getByTestId('catalogue-details-error-message')).toBeInTheDocument();

    // and the path of the catalogue is shown
    expect(getByText('test-owner/specs-test/error-config.yml')).toBeInTheDocument();

    // and the error message is shown
    expect(getByText(errorMessage))
      .toBeInTheDocument();
  });
});

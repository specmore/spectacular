import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import CatalogueDetails from './catalogue-details';
import SpecLogMock from './spec-log';
import { renderWithRouter } from '../__tests__/test-utils';

// mock out the actual spec-file-item
jest.mock('./spec-log', () => jest.fn(() => null));

describe('CatalogueDetails component', () => {
  test('renders catalogue details when no error is given', async () => {
    // given a catalogue data item with 2 spec files
    const catalogue = {
      repository: {
        owner: 'test-owner',
        name: 'specs-test',
        htmlUrl: 'https://github.com/test-owner/specs-test',
        nameWithOwner: 'test-owner/specs-test',
      },
      catalogueManifest: {
        name: 'Test Catalogue 1',
        description: 'Specifications for all the interfaces in the across the system X.',
        'spec-files': [],
      },
      specLogs: [{ id: '123' }, { id: '456' }],
      error: null,
    };

    // when catalogue details component renders
    const { getByTestId, getByText } = renderWithRouter(<CatalogueDetails catalogue={catalogue} />);

    // then a catalogue details container is found
    expect(getByTestId('catalogue-details-container')).toBeInTheDocument();

    // and the name of the catalogue is shown
    expect(getByText('Test Catalogue 1')).toBeInTheDocument();

    // and the name of the repository is shown
    expect(getByText('test-owner/specs-test')).toBeInTheDocument();

    // and 2 spec file items were created
    expect(SpecLogMock).toHaveBeenCalledTimes(2);
  });


  test('renders a catalogue details error message when an error is given', async () => {
    // given a catalogue data item
    const catalogue = {
      repository: {
        owner: 'test-owner',
        name: 'specs-test',
        htmlUrl: 'https://github.com/test-owner/specs-test',
        nameWithOwner: 'test-owner/specs-test',
      },
      catalogueManifest: null,
      error: 'An error occurred while parsing the catalogue manifest yaml file. The following field is missing: bla bla bla',
    };

    // when catalogue details component renders
    const { getByTestId, getByText } = renderWithRouter(<CatalogueDetails catalogue={catalogue} />);

    // then a catalogue details error message is found
    expect(getByTestId('catalogue-details-error-message')).toBeInTheDocument();

    // and the name of the repository is shown
    expect(getByText('test-owner/specs-test')).toBeInTheDocument();

    // and the error message is shown
    expect(getByText('An error occurred while parsing the catalogue manifest yaml file. The following field is missing: bla bla bla'))
      .toBeInTheDocument();
  });
});

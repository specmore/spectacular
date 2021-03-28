import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import InterfaceDetails from './interface-details';
import { renderWithRouter } from '../__tests__/test-utils';
import Generator from '../__tests__/test-data-generator';

// // mock out the actual spec-file-item
// jest.mock('./spec-log', () => jest.fn(() => null));

describe('InterfaceDetails component', () => {
  test('renders interface details when no error is given', async () => {
    // given a spec evolution summary two proposed changes
    const specEvolutionSummary = Generator.SpecEvolutionSummary.generateSpecEvolutionSummary({ proposedChangesCount: 2 });

    // when interface details component renders
    const { getByTestId, getByText } = renderWithRouter(<InterfaceDetails specEvolutionSummary={specEvolutionSummary} />);

    // then an interface details container is found
    expect(getByTestId('interface-details-container')).toBeInTheDocument();

    // and the name of the latest agreed spec file is shown
    expect(getByText(specEvolutionSummary.latestAgreed.parseResult.openApiSpec.title)).toBeInTheDocument();

    // and the version of the latest agreed spec file is shown
    expect(getByText(specEvolutionSummary.latestAgreed.parseResult.openApiSpec.version)).toBeInTheDocument();

    // and the number of proposed changes is shown
    expect(getByText('2')).toBeInTheDocument();
  });

  test('renders an interface details error message when openapi parse errors are given', async () => {
    // given a spec item with parse errors
    const specItem = Generator.SpecItem.generateSpecItemWithError('The spec file could not be found.');
    const specEvolutionSummary = Generator.SpecEvolutionSummary.generateSpecEvolutionSummary({ latestAgreed: specItem });

    // when interface details container renders
    const { getByText, getByTestId } = renderWithRouter(<InterfaceDetails specEvolutionSummary={specEvolutionSummary} />);

    // then the file path suffixed by the repo name is shown
    expect(getByText('test-owner/specs-test/specs/example-template.yaml', { exact: false })).toBeInTheDocument();

    // and the spec file error item is shown with error message
    expect(getByTestId('spec-log-error')).toBeInTheDocument();
    expect(getByText('The spec file could not be found.')).toBeInTheDocument();
  });
});

import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import InterfaceListItem from './interface-list-item';
import { renderWithRouter } from '../__tests__/test-utils';
import Generator from '../__tests__/test-data-generator';

describe('InterfaceListItem component', () => {
  test("shows latest agreed spec item's openApiSpec title and version", async () => {
    // given a specEvolutionSummary with a latest agreed spec item
    const specEvolutionSummary = Generator.SpecEvolutionSummary.generateSpecEvolutionSummary();

    // when InterfaceListItem component renders
    const { getByText } = renderWithRouter(<InterfaceListItem specEvolutionSummary={specEvolutionSummary} />);

    // then the open api spec title is shown
    expect(getByText(specEvolutionSummary.latestAgreed.parseResult.openApiSpec.title)).toBeInTheDocument();

    // and the open api spec version is shown
    expect(getByText(specEvolutionSummary.latestAgreed.parseResult.openApiSpec.version)).toBeInTheDocument();
  });

  test('shows spec item error message for latest agreed spec item with parse result errors', async () => {
    // given a specEvolutionSummary with a latest agreed spec item with parse errors
    const latestAgreeSpecItemWithError = Generator.SpecItem.generateSpecItemWithError('The spec file could not be found.');
    const specEvolutionSummary = Generator.SpecEvolutionSummary.generateSpecEvolutionSummary({
      latestAgreed: latestAgreeSpecItemWithError,
    });

    // when InterfaceListItem component renders
    const { getByText, getByTestId } = renderWithRouter(<InterfaceListItem specEvolutionSummary={specEvolutionSummary} />);

    // then the file path suffixed by the repo name is shown
    expect(getByText('test-owner/specs-test/specs/example-template.yaml', { exact: false })).toBeInTheDocument();

    // and the spec file error item is shown with error message
    expect(getByTestId('spec-log-error')).toBeInTheDocument();
    expect(getByText(latestAgreeSpecItemWithError.parseResult.errors[0])).toBeInTheDocument();
  });

  test('shows proposed changes count', async () => {
    // given a specEvolutionSummary with 4 proposedChangesCount
    const specEvolutionSummary = Generator.SpecEvolutionSummary.generateSpecEvolutionSummary({ proposedChangesCount: 4 });

    // when InterfaceListItem component renders
    const { getByText } = renderWithRouter(<InterfaceListItem specEvolutionSummary={specEvolutionSummary} />);

    // then a proposed changes label is shown with a 4 proposed changes count
    expect(getByText('4')).toBeInTheDocument();
  });
});

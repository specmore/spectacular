import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import InterfaceListItem from './interface-list-item';
import { renderWithRouter } from '../__tests__/test-utils';
import Generator from '../__tests__/test-data-generator';

describe('InterfaceListItem component', () => {
  test("shows latest agreed spec item's openApiSpec title and version", async () => {
    // given a spec evolution with a latest agreed evolution item on the main branch
    const latestAgreedEvolutionItemParameters = {
      specFileTitle: 'Test Title',
      specFileVersion: '1.2.3',
    };
    const specEvolution = Generator.SpecEvolution.generateSpecEvolution({ latestAgreedEvolutionItemParameters });

    // when InterfaceListItem component renders
    const { getByText } = renderWithRouter(<InterfaceListItem specEvolution={specEvolution} />);

    // then the open api spec title is shown
    expect(getByText(latestAgreedEvolutionItemParameters.specFileTitle)).toBeInTheDocument();

    // and the open api spec version is shown
    expect(getByText(latestAgreedEvolutionItemParameters.specFileVersion)).toBeInTheDocument();
  });

  test('shows proposed changes count for multiple proposed changes across main and release branches', async () => {
    // given a spec evolution with pull request evolution items on the main branch and release branches
    const specEvolution = Generator.SpecEvolution.generateSpecEvolution({ numberReleaseBranches: 1, numberPullRequestItemsPerBranch: 2 });

    // when InterfaceListItem component renders
    const { getByText } = renderWithRouter(<InterfaceListItem specEvolution={specEvolution} />);

    // then a proposed changes label is shown with a 4 changes count
    expect(getByText('4')).toBeInTheDocument();
  });

  test('shows spec item error message for latest agreed spec item with parse result errors', async () => {
    // given a spec evolution with a latest agreed evolution item with parse errors
    const latestAgreedEvolutionItemParameters = {
      specFileParseErrorMessage: 'The spec file could not be found.',
    };
    const specEvolution = Generator.SpecEvolution.generateSpecEvolution({ latestAgreedEvolutionItemParameters });

    // when InterfaceListItem component renders
    const { getByText, getByTestId } = renderWithRouter(<InterfaceListItem specEvolution={specEvolution} />);

    // then the file path suffixed by the repo name is shown
    expect(getByText('test-owner/specs-test/specs/example-template.yaml', { exact: false })).toBeInTheDocument();

    // and the spec file error item is shown with error message
    expect(getByTestId('spec-log-error')).toBeInTheDocument();
    expect(getByText(latestAgreedEvolutionItemParameters.specFileParseErrorMessage)).toBeInTheDocument();
  });
});

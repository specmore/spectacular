import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import SpecEvolutionBranchContainer from './spec-evolution-branch';
import { renderWithRouter } from '../../__tests__/test-utils';
import Generator from '../../__tests__/test-data-generator';
import EvolutionLinesItemMock from './evolution-lines-item';
import EvolutionItemDetailsMock from './evolution-item-details';
import {
  SHOW_EVOLUTION_QUERY_PARAM_NAME,
  SHOW_EVOLUTION_QUERY_PARAM_VALUES,
} from '../../routes';

// mock out the actual spec evolution items
jest.mock('./evolution-lines-item', () => jest.fn(() => null));
jest.mock('./evolution-item-details', () => jest.fn(() => null));

afterEach(() => {
  EvolutionLinesItemMock.mockClear();
  EvolutionItemDetailsMock.mockClear();
});

describe('SpecEvolutionBranchContainer component', () => {
  test('renders a previous versions label for a main branch', async () => {
    // given an evolution branch
    const evolutionBranch = Generator.EvolutionBranch.generateEvolutionBranch({ numberPullRequests: 1, numberPreviousVersions: 1 });

    // when SpecEvolutionBranchContainer renders a main branch
    const { getByTestId } = renderWithRouter(<SpecEvolutionBranchContainer evolutionBranch={evolutionBranch} isMain />);

    // then an evolution branch container is found
    expect(getByTestId('evolution-branch-container')).toBeInTheDocument();

    // and previous versions label is found
    expect(getByTestId('previous-versions-details-container')).toBeInTheDocument();
  });

  test('does not render previous version items for a main branch when not set to show', async () => {
    // given an evolution branch
    const evolutionBranch = Generator.EvolutionBranch.generateEvolutionBranch({ numberPullRequests: 1, numberPreviousVersions: 1 });

    // when SpecEvolutionBranchContainer renders a main branch without the show previous versions query parameter
    const { getByTestId } = renderWithRouter(<SpecEvolutionBranchContainer evolutionBranch={evolutionBranch} isMain />);

    // then an evolution branch container is found
    expect(getByTestId('evolution-branch-container')).toBeInTheDocument();

    // and only the PR and branch head EvolutionLinesItem items should have been created
    expect(EvolutionLinesItemMock).toHaveBeenCalledTimes(2);

    // and only the PR and branch head EvolutionItemDetails items should have been created
    expect(EvolutionItemDetailsMock).toHaveBeenCalledTimes(2);
  });

  test('renders previous version items for a main branch when set to show', async () => {
    // given an evolution branch
    const evolutionBranch = Generator.EvolutionBranch.generateEvolutionBranch({ numberPullRequests: 1, numberPreviousVersions: 1 });

    // and the show previous versions query parameter is set
    const location = `?${SHOW_EVOLUTION_QUERY_PARAM_NAME}=${SHOW_EVOLUTION_QUERY_PARAM_VALUES.SHOW_WITH_PREVIOUS_VERSIONS}`;

    // when SpecEvolutionBranchContainer renders a main branch without the show previous versions query parameter
    const { getByTestId } = renderWithRouter(<SpecEvolutionBranchContainer evolutionBranch={evolutionBranch} isMain />, location);

    // then an evolution branch container is found
    expect(getByTestId('evolution-branch-container')).toBeInTheDocument();

    // and 3 EvolutionLinesItem items should have been created
    expect(EvolutionLinesItemMock).toHaveBeenCalledTimes(3);

    // and 3 EvolutionItemDetails items should have been created
    expect(EvolutionItemDetailsMock).toHaveBeenCalledTimes(3);
  });

  test('does not render a previous versions label for a release branch and always shows previous version entries', async () => {
    // given an evolution branch
    const evolutionBranch = Generator.EvolutionBranch.generateEvolutionBranch({ numberPullRequests: 1, numberPreviousVersions: 1 });

    // when SpecEvolutionBranchContainer renders a non main branch
    const { queryByTestId } = renderWithRouter(<SpecEvolutionBranchContainer evolutionBranch={evolutionBranch} />);

    // then an evolution branch container is found
    expect(queryByTestId('evolution-branch-container')).toBeInTheDocument();

    // and previous versions label is not found
    expect(queryByTestId('previous-versions-details-container')).not.toBeInTheDocument();

    // and 3 EvolutionLinesItem items should have been created
    expect(EvolutionLinesItemMock).toHaveBeenCalledTimes(3);

    // and 3 EvolutionItemDetails items should have been created
    expect(EvolutionItemDetailsMock).toHaveBeenCalledTimes(3);
  });
});

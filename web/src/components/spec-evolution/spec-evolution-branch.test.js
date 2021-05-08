import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import SpecEvolutionBranchContainer from './spec-evolution-branch';
import { renderWithRouter } from '../../__tests__/test-utils';
import Generator from '../../__tests__/test-data-generator';
import EvolutionLinesItemMock from './evolution-lines-item';
import EvolutionItemDetailsMock from './evolution-item-details';


// mock out the actual spec evolution items
jest.mock('./evolution-lines-item', () => jest.fn(() => null));
jest.mock('./evolution-item-details', () => jest.fn(() => null));

afterEach(() => {
  EvolutionLinesItemMock.mockClear();
  EvolutionItemDetailsMock.mockClear();
});

describe('SpecEvolutionBranchContainer component', () => {
  test('renders a previous versions item for a main branch', async () => {
    // given an evolution branch
    const evolutionBranch = Generator.EvolutionBranch.generateEvolutionBranch({ numberPullRequests: 1, numberPreviousVersions: 1 });

    // when SpecEvolutionBranchContainer renders a main branch
    const { getByTestId } = renderWithRouter(<SpecEvolutionBranchContainer evolutionBranch={evolutionBranch} isMain />);

    // then an evolution branch container is found
    expect(getByTestId('evolution-branch-container')).toBeInTheDocument();

    // and previous versions item is found
    expect(getByTestId('previous-versions-details-container')).toBeInTheDocument();

    // and 3 EvolutionLinesItem items should have been created
    expect(EvolutionLinesItemMock).toHaveBeenCalledTimes(3);

    // and 3 EvolutionItemDetails items should have been created
    expect(EvolutionItemDetailsMock).toHaveBeenCalledTimes(3);
  });
});

describe('SpecEvolutionBranchContainer component', () => {
  test('does not render a previous versions details item for a release branch and always shows previous version entries', async () => {
    // given an evolution branch
    const evolutionBranch = Generator.EvolutionBranch.generateEvolutionBranch({ numberPullRequests: 1, numberPreviousVersions: 1 });

    // when SpecEvolutionBranchContainer renders a non main branch
    const { queryByTestId } = renderWithRouter(<SpecEvolutionBranchContainer evolutionBranch={evolutionBranch} />);

    // then an evolution branch container is found
    expect(queryByTestId('evolution-branch-container')).toBeInTheDocument();

    // and previous versions item is not found
    expect(queryByTestId('previous-versions-details-container')).not.toBeInTheDocument();

    // and 3 EvolutionLinesItem items should have been created
    expect(EvolutionLinesItemMock).toHaveBeenCalledTimes(3);

    // and 3 EvolutionItemDetails items should have been created
    expect(EvolutionItemDetailsMock).toHaveBeenCalledTimes(3);
  });
});

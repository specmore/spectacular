import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import SpecEvolution from './spec-evolution-container';
import { renderWithRouter } from '../../__tests__/test-utils';
import Generator from '../../__tests__/test-data-generator';
import SpecEvolutionBranchContainerMock from './spec-evolution-branch';

// mock out the actual spec evolution branch items
jest.mock('./spec-evolution-branch', () => jest.fn(() => null));

describe('SpecEvolution component', () => {
  test('renders the release spec evolution branches before the main branch', async () => {
    // given a spec evolution with 2 release branches and a main branch
    const specEvolution = Generator.SpecEvolution.generateSpecEvolution({ numberReleaseBranches: 2 });

    // when spec evolution component renders
    const { getByTestId } = renderWithRouter(<SpecEvolution specEvolution={specEvolution} />);

    // then an spec evolution container is found
    expect(getByTestId('spec-evolution-container')).toBeInTheDocument();

    // and the 3 SpecEvolutionBranch items should have been created
    expect(SpecEvolutionBranchContainerMock).toHaveBeenCalledTimes(3);

    // and the SpecEvolutionBranch main branch should have been rendered last
    expect(SpecEvolutionBranchContainerMock).toHaveBeenLastCalledWith(
      { evolutionBranch: specEvolution.main, isMain: true },
      {},
    );
  });
});

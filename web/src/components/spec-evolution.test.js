import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import SpecEvolution from './spec-evolution';
import { renderWithRouter } from '../__tests__/test-utils';
import Generator from '../__tests__/test-data-generator';
import { useGetInterfaceSpecEvolution as useGetInterfaceSpecEvolutionMock } from '../backend-api-client';
import SpecEvolutionBranchContainerMock from './spec-evolution-branch';

jest.mock('../backend-api-client');

// mock out the actual spec evolution branch items
jest.mock('./spec-evolution-branch', () => jest.fn(() => null));

describe('SpecEvolution component', () => {
  test('renders the release spec evolution branches before the main branch', async () => {
    // given a mocked successful spec evolution response with 2 release branches and a main branch
    const specEvolution = Generator.SpecEvolution.generateSpecEvolution({ numberReleaseBranches: 2 });
    const getInterfaceSpecEvolutionResult = {
      data: {
        specEvolution,
      },
    };
    useGetInterfaceSpecEvolutionMock.mockReturnValueOnce(getInterfaceSpecEvolutionResult);

    // when spec evolution component renders
    const { getByTestId } = renderWithRouter(<SpecEvolution />);

    // then an spec evolution container is found
    expect(getByTestId('spec-evolution-container')).toBeInTheDocument();

    // and the 3 SpecEvolutionBranch items should have been created
    expect(SpecEvolutionBranchContainerMock).toHaveBeenCalledTimes(3);

    // and the SpecEvolutionBranch main branch should have been rendered last
    expect(SpecEvolutionBranchContainerMock).toHaveBeenLastCalledWith(
      { evolutionBranch: { branchName: 'mainBranch', evolutionItems: [] }, isMain: true },
      {},
    );
  });


  test('renders loading placeholder when spec evolution data is loading', async () => {
    // given a spec log
    const specLog = Generator.SpecLog.generateSpecLog();

    // and a mocked spec evolution response that is not yet resolved
    const specEvolutionResult = {
      loading: true,
    };
    useGetInterfaceSpecEvolutionMock.mockReturnValueOnce(specEvolutionResult);

    // when spec evolution component renders
    const { getByTestId } = renderWithRouter(<SpecEvolution specLog={specLog} />);

    // then  it contains a place holder item
    expect(getByTestId('spec-evolution-placeholder')).toBeInTheDocument();
  });
});

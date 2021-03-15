import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import SpecEvolution from './spec-evolution';
import { renderWithRouter } from '../__tests__/test-utils';
import Generator from '../__tests__/test-data-generator';
import { useGetInterfaceSpecEvolution as useGetInterfaceSpecEvolutionMock } from '../backend-api-client';

jest.mock('../backend-api-client');

// // mock out the actual spec-file-item
// jest.mock('./spec-log', () => jest.fn(() => null));

describe('SpecEvolution component', () => {
  test('renders spec log items', async () => {
    // given a spec log containing two proposed changes
    const proposedChange1 = Generator.ProposedChange.generateChangeProposal({ number: 98 });
    const proposedChange2 = Generator.ProposedChange.generateChangeProposal({ number: 99 });
    const specLog = Generator.SpecLog.generateSpecLog({ proposedChanges: [proposedChange1, proposedChange2] });

    // when spec evolution component renders
    const { getByTestId, getByText } = renderWithRouter(<SpecEvolution specLog={specLog} />);

    // then an spec evolution container is found
    expect(getByTestId('spec-evolution-container')).toBeInTheDocument();

    // and the ref of the latest agreed spec file is shown
    expect(getByText(specLog.latestAgreed.ref)).toBeInTheDocument();

    // and the version of the latest agreed spec file is shown
    expect(getByText(specLog.latestAgreed.parseResult.openApiSpec.version)).toBeInTheDocument();

    // and the number of each proposed change PR is shown
    expect(getByText(`PR #${proposedChange1.pullRequest.number}`)).toBeInTheDocument();
    expect(getByText(`PR #${proposedChange2.pullRequest.number}`)).toBeInTheDocument();
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


  // test('renders tag name for evolution items with a tag', async () => {
  //   // given a spec log
  //   const specLog = Generator.SpecLog.generateSpecLog();

  //   // and a spec evolution with tags

  //   // and a mocked spec evolution response that is not yet resolved
  //   const specEvolutionResult = {
  //     data: {

  //     },
  //   };
  //   useGetInterfaceSpecEvolutionMock.mockReturnValueOnce(specEvolutionResult);

  //   // when spec evolution component renders
  //   const { getByTestId } = renderWithRouter(<SpecEvolution specLog={specLog} />);

  //   // then  it contains a place holder item
  //   expect(getByTestId('spec-evolution-placeholder')).toBeInTheDocument();
  // });
});

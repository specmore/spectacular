import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import SpecLog from './spec-log';
import { renderWithRouter } from '../__tests__/test-utils';
import LatestAgreedVersionMock from './latest-agreed-version';
import ProposedChangeItemMock from './proposed-change-item';
import Generator from '../__tests__/test-data-generator';

// mock out the actual implementations
jest.mock('./latest-agreed-version', () => jest.fn(() => null));
afterEach(() => {
  LatestAgreedVersionMock.mockClear();
});

jest.mock('./proposed-change-item', () => jest.fn(() => null));
afterEach(() => {
  ProposedChangeItemMock.mockClear();
});

describe('SpecLog component', () => {
  test("shows latest agreed spec item's openApiSpec title as header", async () => {
    // given a valid latest agreed spec item with title
    const specLog = Generator.SpecLog.generateSpecLog();

    // when spec log component renders
    const { getByText } = renderWithRouter(<SpecLog specLog={specLog} />);

    // then the open api spec title is shown
    expect(getByText(specLog.latestAgreed.parseResult.openApiSpec.title)).toBeInTheDocument();
  });

  test('shows latest agreed version component for valid latest agreed spec item', async () => {
    // given a valid latest agreed spec item with title
    const specLog = Generator.SpecLog.generateSpecLog();

    // when spec log component renders
    renderWithRouter(<SpecLog specLog={specLog} />);

    // then a latest agreed version item is shown
    expect(LatestAgreedVersionMock).toHaveBeenCalledTimes(1);
  });

  test('shows spec item error message for spec item with parse result errors', async () => {
    // given a spec item with parse errors
    const specItem = Generator.SpecItem.generateSpecItemWithError('The spec file could not be found.');

    const specLog = Generator.SpecLog.generateSpecLog({ latestAgreed: specItem });

    // when spec file item component renders
    const { getByText, getByTestId } = renderWithRouter(<SpecLog specLog={specLog} />);

    // then the file path suffixed by the repo name is shown
    expect(getByText('test-owner/specs-test/specs/example-template.yaml', { exact: false })).toBeInTheDocument();

    // and the spec file error item is shown with error message
    expect(getByTestId('spec-log-error')).toBeInTheDocument();
    expect(getByText('The spec file could not be found.')).toBeInTheDocument();
  });

  test('shows proposed changes list for one or many proposed changes', async () => {
    // given a spec log containing two proposed changes
    const pullRequest1 = Generator.PullRequest.generatePullRequest({ number: 98, branchName: 'proposal1' });
    const pullRequest2 = Generator.PullRequest.generatePullRequest({ number: 99, branchName: 'proposal2' });
    const proposedChange1 = Generator.ProposedChange.generateProposedChange({ pullRequest: pullRequest1 });
    const proposedChange2 = Generator.ProposedChange.generateProposedChange({ pullRequest: pullRequest2 });
    const specLog = Generator.SpecLog.generateSpecLog({ proposedChanges: [proposedChange1, proposedChange2] });

    // when spec file item component renders
    const { getByText } = renderWithRouter(<SpecLog specLog={specLog} />);

    // then a proposed changes heading is shown with a 2 changes count
    expect(getByText('2')).toBeInTheDocument();

    // then a proposed change item is shown for each proposal
    expect(ProposedChangeItemMock).toHaveBeenCalledTimes(2);
  });
});

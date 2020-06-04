import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import ProposedChangeItem from './proposed-change-item';
import { renderWithRouter } from '../__tests__/test-utils';
import SpecRevisionMock from './spec-revision';
import Generator from '../__tests__/test-data-generator';

// mock out the actual implementations
jest.mock('./spec-revision', () => jest.fn(() => null));
afterEach(() => {
  SpecRevisionMock.mockClear();
});

describe('ProposedChangeItem component', () => {
  test('shows pull request information for valid proposed item', async () => {
    // given a pull request
    const pullRequest = Generator.PullRequest.generatePullRequest({
      number: 1,
      title: 'example change to spec',
    });

    // and a proposed change for that pull request
    const proposedChange = Generator.ProposedChange.generateChangeProposal({ pullRequest });

    // when a proposed change item component is rendered with the given proposed item
    const { getByText } = renderWithRouter(<ProposedChangeItem
      pullRequest={proposedChange.pullRequest}
      specItem={proposedChange.specItem}
    />);

    // then the pull request number and title header is shown
    expect(getByText('#1', { exact: false })).toBeInTheDocument();
    expect(getByText('example change to spec', { exact: false })).toBeInTheDocument();

    // and a link to the pull request page on github is given
    expect(getByText('#1', { exact: false }).closest('a')).toHaveAttribute('href', 'https://github.com/test-owner/specs-test/pull/1');
  });

  test('shows pull request labels', async () => {
    // given a pull request
    const pullRequest = Generator.PullRequest.generatePullRequest({
      number: 1,
      title: 'example change to spec',
      labels: ['project-y'],
    });

    // and a proposed change for that pull request
    const proposedChange = Generator.ProposedChange.generateChangeProposal({ pullRequest });

    // when a proposed change item component is rendered with the given proposed item
    const { getByText } = renderWithRouter(<ProposedChangeItem
      pullRequest={proposedChange.pullRequest}
      specItem={proposedChange.specItem}
    />);

    // then the pull request number and title header is shown
    expect(getByText('project-y')).toBeInTheDocument();
  });

  test('shows a spec revision component for the proposed item', async () => {
    // given a pull request
    const pullRequest = Generator.PullRequest.generatePullRequest();

    // and a proposed change for that pull request
    const proposedChange = Generator.ProposedChange.generateChangeProposal({ pullRequest });

    // when the component is rendered
    renderWithRouter(<ProposedChangeItem pullRequest={proposedChange.pullRequest} specItem={proposedChange.specItem} />);

    // then the spec revision component is shown
    expect(SpecRevisionMock).toHaveBeenCalledTimes(1);
  });
});

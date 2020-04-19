import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import ProposedChangeItem from './proposed-change-item';
import { renderWithRouter } from '../__tests__/test-utils';
import SpecRevisionMock from './spec-revision';

// mock out the actual implementations
jest.mock('./spec-revision', () => jest.fn(() => null));
afterEach(() => {
  SpecRevisionMock.mockClear();
});

describe('ProposedChangeItem component', () => {
  test('shows pull request information for valid proposed item', async () => {
    // given a pull request
    const pullRequest = {
      repository: {
        owner: 'test-owner',
        name: 'specs-test',
        htmlUrl: 'https://github.com/test-owner/specs-test',
        nameWithOwner: 'test-owner/specs-test',
      },
      branchName: 'change-branch',
      number: 1,
      url: 'https://github.com/test-owner/specs-test/pull/1',
      labels: ['project-x'],
      changedFiles: ['specs/example-template.yaml'],
      title: 'example change to spec',
    };

    // and a proposed change for that pull request
    const proposedChange = {
      pullRequest,
      specItem: {},
    };

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
    // given a pull request with a label
    const pullRequest = {
      repository: {
        owner: 'test-owner',
        name: 'specs-test',
        htmlUrl: 'https://github.com/test-owner/specs-test',
        nameWithOwner: 'test-owner/specs-test',
      },
      branchName: 'change-branch',
      number: 1,
      url: 'https://github.com/test-owner/specs-test/pull/1',
      labels: ['project-x'],
      changedFiles: ['specs/example-template.yaml'],
      title: 'example change to spec',
    };

    // and a proposed change for that pull request
    const proposedChange = {
      pullRequest,
      specItem: {},
    };

    // when a proposed change item component is rendered with the given proposed item
    const { getByText } = renderWithRouter(<ProposedChangeItem
      pullRequest={proposedChange.pullRequest}
      specItem={proposedChange.specItem}
    />);

    // then the pull request number and title header is shown
    expect(getByText('project-x')).toBeInTheDocument();
  });

  test('shows a spec revision component for the proposed item', async () => {
    // given a pull request
    const pullRequest = {
      repository: {
        owner: 'test-owner',
        name: 'specs-test',
        htmlUrl: 'https://github.com/test-owner/specs-test',
        nameWithOwner: 'test-owner/specs-test',
      },
      branchName: 'change-branch',
      number: 1,
      url: 'https://github.com/test-owner/specs-test/pull/1',
      labels: ['project-x'],
      changedFiles: ['specs/example-template.yaml'],
      title: 'example change to spec',
    };

    // and a proposed change for that pull request with spec item
    const proposedChange = {
      pullRequest,
      specItem: {},
    };

    // when the component is rendered
    renderWithRouter(<ProposedChangeItem pullRequest={proposedChange.pullRequest} specItem={proposedChange.specItem} />);

    // then the spec revision component is shown
    expect(SpecRevisionMock).toHaveBeenCalledTimes(1);
  });
});

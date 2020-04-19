import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import SpecLog from './spec-log';
import { renderWithRouter } from '../common/test-utils';
import LatestAgreedVersionMock from './latest-agreed-version';
import ProposedChangeItemMock from './proposed-change-item';

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
    const specItem = {
      repository: {
        owner: 'test-owner', name: 'specs-test', htmlUrl: 'https://github.com/test-owner/specs-test', nameWithOwner: 'test-owner/specs-test',
      },
      filePath: 'specs/example-template.yaml',
      ref: 'master',
      parseResult: { openApiSpec: { title: 'An empty API spec', version: '0.1.0', operations: [] }, errors: [] },
    };

    const specLog = {
      latestAgreed: specItem,
      proposedChanges: [],
    };

    // when spec log component renders
    const { getByText } = renderWithRouter(<SpecLog specLog={specLog} />);

    // then the open api spec title is shown
    expect(getByText('An empty API spec')).toBeInTheDocument();
  });

  test('shows latest agreed version component for valid latest agreed spec item', async () => {
    // given a valid latest agreed spec item with title
    const specItem = {
      repository: {
        owner: 'test-owner', name: 'specs-test', htmlUrl: 'https://github.com/test-owner/specs-test', nameWithOwner: 'test-owner/specs-test',
      },
      filePath: 'specs/example-template.yaml',
      ref: 'master',
      parseResult: { openApiSpec: { title: 'An empty API spec', version: '0.1.0', operations: [] }, errors: [] },
    };

    const specLog = {
      latestAgreed: specItem,
      proposedChanges: [],
    };

    // when spec log component renders
    renderWithRouter(<SpecLog specLog={specLog} />);

    // then a latest agreed version item is shown
    expect(LatestAgreedVersionMock).toHaveBeenCalledTimes(1);
  });

  test('shows spec item error message for spec item with parse result errors', async () => {
    // given a spec item with parse errors
    const specItem = {
      repository: {
        owner: 'test-owner', name: 'specs-test', htmlUrl: 'https://github.com/test-owner/specs-test', nameWithOwner: 'test-owner/specs-test',
      },
      filePath: 'specs/example-template.yaml',
      ref: 'master',
      parseResult: { openApiSpec: null, errors: ['The spec file could not be found.'] },
    };

    const specLog = {
      latestAgreed: specItem,
      proposedChanges: [],
    };

    // when spec file item component renders
    const { getByText, getByTestId } = renderWithRouter(<SpecLog specLog={specLog} />);

    // then the file path suffixed by the repo name is shown
    expect(getByText('test-owner/specs-test/specs/example-template.yaml', { exact: false })).toBeInTheDocument();

    // and the spec file error item is shown with error message
    expect(getByTestId('spec-log-error')).toBeInTheDocument();
    expect(getByText('The spec file could not be found.')).toBeInTheDocument();
  });

  test('shows proposed changes list for one or many proposed changes', async () => {
    // given valid latest agreed spec item with title and version
    const specItem = {
      repository: {
        owner: 'test-owner', name: 'specs-test', htmlUrl: 'https://github.com/test-owner/specs-test', nameWithOwner: 'test-owner/specs-test',
      },
      filePath: 'specs/example-template.yaml',
      ref: 'master',
      parseResult: { openApiSpec: { title: 'An empty API spec', version: '0.1.0', operations: [] }, errors: [] },
    };

    // and a spec log containing two proposed changes
    const proposedSpec1 = JSON.parse(JSON.stringify(specItem));
    proposedSpec1.ref = 'proposal1';
    const proposedSpec2 = JSON.parse(JSON.stringify(specItem));
    proposedSpec2.ref = 'proposal2';
    const specLog = {
      latestAgreed: specItem,
      proposedChanges: [{ specItem: proposedSpec1 }, { specItem: proposedSpec2 }],
    };

    // when spec file item component renders
    const { getByText, getByTestId } = renderWithRouter(<SpecLog specLog={specLog} />);

    // then a proposed changes heading is shown with a 2 changes count
    expect(getByText('2')).toBeInTheDocument();

    // then a proposed change item is shown for each proposal
    expect(ProposedChangeItemMock).toHaveBeenCalledTimes(2);
  });
});

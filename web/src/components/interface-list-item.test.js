import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import InterfaceListItem from './interface-list-item';
import { renderWithRouter } from '../__tests__/test-utils';
import Generator from '../__tests__/test-data-generator';

describe('InterfaceListItem component', () => {
  test("shows latest agreed spec item's openApiSpec title and version", async () => {
    // given a valid latest agreed spec item with title
    const specLog = Generator.SpecLog.generateSpecLog();

    // when InterfaceListItem component renders
    const { getByText } = renderWithRouter(<InterfaceListItem specLog={specLog} />);

    // then the open api spec title is shown
    expect(getByText(specLog.latestAgreed.parseResult.openApiSpec.title)).toBeInTheDocument();

    // and the open api spec version is shown
    expect(getByText(specLog.latestAgreed.parseResult.openApiSpec.version)).toBeInTheDocument();
  });

  test('shows proposed changes count for multiple proposed changes', async () => {
    // given a spec log containing two proposed changes
    const proposedChange1 = Generator.ProposedChange.generateChangeProposal({ number: 98 });
    const proposedChange2 = Generator.ProposedChange.generateChangeProposal({ number: 99 });
    const specLog = Generator.SpecLog.generateSpecLog({ proposedChanges: [proposedChange1, proposedChange2] });

    // when spec file item component renders
    const { getByText } = renderWithRouter(<InterfaceListItem specLog={specLog} />);

    // then a proposed changes label is shown with a 2 changes count
    expect(getByText('2')).toBeInTheDocument();
  });

  test('shows spec item error message for spec item with parse result errors', async () => {
    // given a spec item with parse errors
    const specItem = Generator.SpecItem.generateSpecItemWithError('The spec file could not be found.');

    const specLog = Generator.SpecLog.generateSpecLog({ latestAgreed: specItem });

    // when InterfaceListItem component renders
    const { getByText, getByTestId } = renderWithRouter(<InterfaceListItem specLog={specLog} />);

    // then the file path suffixed by the repo name is shown
    expect(getByText('test-owner/specs-test/specs/example-template.yaml', { exact: false })).toBeInTheDocument();

    // and the spec file error item is shown with error message
    expect(getByTestId('spec-log-error')).toBeInTheDocument();
    expect(getByText('The spec file could not be found.')).toBeInTheDocument();
  });
});

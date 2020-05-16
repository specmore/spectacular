import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import moment from 'moment';
import SpecRevision from './spec-revision';
import { renderWithRouter } from '../__tests__/test-utils';
import Generator from '../__tests__/test-data-generator';

describe('SpecRevision component', () => {
  test('shows latest agreed spec item with branch name and version', async () => {
    // given a spec item with branch name and version
    const specItem = Generator.SpecItem.generateSpecItem();

    // when component renders
    const { getByText } = renderWithRouter(<SpecRevision specItem={specItem} />);

    // then the master branch is shown
    expect(getByText('master')).toBeInTheDocument();

    // and the open api spec version is shown
    expect(getByText('0.1.0')).toBeInTheDocument();
  });

  test('shows last modified date relative to now', async () => {
    // given a spec item with a last modified date 5 minutes ago
    const specItem = Generator.SpecItem.generateSpecItem();
    specItem.lastModified = moment().subtract(5, 'minutes').toISOString();

    // when component renders
    const { getByText } = renderWithRouter(<SpecRevision specItem={specItem} />);

    // then the master branch is shown
    expect(getByText('master')).toBeInTheDocument();

    // and the open api spec version is shown
    expect(getByText('0.1.0')).toBeInTheDocument();

    // and last modified date is shown
    expect(getByText(/5 minutes ago/i)).toBeInTheDocument();
  });

  test('shows the branch color specified', async () => {
    // given a spec item with branch name and version
    const specItem = Generator.SpecItem.generateSpecItem();

    // and a branch colour of yellow
    const branchColor = 'yellow';

    // when the component renders
    const { getByText } = renderWithRouter(<SpecRevision specItem={specItem} branchColor={branchColor} />);

    // then the master branch is shown in yellow
    expect(getByText('master')).toHaveClass('yellow');
  });

  test('defaults to the color olive if no branch color is specified', async () => {
    // given a spec item with branch name and version
    const specItem = Generator.SpecItem.generateSpecItem();

    // when the component renders
    const { getByText } = renderWithRouter(<SpecRevision specItem={specItem} />);

    // then the master branch is shown in olive
    expect(getByText('master')).toHaveClass('olive');
  });
});

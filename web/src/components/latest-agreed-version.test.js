import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import LatestAgreedVersion from './latest-agreed-version';
import { renderWithRouter } from '../__tests__/test-utils';
import SpecRevisionMock from './spec-revision';

// mock out the actual implementations
jest.mock('./spec-revision', () => jest.fn(() => null));
afterEach(() => {
  SpecRevisionMock.mockClear();
});

describe('LatestAgreedVersion component', () => {
  test('shows a spec revision component for latest agreed spec item with branch name and version', async () => {
    // given a valid latest agreed spec item with title and version
    const specItem = {};

    // when the latest agreed version component renders
    renderWithRouter(<LatestAgreedVersion latestAgreedSpecItem={specItem} />);

    // then the spec revision component is shown
    expect(SpecRevisionMock).toHaveBeenCalledTimes(1);
  });
});

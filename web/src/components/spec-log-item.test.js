import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import SpecLogItem from './spec-log-item';
import { renderWithRouter } from '../__tests__/test-utils';
import { CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE, CreateViewSpecLocation } from '../routes';
import Generator from '../__tests__/test-data-generator';

describe('SpecLogItem component', () => {
  test('uses selected class if browser location is for given spec item', async () => {
    // given a spec item
    const specItem = Generator.SpecItem.generateSpecItem();

    // and a browser location with the spec item selected
    const location = CreateViewSpecLocation('any-catalogue-encoded-id', specItem.id);

    // when the component renders
    const { getByTestId } = renderWithRouter(<SpecLogItem specItem={specItem} />, location, CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE);

    // then the tertiary class is set
    expect(getByTestId('spec-log-item-segment')).toHaveClass('selected');
  });

  test('shows a view spec button', async () => {
    // given a spec item
    const specItem = Generator.SpecItem.generateSpecItem();

    // when the component renders
    const { getByTestId } = renderWithRouter(<SpecLogItem specItem={specItem} />);

    // then the view spec button is shown
    expect(getByTestId('view-spec-button')).toBeInTheDocument();
  });
});

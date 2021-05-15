import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import TopicSelectionList from './topic-selection-list';
import { renderWithRouter } from '../../__tests__/test-utils';
import Generator from '../../__tests__/test-data-generator';

describe('TopicSelectionList component', () => {
  test('renders a unique list of topics across all catalogues', async () => {
    // given 3 catalogue data items with duplicate topics
    const catalogue1 = Generator.Catalogue.generateCatalogue({ topics: ['topic1', 'topic2'] });
    const catalogue2 = Generator.Catalogue.generateCatalogue({ topics: ['topic2', 'topic3'] });
    const catalogue3 = Generator.Catalogue.generateCatalogue({ topics: ['topic3', 'topic4'] });
    const catalogues = [catalogue1, catalogue2, catalogue3];

    // when TopicSelectionList component renders
    const { getByTestId, getAllByText } = renderWithRouter(<TopicSelectionList catalogues={catalogues} />);

    // then a TopicSelectionList container is found
    expect(getByTestId('topic-selection-list-container')).toBeInTheDocument();

    // and each topic is shown exactly once
    expect(getAllByText('topic1')).toHaveLength(1);
    expect(getAllByText('topic2')).toHaveLength(1);
    expect(getAllByText('topic3')).toHaveLength(1);
    expect(getAllByText('topic4')).toHaveLength(1);
  });

  test('can handle catalogues with empty or null topic lists', async () => {
    // given catalogue data items null or empty topic lists
    const catalogue1 = Generator.Catalogue.generateCatalogue({ topics: ['topic1', 'topic2'] });
    const catalogue2 = Generator.Catalogue.generateCatalogue({ topics: [] });
    const catalogue3 = Generator.Catalogue.generateCatalogue({ topics: null });
    const catalogues = [catalogue1, catalogue2, catalogue3];

    // when TopicSelectionList component renders
    const { getByTestId, getAllByText } = renderWithRouter(<TopicSelectionList catalogues={catalogues} />);

    // then a TopicSelectionList container is found
    expect(getByTestId('topic-selection-list-container')).toBeInTheDocument();

    // and each topic is shown exactly once
    expect(getAllByText('topic1')).toHaveLength(1);
    expect(getAllByText('topic2')).toHaveLength(1);
  });
});

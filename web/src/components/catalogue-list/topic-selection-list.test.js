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

  test('renders topics in the query param as checked', async () => {
    // given a catalogue with topics
    const catalogue1 = Generator.Catalogue.generateCatalogue({ topics: ['topic1', 'topic2'] });
    const catalogues = [catalogue1];

    // when TopicSelectionList component renders with topic1 in the selected topics query parameters
    const { getByText } = renderWithRouter(
      <TopicSelectionList catalogues={catalogues} />,
      '?topics=topic1',
    );

    // then the topic1 checkbox is checked
    expect(getByText('topic1').parentElement).toHaveClass('checked');

    // and the topic2 checkbox is not checked
    expect(getByText('topic2').parentElement).not.toHaveClass('checked');
  });
});

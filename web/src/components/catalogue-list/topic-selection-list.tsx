import React, { FunctionComponent } from 'react';
import { Checkbox, List } from 'semantic-ui-react';
import { Catalogue } from '../../backend-api-client';

interface CatalogueListProps {
  catalogues: Catalogue[];
}

const TopicSelectionList: FunctionComponent<CatalogueListProps> = ({ catalogues }) => {
  const topicReducer = (accumulator: Set<string>, currentValue: Catalogue) => {
    if (currentValue.topics) {
      currentValue.topics.map((topic) => accumulator.add(topic));
    }
    return accumulator;
  };
  const topics = catalogues.reduce(topicReducer, new Set<string>());

  return (
    <div data-testid="topic-selection-list-container">
      <h5>Topics</h5>
      <List>
        { [...topics].map((topic) => (
          <List.Item key={topic}>
            <Checkbox label={topic} />
          </List.Item>
        ))}
      </List>
    </div>
  );
};

export default TopicSelectionList;

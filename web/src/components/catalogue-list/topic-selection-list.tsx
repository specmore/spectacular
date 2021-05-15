import React, { FunctionComponent } from 'react';
import { Checkbox, List } from 'semantic-ui-react';
import { Catalogue } from '../../backend-api-client';
import { updateTopicSelection } from '../../routes';

interface TopicSelectionItemProps {
  topic: string;
}

const TopicSelectionItem: FunctionComponent<TopicSelectionItemProps> = ({ topic }) => {
  const handleCheckboxChanged = (e: unknown, { checked } : { checked: boolean }) => {
    updateTopicSelection(topic, checked);
  };

  return (
    <List.Item>
      <Checkbox label={topic} onChange={handleCheckboxChanged} />
    </List.Item>
  );
};

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
        { [...topics].map((topic) => (<TopicSelectionItem key={topic} topic={topic} />))}
      </List>
    </div>
  );
};

export default TopicSelectionList;

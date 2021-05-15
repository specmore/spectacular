import React, { FunctionComponent } from 'react';
import { Checkbox, List } from 'semantic-ui-react';
import { ArrayParam, useQueryParam } from 'use-query-params';
import { Catalogue } from '../../backend-api-client';
import { TOPIC_SELECTION_QUERY_PARAM_NAME } from '../../routes';

interface TopicSelectionItemProps {
  topic: string;
}

const TopicSelectionItem: FunctionComponent<TopicSelectionItemProps> = ({ topic }) => {
  const [topics, setTopics] = useQueryParam(TOPIC_SELECTION_QUERY_PARAM_NAME, ArrayParam);
  const isTopicSelected = topics ? topics.includes(topic) : false;

  const handleCheckboxChanged = (e: unknown, { checked } : { checked: boolean }) => {
    const selectedTopics = new Set(topics);

    if (checked) {
      selectedTopics.add(topic);
    } else {
      selectedTopics.delete(topic);
    }

    setTopics([...selectedTopics]);
  };

  return (
    <List.Item>
      <Checkbox label={topic} checked={isTopicSelected} onChange={handleCheckboxChanged} />
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

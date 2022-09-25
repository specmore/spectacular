import React, { FunctionComponent } from 'react';
import {
  Item, Segment, Message, Header, Container, Placeholder,
} from 'semantic-ui-react';
import { ArrayParam, useQueryParam } from 'use-query-params';
import CatalogueListItem from './catalogue-list-item';
import TopicSelectionList from './topic-selection-list';
import { useFindCataloguesForUser, Catalogue } from '../../backend-api-client';
import LocationBar from '../location-bar';
import './catalogue-list.less';
import { TOPIC_SELECTION_QUERY_PARAM_NAME } from '../../routes';

function CatalogueListLoading() {
  return (
    <Placeholder data-testid="catalogue-list-placeholder">
      <Placeholder.Header image>
        <Placeholder.Line />
        <Placeholder.Line />
      </Placeholder.Header>
    </Placeholder>
  );
}

interface CatalogueListErrorProps {
  errorMessage: string;
}

const CatalogueListError: FunctionComponent<CatalogueListErrorProps> = ({ errorMessage }) => (
  <Message negative>
    <Message.Header>{errorMessage}</Message.Header>
  </Message>
);

interface CatalogueListProps {
  installationId: number;
  catalogues: Catalogue[];
}

const CatalogueList: FunctionComponent<CatalogueListProps> = ({ installationId, catalogues }) => {
  const [selectedTopics] = useQueryParam(TOPIC_SELECTION_QUERY_PARAM_NAME, ArrayParam);
  const cataloguesFilteredByTopic = catalogues.filter((catalogue) => {
    if (!selectedTopics) return true;
    if (!catalogue.topics) return false;
    return selectedTopics.every((topic) => catalogue.topics.includes(topic));
  });

  return (
    <div className="catalogue-list-container">
      <div className="filter-container">
        <TopicSelectionList catalogues={cataloguesFilteredByTopic} />
      </div>
      <div className="catalogue-list">
        <Item.Group divided data-testid="catalogue-list-item-group">
          {cataloguesFilteredByTopic.map((catalogue) => (
            <CatalogueListItem key={catalogue.encodedId} installationId={installationId} catalogue={catalogue} />
          ))}
        </Item.Group>
      </div>
    </div>
  );
};

interface CatalogueListContainerProps {
  installationId: number;
  org: string;
}

const CatalogueListContainer: FunctionComponent<CatalogueListContainerProps> = ({ installationId, org }) => {
  const findCataloguesForUser = useFindCataloguesForUser({ installationId });
  const { data: findCataloguesResult, loading, error } = findCataloguesForUser;

  let content = null;
  if (loading) {
    content = (<CatalogueListLoading />);
  } else if (error) {
    content = (<CatalogueListError errorMessage={error.message} />);
  } else {
    content = (<CatalogueList installationId={installationId} catalogues={findCataloguesResult.catalogues} />);
  }

  return (
    <>
      <LocationBar installationId={installationId} installationOwner={org} />
      <Segment vertical>
        <Container>
          <Header as="h3">Available Interface Catalogues</Header>
          {content}
        </Container>
      </Segment>
    </>
  );
};

export default CatalogueListContainer;

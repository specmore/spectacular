import React, { FunctionComponent } from 'react';
import {
  Item, Segment, Message, Header, Container, Placeholder,
} from 'semantic-ui-react';
import CatalogueListItem from './catalogue-list-item';
import TopicSelectionList from './topic-selection-list';
import { useFindCataloguesForUser, Catalogue } from '../../backend-api-client';
import LocationBar from '../location-bar';
import './catalogue-list.less';

const CatalogueListLoading = () => (
  <Placeholder data-testid="catalogue-list-placeholder">
    <Placeholder.Header image>
      <Placeholder.Line />
      <Placeholder.Line />
    </Placeholder.Header>
  </Placeholder>
);

interface CatalogueListErrorProps {
  errorMessage: string;
}

const CatalogueListError: FunctionComponent<CatalogueListErrorProps> = ({ errorMessage }) => (
  <Message negative>
    <Message.Header>{errorMessage}</Message.Header>
  </Message>
);

interface CatalogueListProps {
  catalogues: Catalogue[];
}

const CatalogueList: FunctionComponent<CatalogueListProps> = ({ catalogues }) => (
  <div className="catalogue-list-container">
    <div className="filter-container cell">
      <TopicSelectionList catalogues={catalogues} />
    </div>
    <div className="cell">
      <Item.Group divided data-testid="catalogue-list-item-group">
        {catalogues.map((catalogue) => (<CatalogueListItem key={catalogue.encodedId} catalogue={catalogue} />))}
      </Item.Group>
    </div>
  </div>
);

interface CatalogueListContainerProps {
  org: string;
}

const CatalogueListContainer: FunctionComponent<CatalogueListContainerProps> = ({ org }) => {
  const findCataloguesForUser = useFindCataloguesForUser({ queryParams: { org } });
  const { data: findCataloguesResult, loading, error } = findCataloguesForUser;

  let content = null;
  if (loading) {
    content = (<CatalogueListLoading />);
  } else if (error) {
    content = (<CatalogueListError errorMessage={error.message} />);
  } else {
    content = (<CatalogueList catalogues={findCataloguesResult.catalogues} />);
  }

  return (
    <>
      <LocationBar installationOwner={org} />
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

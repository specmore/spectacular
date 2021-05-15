import React, { FunctionComponent } from 'react';
import { Catalogue } from '../../backend-api-client';

interface CatalogueListProps {
  catalogues: Catalogue[];
}

const TopicSelectionList: FunctionComponent<CatalogueListProps> = ({ catalogues }) => {
  const topics = catalogues.map((catalogue) => catalogue.topics);

  return (
    <h5>Topics</h5>
  );
};

export default TopicSelectionList;

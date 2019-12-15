import React, { useState } from "react";
import { Item } from 'semantic-ui-react'


const CatalogueItem = ({name, repo}) => (
  <Item>
    <Item.Content>
      <Item.Header>{name}</Item.Header>
      <Item.Meta>...</Item.Meta>
      <Item.Description>
          ...
      </Item.Description>
      <Item.Extra>{repo}</Item.Extra>
    </Item.Content>
  </Item>
);

const CatalogueList = () => {
  const [catalogues, setCatalogues] = useState([{
    "repo": "pburls/specs-test",
    "name": "Test Catalogue 1"
}]);
  const [isLoading, setIsLoading] = useState([]);


  return (
    <Item.Group>
      {catalogues.map(catalogue => (<CatalogueItem key={catalogue.repo} {...catalogue} />))}
    </Item.Group>
  );
};

export default CatalogueList;
import React from "react";
import { Image, Item } from 'semantic-ui-react'

const CatalogueList = () => (
  <Item.Group>
    <Item>
      <Item.Content>
        <Item.Header>Header 1</Item.Header>
        <Item.Meta>Meta</Item.Meta>
        <Item.Description>
            Description1
        </Item.Description>
        <Item.Extra>Additional Details</Item.Extra>
      </Item.Content>
    </Item>

    <Item>
      <Item.Content>
        <Item.Header>Header 2</Item.Header>
        <Item.Meta>Meta</Item.Meta>
        <Item.Description>
            Description2
        </Item.Description>
        <Item.Extra>Additional Details</Item.Extra>
      </Item.Content>
    </Item>
  </Item.Group>
);

export default CatalogueList;
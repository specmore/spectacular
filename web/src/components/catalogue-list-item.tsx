import React, { FunctionComponent } from 'react';
import {
  Label, Icon, Item, Message,
} from 'semantic-ui-react';
import { Link } from 'react-router-dom';
import { CreateCatalogueContainerLocation } from '../routes';
import { Catalogue } from '../backend-api-client';

interface CatalogueErrorItemProps {
  catalogue: Catalogue;
}

const CatalogueErrorItem: FunctionComponent<CatalogueErrorItemProps> = ({ catalogue }) => (
  <Item data-testid="catalogue-list-item-error-item">
    <Item.Content>
      <Item.Description>
        <Message icon negative>
          <Icon name="warning sign" />
          <Message.Content>
            <Message.Header>
              An error occurred while parsing the catalogue manifest file
              {' '}
              <a href={catalogue.htmlUrl} target="_blank" rel="noopener noreferrer">{catalogue.fullPath}</a>
            </Message.Header>
            {catalogue.parseError}
          </Message.Content>
        </Message>
      </Item.Description>
    </Item.Content>
  </Item>
);

interface CatalogueItemDetailsProps {
  catalogue: Catalogue;
  catalogueLink: string;
}

const CatalogueItemDetails: FunctionComponent<CatalogueItemDetailsProps> = ({ catalogue, catalogueLink }) => (
  <Item data-testid="catalogue-list-item-details-item">
    <Item.Content>
      <Item.Header as={Link} to={catalogueLink}>{catalogue.title}</Item.Header>
      <Item.Meta>
        {catalogue.topics && catalogue.topics.map((topic) => (<Label key={topic} color="grey">{topic}</Label>))}
      </Item.Meta>
      <Item.Description>
        {catalogue.description}
      </Item.Description>
      <Item.Extra>
        <Label color="green">
          <Icon name="file alternate" />
          {catalogue.interfaceCount}
          {' '}
          specs
        </Label>
      </Item.Extra>
    </Item.Content>
  </Item>
);

interface CatalogueListItemProps {
  catalogue: Catalogue;
}

const CatalogueListItem: FunctionComponent<CatalogueListItemProps> = ({ catalogue }) => {
  const catalogueLink = CreateCatalogueContainerLocation(catalogue.encodedId);

  if (catalogue.parseError) return (<CatalogueErrorItem catalogue={catalogue} />);

  return (
    <CatalogueItemDetails
      catalogue={catalogue}
      catalogueLink={catalogueLink}
    />
  );
};

export default CatalogueListItem;

import React from 'react';
import {
  Label, Icon, Item, Message,
} from 'semantic-ui-react';
import ImagePlaceHolder from '../assets/images/image-placeholder.png';
import { CatalogueContainerLinkButton } from '../routes';

const CatalogueErrorItem = ({ error, catalogueId }) => (
  <Item data-testid="catalogue-list-item-error-item">
    <Item.Content>
      <Item.Description>
        <Message icon negative>
          <Icon name="warning sign" />
          <Message.Content>
            <Message.Header>
              An error occurred while parsing the catalogue manifest file in
              {' '}
              <a href={catalogueId.repository.htmlUrl} target="_blank" rel="noopener noreferrer">{catalogueId.repository.nameWithOwner}</a>
            </Message.Header>
            {error}
          </Message.Content>
        </Message>
      </Item.Description>
    </Item.Content>
  </Item>
);

const CatalogueItemDetails = ({ catalogueId, catalogueManifest, selectButton }) => (
  <Item data-testid="catalogue-list-item-details-item">
    <Item.Image size="tiny" src={ImagePlaceHolder} />
    <Item.Content>
      <Item.Header>{catalogueManifest.name}</Item.Header>
      <Item.Description>
        {catalogueManifest.description}
      </Item.Description>
      <Item.Extra>
        {selectButton}
        <Label as="a" href={catalogueId.repository.htmlUrl} target="_blank">
          <Icon name="github" />
          {catalogueId.repository.nameWithOwner}
        </Label>
        <Label color="teal">
          <Icon name="file alternate" />
          {catalogueManifest['spec-files'] ? catalogueManifest['spec-files'].length : 0}
          {' '}
          specs
        </Label>
      </Item.Extra>
    </Item.Content>
  </Item>
);

const CatalogueListItem = ({ catalogue }) => {
  const selectButton = (<CatalogueContainerLinkButton catalogueId={catalogue.id} />);

  if (catalogue.error) return (<CatalogueErrorItem error={catalogue.error} catalogueId={catalogue.id} />);

  return (
    <CatalogueItemDetails
      catalogueId={catalogue.id}
      catalogueManifest={catalogue.catalogueManifest}
      selectButton={selectButton}
    />
  );
};

export default CatalogueListItem;

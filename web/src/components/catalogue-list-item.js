import React from 'react';
import {
  Label, Icon, Item, Message,
} from 'semantic-ui-react';
import ImagePlaceHolder from '../assets/images/image-placeholder.png';
import { CatalogueContainerLinkButton } from '../routes';

const CatalogueErrorItem = ({ error, repository }) => (
  <Item data-testid="catalogue-list-item-error-item">
    <Item.Content>
      <Item.Description>
        <Message icon negative>
          <Icon name="warning sign" />
          <Message.Content>
            <Message.Header>
              An error occurred while parsing the catalogue manifest file in
              <a href={repository.htmlUrl} target="_blank">{repository.nameWithOwner}</a>
            </Message.Header>
            {error}
          </Message.Content>
        </Message>
      </Item.Description>
    </Item.Content>
  </Item>
);

const CatalogueItemDetails = ({ repository, catalogueManifest, selectButton }) => (
  <Item data-testid="catalogue-list-item-details-item">
    <Item.Image size="tiny" src={ImagePlaceHolder} />
    <Item.Content>
      <Item.Header>{catalogueManifest.name}</Item.Header>
      <Item.Description>
        {catalogueManifest.description}
      </Item.Description>
      <Item.Extra>
        {selectButton}
        <Label as="a" href={repository.htmlUrl} target="_blank">
          <Icon name="github" />
          {repository.nameWithOwner}
        </Label>
        <Label color="teal">
          <Icon name="file alternate" />
          {catalogueManifest['spec-files'].length}
          {' '}
          specs
        </Label>
      </Item.Extra>
    </Item.Content>
  </Item>
);

const CatalogueListItem = ({ catalogue }) => {
  const selectButton = (<CatalogueContainerLinkButton repository={catalogue.repository} />);

  if (catalogue.error) return (<CatalogueErrorItem error={catalogue.error} repository={catalogue.repository} />);

  return (<CatalogueItemDetails repository={catalogue.repository} catalogueManifest={catalogue.catalogueManifest} selectButton={selectButton} />);
};

export default CatalogueListItem;

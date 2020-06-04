import React, { FunctionComponent } from 'react';
import {
  Label, Icon, Item, Message,
} from 'semantic-ui-react';
import ImagePlaceHolder from '../assets/images/image-placeholder.png';
import { CatalogueContainerLinkButton } from '../routes';
import { Catalogue } from '../__generated__/backend-api-client';

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
  selectButton: JSX.Element;
}

const CatalogueItemDetails: FunctionComponent<CatalogueItemDetailsProps> = ({ catalogue, selectButton }) => (
  <Item data-testid="catalogue-list-item-details-item">
    <Item.Image size="tiny" src={ImagePlaceHolder} />
    <Item.Content>
      <Item.Header>{catalogue.title}</Item.Header>
      <Item.Description>
        {catalogue.description}
      </Item.Description>
      <Item.Extra>
        {selectButton}
        <Label as="a" href={catalogue.htmlUrl} target="_blank">
          <Icon name="github" />
          {catalogue.fullPath}
        </Label>
        <Label color="teal">
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
  const selectButton = (<CatalogueContainerLinkButton encodedId={catalogue.encodedId} />);

  if (catalogue.parseError) return (<CatalogueErrorItem catalogue={catalogue} />);

  return (
    <CatalogueItemDetails
      catalogue={catalogue}
      selectButton={selectButton}
    />
  );
};

export default CatalogueListItem;

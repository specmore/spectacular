import React from "react";
import { Label, Icon, Item, Message } from 'semantic-ui-react'
import ImagePlaceHolder from '../assets/images/image-placeholder.png';

const CatalogueErrorItem = ({error, repository}) => (
    <Item data-testid='catalogue-list-item-error-item'>
      <Item.Content>
        <Item.Description>
          <Message icon negative>
            <Icon name='warning sign' />
            <Message.Content>
              <Message.Header>An error occurred while parsing the catalogue manifest file in <a href={repository.htmlUrl} target='_blank'>{repository.nameWithOwner}</a></Message.Header>
              {error}
            </Message.Content>
          </Message>
        </Item.Description>
      </Item.Content>
    </Item>
  );
  
  const CatalogueDetailsItem = ({repository, catalogueManifest, selectButton}) => (
    <Item data-testid='catalogue-list-item-details-item'>
      <Item.Image size='tiny' src={ImagePlaceHolder} />
      <Item.Content>
        <Item.Header>{catalogueManifest.name}</Item.Header>
        <Item.Meta><a href={repository.htmlUrl} target='_blank'><Icon name="github"/> {repository.nameWithOwner}</a></Item.Meta>
        <Item.Description>
          {catalogueManifest.description}
        </Item.Description>
        <Item.Extra>
          {selectButton}
          <Label color='teal'>
            <Icon name='file alternate' />{catalogueManifest["spec-files"].length} specs
          </Label>
        </Item.Extra>
      </Item.Content>
    </Item>
  );

  const CatalogueDetails = ({repository, catalogueManifest, error}) => {  
    if (error) return (<CatalogueErrorItem error={error} repository={repository} />);
  
    return (<CatalogueDetailsItem repository={repository} catalogueManifest={catalogueManifest} />);
  };

  export default CatalogueDetails;
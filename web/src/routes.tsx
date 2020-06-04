import React, { FunctionComponent } from 'react';
import { Button, Icon } from 'semantic-ui-react';
import { Link, useParams } from 'react-router-dom';

export const CATALOGUE_LIST_ROUTE = '/';

export const CATALOGUE_CONTAINER_ROUTE = '/catalogue/:encodedId';
export const CreateCatalogueContainerLocation = (encodedId: string): string => `/catalogue/${encodedId}/`;

export const CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE = '/catalogue/:encodedId/file/*';
export const CreateViewSpecLocation = (encodedId: string, specItemId: string): string => (
  `/catalogue/${encodedId}/file/${specItemId}`
);

export const BackToCatalogueListLinkButton: FunctionComponent = () => (
  <Button icon compact labelPosition="left" as={Link} to={CATALOGUE_LIST_ROUTE} data-testid="back-to-catalogue-list-button">
    Catalogue List
    {' '}
    <Icon name="chevron left" />
  </Button>
);

interface CatalogueContainerLinkButtonProps {
  encodedId: string;
}

export const CatalogueContainerLinkButton: FunctionComponent<CatalogueContainerLinkButtonProps> = ({ encodedId }) => {
  const catalogueLink = CreateCatalogueContainerLocation(encodedId);
  return (
    <Button primary floated="right" icon labelPosition="right" as={Link} to={catalogueLink} data-testid="view-catalogue-button">
      View Catalogue
      {' '}
      <Icon name="chevron right" />
    </Button>
  );
};

interface ViewSpecLinkButtonProps {
  specItemId: string;
  isSelected: boolean;
}

export const ViewSpecLinkButton: FunctionComponent<ViewSpecLinkButtonProps> = ({ specItemId, isSelected }) => {
  const { encodedId } = useParams();
  const viewSpecLink = CreateViewSpecLocation(encodedId, specItemId);
  return (
    <Button
      primary
      compact
      floated="right"
      icon
      labelPosition="right"
      as={Link}
      to={viewSpecLink}
      disabled={isSelected}
      data-testid="view-spec-button"
    >
      View Spec
      {' '}
      <Icon name="chevron right" />
    </Button>
  );
};

export const CloseSpecButton: FunctionComponent = () => {
  const { encodedId } = useParams();
  const catalogueOnlyLink = CreateCatalogueContainerLocation(encodedId);
  return (
    <Button icon compact floated="right" labelPosition="right" as={Link} to={catalogueOnlyLink} data-testid="close-spec-button">
      Close
      {' '}
      <Icon name="close" />
    </Button>
  );
};

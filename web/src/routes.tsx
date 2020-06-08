import React, { FunctionComponent } from 'react';
import { Button, Icon } from 'semantic-ui-react';
import { Link, useParams, useLocation } from 'react-router-dom';

export const CATALOGUE_LIST_ROUTE = '/';

export const CATALOGUE_CONTAINER_ROUTE = '/catalogue/:encodedId';
export const CreateCatalogueContainerLocation = (encodedId: string): string => `/catalogue/${encodedId}/`;

export const CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE = '/catalogue/:encodedId/interface/:interfaceName';
export const CreateViewSpecLocation = (encodedId: string, interfaceName: string, refName: string): string => (
  `/catalogue/${encodedId}/interface/${interfaceName}?ref=${refName}`
);

export const useQuery = (): URLSearchParams => new URLSearchParams(useLocation().search);

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
  interfaceName: string;
  refName: string;
  isSelected: boolean;
}

export const ViewSpecLinkButton: FunctionComponent<ViewSpecLinkButtonProps> = ({ interfaceName, refName, isSelected }) => {
  const { encodedId } = useParams();
  const viewSpecLink = CreateViewSpecLocation(encodedId, interfaceName, refName);
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

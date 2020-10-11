import React, { FunctionComponent } from 'react';
import { Button, Icon } from 'semantic-ui-react';
import { Link, useParams, useLocation } from 'react-router-dom';

export const CATALOGUE_LIST_ROUTE = '/';

export const CATALOGUE_CONTAINER_ROUTE = '/catalogue/:encodedId';
export const CreateCatalogueContainerLocation = (encodedId: string): string => `/catalogue/${encodedId}/`;

export const CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE = '/catalogue/:encodedId/interface/:interfaceName';
export const CreateInterfaceLocation = (encodedId: string, interfaceName: string): string => (
  `/catalogue/${encodedId}/interface/${interfaceName}`
);

export const useQuery = (): URLSearchParams => new URLSearchParams(useLocation().search);

const addQueryParam = (name: string, value: string): string => {
  const { pathname } = useLocation();
  const search = new URLSearchParams(useLocation().search);
  search.set(name, value);
  return `${pathname}?${search.toString()}`;
};

const removeQueryParam = (name: string): string => {
  const { pathname } = useLocation();
  const search = new URLSearchParams(useLocation().search);
  search.delete(name);
  return `${pathname}?${search.toString()}`;
};

export const CreateViewSpecLocation = (refName: string): string => addQueryParam('ref', refName);

export const BackToCatalogueListLinkButton: FunctionComponent = () => (
  <Button icon compact labelPosition="left" as={Link} to={CATALOGUE_LIST_ROUTE} data-testid="back-to-catalogue-list-button">
    Catalogue List
    {' '}
    <Icon name="chevron left" />
  </Button>
);

interface ViewSpecLinkButtonProps {
  refName: string;
  isSelected: boolean;
}

export const ViewSpecLinkButton: FunctionComponent<ViewSpecLinkButtonProps> = ({ refName, isSelected }) => {
  const viewSpecLink = CreateViewSpecLocation(refName);
  return (
    <Button
      icon
      circular
      size="mini"
      labelPosition="right"
      as={Link}
      to={viewSpecLink}
      disabled={isSelected}
      data-testid="view-spec-button"
    >
      View Spec
      <Icon name="eye" />
    </Button>
  );
};

export const CloseSpecButton: FunctionComponent = () => {
  const interfaceOnlyLink = removeQueryParam('ref');
  return (
    <Button
      icon
      circular
      size="mini"
      floated="right"
      labelPosition="right"
      as={Link}
      to={interfaceOnlyLink}
      data-testid="close-spec-button"
    >
      Close Preview
      <Icon name="close" />
    </Button>
  );
};

// export const ViewSpecEvolutionLinkButton: FunctionComponent<ViewSpecLinkButtonProps> = ({ interfaceName }) => {
//   const { encodedId } = useParams();
//   const viewSpecLink = CreateViewSpecLocation(encodedId, interfaceName);
//   return (
//     <Button
//       icon
//       circular
//       size="mini"
//       labelPosition="right"
//       as={Link}
//       to={viewSpecLink}
//       data-testid="view-spec-evolution-button"
//     >
//       View Changes
//       <Icon name="eye" />
//     </Button>
//   );
// };

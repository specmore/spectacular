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

const VIEW_SPEC_QUERY_PARAM_NAME = 'ref';

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

export const BackToCatalogueListLinkButton: FunctionComponent = () => (
  <Button icon compact labelPosition="left" as={Link} to={CATALOGUE_LIST_ROUTE} data-testid="back-to-catalogue-list-button">
    Catalogue List
    {' '}
    <Icon name="chevron left" />
  </Button>
);

interface ViewSpecLinkButtonProps {
  refName: string;
  interfaceName: string;
}

export const ViewSpecLinkButton: FunctionComponent<ViewSpecLinkButtonProps> = ({ refName, interfaceName }) => {
  const { interfaceName: selectedInterfaceName } = useParams();
  const isSelected = interfaceName === selectedInterfaceName && useQuery().get(VIEW_SPEC_QUERY_PARAM_NAME) === refName;

  const viewSpecLink = addQueryParam(VIEW_SPEC_QUERY_PARAM_NAME, refName);
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
  const interfaceOnlyLink = removeQueryParam(VIEW_SPEC_QUERY_PARAM_NAME);
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

export const getCurrentSpecRefViewed = (): string => useQuery().get(VIEW_SPEC_QUERY_PARAM_NAME);

export const ViewSpecEvolutionLinkButton: FunctionComponent = () => {
  const expandSpecEvolutionLocation = addQueryParam('show-evolution', 'true');
  return (
    <Button
      icon
      circular
      size="mini"
      labelPosition="right"
      as={Link}
      to={expandSpecEvolutionLocation}
      data-testid="view-spec-evolution-button"
    >
      View Changes
      <Icon name="code branch" />
    </Button>
  );
};

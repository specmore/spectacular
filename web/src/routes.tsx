import React, { FunctionComponent } from 'react';
import { Button, Icon } from 'semantic-ui-react';
import { Link, useParams, useLocation } from 'react-router-dom';
import { SpecItem } from './backend-api-client';

export const CATALOGUE_LIST_ROUTE = '/';

export const CATALOGUE_CONTAINER_ROUTE = '/catalogue/:encodedId';
export const CreateCatalogueContainerLocation = (encodedId: string): string => `/catalogue/${encodedId}/`;

export const CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE = '/catalogue/:encodedId/interface/:interfaceName';
export const CreateInterfaceLocation = (encodedId: string, interfaceName: string): string => (
  `/catalogue/${encodedId}/interface/${interfaceName}`
);

export const VIEW_SPEC_QUERY_PARAM_NAME = 'ref';
export const SHOW_EVOLUTION_QUERY_PARAM_NAME = 'show-evolution';

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
  withoutLabel? : boolean;
}

export const ViewSpecLinkButton: FunctionComponent<ViewSpecLinkButtonProps> = ({ refName, withoutLabel = false }) => {
  const isSelected = useQuery().get(VIEW_SPEC_QUERY_PARAM_NAME) === refName;

  const viewSpecLink = addQueryParam(VIEW_SPEC_QUERY_PARAM_NAME, refName);

  const labelPosition = withoutLabel ? null : 'right';
  const labelText = withoutLabel ? null : 'View Spec';

  return (
    <Button
      icon
      circular
      size="mini"
      labelPosition={labelPosition}
      as={Link}
      to={viewSpecLink}
      disabled={isSelected}
      data-testid="view-spec-button"
      floated="right"
    >
      { labelText }
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
  const expandSpecEvolutionLocation = addQueryParam(SHOW_EVOLUTION_QUERY_PARAM_NAME, 'true');
  const isSelected = useQuery().get(SHOW_EVOLUTION_QUERY_PARAM_NAME) === 'true';

  return (
    <Button
      icon
      circular
      size="mini"
      labelPosition="right"
      as={Link}
      to={expandSpecEvolutionLocation}
      disabled={isSelected}
      data-testid="view-spec-evolution-button"
      floated="right"
    >
      View Changes
      <Icon name="code branch" />
    </Button>
  );
};

export const CloseSpecEvolutionButton: FunctionComponent = () => {
  const interfaceOnlyLink = removeQueryParam(SHOW_EVOLUTION_QUERY_PARAM_NAME);
  return (
    <Button
      icon="close"
      circular
      size="mini"
      floated="right"
      as={Link}
      to={interfaceOnlyLink}
      data-testid="close-spec-evolution-button"
    />
  );
};

export const isShowSpecEvolution = (): boolean => useQuery().get(SHOW_EVOLUTION_QUERY_PARAM_NAME) === 'true';


interface OpenSpecItemContentPageButtonProps {
  specItem: SpecItem;
}
export const OpenSpecItemContentPageButton: FunctionComponent<OpenSpecItemContentPageButtonProps> = ({ specItem }) => (
  <Button
    icon="file code"
    circular
    size="mini"
    href={specItem.htmlUrl}
    target="_blank"
    rel="noopener noreferrer"
    color="grey"
    floated="right"
  />
);

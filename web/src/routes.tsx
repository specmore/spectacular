import React, { FunctionComponent } from 'react';
import { Button, Icon, Popup } from 'semantic-ui-react';
import { Link, useLocation } from 'react-router-dom';
import { SpecItem } from './backend-api-client';

export const GITHUB_LOGIN_ROUTE = '/login/github';

export const INSTALLATION_LIST_ROUTE = '/';

export const INSTALLATION_CONTAINER_ROUTE = '/:org';
export const CreateInstallationContainerLocation = (org: string): string => `/${org}/`;

export const CATALOGUE_CONTAINER_ROUTE = '/catalogue/:encodedId';
export const CreateCatalogueContainerLocation = (encodedId: string): string => `/catalogue/${encodedId}/`;

export const CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE = '/catalogue/:encodedId/interface/:interfaceName';
export const CreateInterfaceLocation = (encodedId: string, interfaceName: string): string => (
  `/catalogue/${encodedId}/interface/${interfaceName}`
);

export const LOGIN_REDIRECT_RETURN_TO_PARAM_NAME = 'backTo';

export const VIEW_SPEC_QUERY_PARAM_NAME = 'ref';
export const TOPIC_SELECTION_QUERY_PARAM_NAME = 'topics';

export const SHOW_EVOLUTION_QUERY_PARAM_NAME = 'show-evolution';
export enum SHOW_EVOLUTION_QUERY_PARAM_VALUES {
  SHOW = 'true',
  SHOW_WITH_PREVIOUS_VERSIONS = 'with-previous-versions',
}

export const useQuery = (): URLSearchParams => new URLSearchParams(useLocation().search);

const addQueryParam = (name: string, value: string): string => {
  const { pathname, search } = useLocation();
  const searchParams = new URLSearchParams(search);
  searchParams.set(name, value);
  return `${pathname}?${searchParams.toString()}`;
};

const removeQueryParam = (name: string): string => {
  const { pathname, search } = useLocation();
  const searchParams = new URLSearchParams(search);
  searchParams.delete(name);
  return `${pathname}?${searchParams.toString()}`;
};

export const redirectToLogin = (): void => {
  console.debug('expired token');
  console.debug(`current location:${window.location.pathname}`);

  const redirectParams = new URLSearchParams();
  redirectParams.append(LOGIN_REDIRECT_RETURN_TO_PARAM_NAME, window.location.pathname);

  window.location.assign(`${GITHUB_LOGIN_ROUTE}?${redirectParams.toString()}`);
};

export const extractLoginRedirectReturnToPath = (): string => {
  const { search } = useLocation();
  const redirectParams = new URLSearchParams(search);
  return redirectParams.get(LOGIN_REDIRECT_RETURN_TO_PARAM_NAME);
};

export const extractLoginCallbackURL = (): string => {
  const { location } = window;
  return `${location.protocol}//${location.host}${location.pathname}`;
};

// export const BackToCatalogueListLinkButton: FunctionComponent = () => (
//   <Button icon compact labelPosition="left" as={Link} to={CATALOGUE_LIST_ROUTE} data-testid="back-to-catalogue-list-button">
//     Catalogue List
//     {' '}
//     <Icon name="chevron left" />
//   </Button>
// );

interface ViewSpecLinkButtonProps {
  refName: string;
  withoutLabel? : boolean;
}

export const ViewSpecLinkButton: FunctionComponent<ViewSpecLinkButtonProps> = ({ refName, withoutLabel = false }) => {
  const isSelected = useQuery().get(VIEW_SPEC_QUERY_PARAM_NAME) === refName;

  const viewSpecLink = addQueryParam(VIEW_SPEC_QUERY_PARAM_NAME, refName);

  if (withoutLabel) {
    return (
      <Popup
        content="View Spec"
        trigger={(
          <Button
            icon
            circular
            size="mini"
            as={Link}
            to={viewSpecLink}
            disabled={isSelected}
            data-testid="view-spec-button"
            floated="right"
          >
            <Icon name="eye" />
          </Button>
        )}
      />
    );
  }

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
      floated="right"
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

export const isShowSpecEvolution = (): boolean => useQuery().get(SHOW_EVOLUTION_QUERY_PARAM_NAME) != null;

export const ViewSpecEvolutionLinkButton: FunctionComponent = () => {
  const expandSpecEvolutionLocation = addQueryParam(SHOW_EVOLUTION_QUERY_PARAM_NAME, SHOW_EVOLUTION_QUERY_PARAM_VALUES.SHOW);
  const isSelected = isShowSpecEvolution();

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

export const isShowSpecEvolutionPreviousVersions = (): boolean => useQuery().get(SHOW_EVOLUTION_QUERY_PARAM_NAME)
=== SHOW_EVOLUTION_QUERY_PARAM_VALUES.SHOW_WITH_PREVIOUS_VERSIONS;

export const ShowSpecEvolutionPreviousVersionsToggleButton: FunctionComponent = () => {
  const isShowing = isShowSpecEvolutionPreviousVersions();
  const newQueryParamValue = isShowing ? SHOW_EVOLUTION_QUERY_PARAM_VALUES.SHOW
    : SHOW_EVOLUTION_QUERY_PARAM_VALUES.SHOW_WITH_PREVIOUS_VERSIONS;
  const toggleLocation = addQueryParam(SHOW_EVOLUTION_QUERY_PARAM_NAME, newQueryParamValue);
  const angleIconDirection = isShowing ? 'angle down' : 'angle right';

  return (
    <Button
      icon
      circular
      size="mini"
      as={Link}
      to={toggleLocation}
      data-testid="show-spec-evolution-previous-versions-toggle-button"
      floated="right"
    >
      <Icon name={angleIconDirection} />
    </Button>
  );
};

interface OpenSpecItemContentPageButtonProps {
  specItem: SpecItem;
}
export const OpenSpecItemContentPageButton: FunctionComponent<OpenSpecItemContentPageButtonProps> = ({ specItem }) => (
  <Popup
    content="Open Interface Spec File"
    trigger={(
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
    )}
  />
);

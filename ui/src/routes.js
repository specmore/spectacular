import React from "react";
import { Button, Icon } from 'semantic-ui-react'
import { Link, useParams } from "react-router-dom";

export const CATALOGUE_CONTAINER_ROUTE = "/catalogue/:owner/:repo";
export const CreateCatalogueContainerLocation = (owner, repo) => `/catalogue/${owner}/${repo}/`;

export const CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE = "/catalogue/:owner/:repo/file/*";
export const CreateViewSpecLocation = (owner, repo, location) => `/catalogue/${owner}/${repo}/file/${location}`;


export const CatalogueContainerLinkButton = ({repository}) => {
    const catalogueLink = CreateCatalogueContainerLocation(repository.owner, repository.name);
    return (
        <Button primary floated='right' icon labelPosition='right' as={Link} to={catalogueLink} data-testid='view-catalogue-button'>
            View Catalogue <Icon name='right chevron' />
        </Button>
    );
}

export const ViewSpecLinkButton = ({specFileLocation}) => {
    const { owner, repo } = useParams();
    const viewSpecLink = CreateViewSpecLocation(owner, repo, specFileLocation);
    return (
        <Button primary floated='right' icon labelPosition='right' as={Link} to={viewSpecLink} data-testid='view-spec-button'>
            View Spec <Icon name='right chevron' />
        </Button>
    );
}
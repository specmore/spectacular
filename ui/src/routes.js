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

export const ViewSpecLinkButton = ({specFileLocation, isSelected}) => {
    const { owner, repo } = useParams();
    const viewSpecLink = CreateViewSpecLocation(owner, repo, specFileLocation);
    return (
        <Button primary compact floated='right' icon labelPosition='right' as={Link} to={viewSpecLink} disabled={isSelected} data-testid='view-spec-button'>
            View Spec <Icon name='right chevron' />
        </Button>
    );
}

export const CloseSpecButton = () => {
    const { owner, repo } = useParams();
    const catalogueOnlyLink = CreateCatalogueContainerLocation(owner, repo);
    return (
        <Button icon compact floated='right' labelPosition='right' as={Link} to={catalogueOnlyLink} data-testid='close-spec-button' style={{marginTop: "10px"}}>
            Close <Icon name='close' />
        </Button>
    );
}

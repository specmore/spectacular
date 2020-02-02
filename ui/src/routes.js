import React from "react";
import { Button, Icon } from 'semantic-ui-react'
import { Link } from "react-router-dom";

export const CATALOGUE_CONTAINER_ROUTE = "/catalogue/:owner/:repo";

export const CatalogueContainerLinkButton = ({repository}) => {
    const catalogueLink = `catalogue/${repository.owner}/${repository.name}`;
    return (
        <Button primary floated='right' icon labelPosition='right' as={Link} to={catalogueLink} data-testid='view-catalogue-button'>
            View Catalogue <Icon name='right chevron' />
        </Button>
    );
}
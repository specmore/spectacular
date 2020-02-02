import React, { useState, useEffect } from "react";
import { Dimmer, Loader, Message, Segment, Grid, Container } from 'semantic-ui-react'
import { fetchCatalogue } from '../api-client';
import EmptyItemImage from '../assets/images/empty-catalogue-item.png';
import { useParams } from 'react-router-dom';
import CatalogueDetails from './catalogue-details';
import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";

const CatalogueContainerLoading = ({owner, repo}) => (
    <Segment vertical textAlign='center'>
        <Dimmer inverted active>
            <Loader content={`Loading catalogue for ${owner}/${repo}`} />
        </Dimmer>
        <img src={EmptyItemImage} data-testid='catalogue-container-placeholder-image' />
    </Segment>
);

const CatalogueContainerError = ({errorMessage}) => (
    <Message negative>
        <Message.Header>{errorMessage}</Message.Header>
    </Message>
);

const CatalogueContainerSegment = ({catalogue}) => (
    <Segment vertical data-testid='catalogue-container-segment'>
        <CatalogueDetails {...catalogue} />
    </Segment>
);

const CatalogueContainer = () => {
    const [catalogue, setCatalogue] = useState(null);
    const [errorMessage, setErrorMessage] = useState(null);
    const { owner, repo, location } = useParams();

    const fetchCatalogueData = async (owner, repo) => {
        try {
            const catalogueData = await fetchCatalogue(owner, repo);
            setCatalogue(catalogueData);
        } catch (error) {
            //console.error(error);
            setErrorMessage("An error occurred while fetching catalogue details.");
        }
    }

    useEffect(() => {
        fetchCatalogueData(owner, repo);
    }, [owner, repo])

    if (!catalogue && !errorMessage) return ( <CatalogueContainerLoading owner={owner} repo={repo} />);

    if (errorMessage) return (<CatalogueContainerError errorMessage={errorMessage} />);

    if  (!location) return (
        <Container text>
            <CatalogueContainerSegment catalogue={catalogue}/>
        </Container>
    );

    if (location) return (
        <Grid columns={2} stackable>
            <Grid.Row>
                <Grid.Column>
                    <div style={{ paddingLeft: '14px' }}>
                        <CatalogueContainerSegment catalogue={catalogue}/>
                    </div>
                </Grid.Column>
                <Grid.Column>
                    <SwaggerUI url="https://petstore.swagger.io/v2/swagger.json" />
                </Grid.Column>
            </Grid.Row>
        </Grid>
    );
};

export default CatalogueContainer;
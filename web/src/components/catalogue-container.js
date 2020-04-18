import React, { useState, useEffect } from "react";
import { Dimmer, Loader, Message, Segment, Grid, Container } from 'semantic-ui-react'
import { fetchCatalogue, createFileApiURL } from '../api-client';
import EmptyItemImage from '../assets/images/empty-catalogue-item.png';
import { useParams } from 'react-router-dom';
import CatalogueDetails from './catalogue-details';
import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";
import "./catalogue-container.css";
import { CloseSpecButton, BackToCatalogueListLinkButton } from '../routes';

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
    <div data-testid='catalogue-container-segment' style={{marginBottom: "10px"}} >
        <CatalogueDetails {...catalogue} />
    </div>
);

const CatalogueContainer = () => {
    const [catalogue, setCatalogue] = useState(null);
    const [errorMessage, setErrorMessage] = useState(null);
    const { 0: location, owner, repo } = useParams();
    
    const fileApiURL = createFileApiURL(owner, repo, location);

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

    if (errorMessage) return (
        <Container text>
            <BackToCatalogueListLinkButton />
            <CatalogueContainerError errorMessage={errorMessage} />
        </Container>
    );

    if  (!location) return (
        <Container text>
            <BackToCatalogueListLinkButton />
            <CatalogueContainerSegment catalogue={catalogue}/>
        </Container>
    );

    if (location) return (
        <div className='catalogue-container side-by-side-container'>
            <Container text className='side-by-side-column'>
                <BackToCatalogueListLinkButton />
                <CatalogueContainerSegment catalogue={catalogue}/>
            </Container>
            <div className='side-by-side-column' data-testid='catalogue-container-swagger-ui'>
                <CloseSpecButton />
                <SwaggerUI url={fileApiURL}/>
            </div>
        </div>
    );
};

export default CatalogueContainer;
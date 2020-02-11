import React, { useState, useEffect } from "react";
import { Dimmer, Loader, Message, Segment, Grid, Container } from 'semantic-ui-react'
import { fetchCatalogue } from '../api-client';
import EmptyItemImage from '../assets/images/empty-catalogue-item.png';
import { useParams } from 'react-router-dom';
import CatalogueDetails from './catalogue-details';
import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";
import "./catalogue-container.css";

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
    const { 0: location, owner, repo } = useParams();
    //const location = null;
    // console.log(useParams());
    // console.log(useRouteMatch());
    // console.log(location);

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
        <div className='catalogue-container side-by-side-container'>
            <Container text className='side-by-side-column'>
                <CatalogueContainerSegment catalogue={catalogue}/>
            </Container>
            <div className='side-by-side-column'>
                <SwaggerUI url="https://petstore.swagger.io/v2/swagger.json"/>
            </div>
        </div>
    );
};

export default CatalogueContainer;
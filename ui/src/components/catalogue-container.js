import React, { useState, useEffect } from "react";
import { Dimmer, Loader, Message, Header, Segment } from 'semantic-ui-react'
import { fetchCatalogue } from '../api-client';
import EmptyWelcomeItemImage from '../assets/images/empty-catalogue-item.png';
import { useParams } from 'react-router-dom';

const CatalogueContainer = () => {
    const [catalogue, setCatalogue] = useState(null);
    const [errorMessage, setErrorMessage] = useState(null);
    const { owner, repo } = useParams();

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

    if (!catalogue && !errorMessage) {
        return (
            <Segment vertical>
                <Dimmer inverted active>
                    <Loader content='Loading' />
                </Dimmer>
                <img src={EmptyWelcomeItemImage} />
            </Segment>
        );
    }

    if (errorMessage) {
        return (
            <Message negative>
                <Message.Header>{errorMessage}</Message.Header>
            </Message>
        );
    }

    return (
        <Segment vertical>
            <Header as='h1' textAlign='center'>
                catalogue {owner}/{repo}
            </Header>
        </Segment>
    );
};

export default CatalogueContainer;
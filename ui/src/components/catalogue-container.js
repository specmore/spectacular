import React, { useState, useEffect } from "react";
import { Dimmer, Loader, Message, Segment } from 'semantic-ui-react'
import { fetchCatalogue } from '../api-client';
import EmptyItemImage from '../assets/images/empty-catalogue-item.png';
import { useParams } from 'react-router-dom';
import CatalogueDetails from './catalogue-details';

const CatalogueContainerLoading = ({owner, repo}) => (
    <Segment vertical>
        <Dimmer inverted active>
            <Loader content={`Loading catalogue for ${owner}/${repo}`} />
        </Dimmer>
        <img src={EmptyItemImage} data-testid='catalogue-container-placeholder-image'/>
    </Segment>
  );
  
  const CatalogueContainerError = ({errorMessage}) => (
    <Message negative>
      <Message.Header>{errorMessage}</Message.Header>
    </Message>
  );

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

    if (!catalogue && !errorMessage) return ( <CatalogueContainerLoading owner={owner} repo={repo} />);

    if (errorMessage) return (<CatalogueContainerError errorMessage={errorMessage} />);

    return (
        <Segment vertical data-testid='catalogue-container'>
            <CatalogueDetails {...catalogue} />
        </Segment>
    );
};

export default CatalogueContainer;
import React, { useState, useEffect } from "react";
import { Dimmer, Loader, Message, Header, Segment } from 'semantic-ui-react'
import { fetchInstallation } from '../api-client';
import EmptyWelcomeItemImage from '../assets/images/empty-catalogue-item.png';
import CatalogueList from './catalogue-list';

const InstallationWelcome = () => {
    const [installation, setInstallation] = useState(null);
    const [errorMessage, setErrorMessage] = useState(null);

    const fetchInstallationData = async () => {
        try {
            const installationData = await fetchInstallation();
            setInstallation(installationData);
        } catch (error) {
            //console.error(error);
            setErrorMessage("An error occurred while fetching installation details.");
        }
    }

    useEffect(() => {
        fetchInstallationData();
    }, [])

    if (!installation && !errorMessage) {
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

    let org_url = `https://github.com/${installation.owner}`;
    
    return (
        <Segment vertical>
            <Header as='h1' textAlign='center' image={installation.owner_avatar_url}>
                Welcome to Spectacular
                <Header.Subheader>Connected to the <a href={org_url} target='_blank'>{installation.owner}</a> GitHub organization</Header.Subheader>
            </Header>
            <CatalogueList org={installation.owner}/>
        </Segment>
    );
};

export default InstallationWelcome;
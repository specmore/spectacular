import React, { useState, useEffect } from "react";
import { Dimmer, Loader, Message, Header } from 'semantic-ui-react'
import { fetchInstallation } from '../api-client';
import EmptyWelcomeItemImage from '../assets/images/empty-catalogue-item.png';

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
            <React.Fragment>
                <Dimmer inverted active>
                    <Loader content='Loading' />
                </Dimmer>
                <img src={EmptyWelcomeItemImage} />
            </React.Fragment>
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
        <Header textAlign='center' image={installation.owner_avatar_url}>
            Welcome to Spectacular
            <Header.Subheader>This installation is for the {installation.owner} GitHub organization.</Header.Subheader>
        </Header>
    );
};

export default InstallationWelcome;
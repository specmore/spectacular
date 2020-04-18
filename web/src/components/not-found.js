import React from "react";
import { Message, Header, Segment, Icon, Container } from 'semantic-ui-react'
import { useLocation } from 'react-router-dom';

const NotFound = () => {
    const location = useLocation();
    return (
        <Container text>
            <Segment vertical>
                <Header>Not Found</Header>
                <Message negative><Icon name='search' />No page for <code>{location.pathname}</code> was found.</Message>
            </Segment>
        </Container>
    );
};

export default NotFound;
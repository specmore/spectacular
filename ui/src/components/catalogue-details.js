import React from "react";
import { Icon, Image, Message, Segment, Header, Grid, Item } from 'semantic-ui-react'
import ImagePlaceHolder from '../assets/images/image-placeholder.png';
import SpecFileItem from './spec-file-item';

const CatalogueError = ({error, repository}) => (
    <Message icon negative data-testid='catalogue-details-error-message'>
        <Icon name='warning sign' />
        <Message.Content>
            <Message.Header>An error occurred while parsing the catalogue manifest file in <a href={repository.htmlUrl} target='_blank'>{repository.nameWithOwner}</a></Message.Header>
            {error}
        </Message.Content>
    </Message>
);
  
const CatalogueDetails = ({repository, catalogueManifest}) => (
    <Segment vertical data-testid='catalogue-details-segment'>
        <Header as='h1' textAlign='center'>{catalogueManifest.name}</Header>
        <Segment.Group>
            <Segment>
                <Header as='h3'>Details</Header>
                <Grid divided>
                    <Grid.Row>
                        <Grid.Column width={13}>
                            {catalogueManifest.description}
                        </Grid.Column>
                        <Grid.Column width={3}>
                            <Image src={ImagePlaceHolder} />
                        </Grid.Column>
                    </Grid.Row>
                </Grid>
            </Segment>
            <Segment>
                <Header as='h3'>Repository</Header>
                <p><a href={repository.htmlUrl} target='_blank'><Icon name="github"/> {repository.nameWithOwner}</a></p>
            </Segment>
            <Segment>
                <Header as='h3'>Specifications</Header>
                <Item.Group divided data-testid='specifications-item-group'>
                    {catalogueManifest["spec-files"].map((specFileLocation, index) => (<SpecFileItem key={index} catalogueRepository={repository} specFileLocation={specFileLocation} />))}
                </Item.Group>
            </Segment>
        </Segment.Group>
    </Segment>
);

const CatalogueDetailsContainer = ({repository, catalogueManifest, error}) => {  
    if (error) return (<CatalogueError error={error} repository={repository} />);

    return (<CatalogueDetails repository={repository} catalogueManifest={catalogueManifest} />);
};

export default CatalogueDetailsContainer;
import React from "react";
import { Icon, Image, Message, Segment, Header, Grid, Item, Label } from 'semantic-ui-react'
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
  
const CatalogueDetails = ({repository, catalogueManifest, specItems}) => (
    <Segment vertical data-testid='catalogue-details-segment'>
        <Header as='h1' textAlign='center'>{catalogueManifest.name}</Header>
        <Segment.Group>
            <Segment>
                <Header as='h3'>Details</Header>
                <Grid divided>
                    <Grid.Row>
                        <Grid.Column width={13}>
                            <p>{catalogueManifest.description}</p>
                            <Label as='a' href={repository.htmlUrl} target='_blank'>
                                <Icon name='github' />{repository.nameWithOwner}
                            </Label>
                        </Grid.Column>
                        <Grid.Column width={3}>
                            <Image src={ImagePlaceHolder} />
                        </Grid.Column>
                    </Grid.Row>
                </Grid>
            </Segment>
            <Segment>
                <Header as='h3'>Interface Specifications</Header>
                <Item.Group divided data-testid='specifications-item-group'>
                    {specItems.map((specItem, index) => (<SpecFileItem key={index} catalogueRepository={repository} specItem={specItem} />))}
                </Item.Group>
            </Segment>
        </Segment.Group>
    </Segment>
);

const CatalogueDetailsContainer = ({repository, catalogueManifest, specItems, error}) => {  
    if (error) return (<CatalogueError error={error} repository={repository} />);

    return (<CatalogueDetails repository={repository} catalogueManifest={catalogueManifest} specItems={specItems} />);
};

export default CatalogueDetailsContainer;
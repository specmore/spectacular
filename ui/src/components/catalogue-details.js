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
    <div data-testid='catalogue-details-segment'>
        <Header as='h1' textAlign='center'>{catalogueManifest.name}</Header>
        <Header as='h3' attached='top'><Icon name='info' />Details</Header>
        <Segment attached>
            <Grid divided>
                <Grid.Row>
                    <Grid.Column width={13}>
                        <p>{catalogueManifest.description}</p>
                    </Grid.Column>
                    <Grid.Column width={3}>
                        <Image src={ImagePlaceHolder} />
                    </Grid.Column>
                </Grid.Row>
            </Grid>
            <Label as='a' href={repository.htmlUrl} target='_blank'>
                <Icon name='github' />{repository.nameWithOwner}
            </Label>
        </Segment>
        <Header as='h3' attached='top'><Icon name='list' />Interface Specifications</Header>
        <Segment attached>
            {specItems.map((specItem, index) => (<SpecFileItem key={index} catalogueRepository={repository} specItem={specItem} />))}
        </Segment>
    </div>
);

const CatalogueDetailsContainer = ({repository, catalogueManifest, specItems, error}) => {  
    if (error) return (<CatalogueError error={error} repository={repository} />);

    return (<CatalogueDetails repository={repository} catalogueManifest={catalogueManifest} specItems={specItems} />);
};

export default CatalogueDetailsContainer;
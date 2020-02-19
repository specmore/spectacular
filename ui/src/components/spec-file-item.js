import React from "react";
import { Label, List, Icon, Item, Message, Segment, Header } from 'semantic-ui-react'
import { ViewSpecLinkButton } from "../routes";

const SpecFileItemError = ({specFileFullLocation, errors}) => (
    <Message icon negative data-testid='specification-file-item-error'>
        <Icon name='warning sign' />
        <Message.Content>
            <Message.Header>The following errors occurred while processing the specification file '{specFileFullLocation}':</Message.Header>
            <List bulleted>
                {errors.map((error, index) => (<List.Item key={index}>{error}</List.Item>))}
            </List>
        </Message.Content>
    </Message>
);

const SpecFileItem = ({catalogueRepository, specItem}) => {
    const specFileFullLocation = `${specItem.repository.nameWithOwner}/${specItem.filePath}`;
    if (specItem.parseResult.errors.length > 0) return (<SpecFileItemError specFileFullLocation={specFileFullLocation} errors={specItem.parseResult.errors} />);

    const selectButton = (<ViewSpecLinkButton catalogueRepository={catalogueRepository} specFileLocation={specFileFullLocation} />);

    return (
        <div data-testid='specification-file-item'>
            <Header as='h4' attached='top' block><Icon name='file code'/>{specItem.parseResult.openApiSpec.title}</Header>
            <Segment attached>
                <Header as='h5'>Latest agreed version</Header>
                <Label><Icon name='code branch' />master</Label>
                <Label circular color='grey'>{specItem.parseResult.openApiSpec.version}</Label>
                {selectButton}
                <Header as='h5'>Proposed changes</Header>
            </Segment>
        </div>
    );
};

export default SpecFileItem;
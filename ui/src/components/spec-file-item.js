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
            <Header as='h5' attached='top' block>{specItem.parseResult.openApiSpec.title}</Header>
            <Segment attached>
                <p>Latest agreed version</p>
                <Label><Icon name='github' />{specFileFullLocation}</Label>
                <Label circular color='grey'>{specItem.parseResult.openApiSpec.version}</Label>
                {selectButton}
                <p>Proposed changes</p>
            </Segment>
        </div>
    );
};

export default SpecFileItem;
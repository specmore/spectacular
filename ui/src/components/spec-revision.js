import React from "react";
import { Label, List, Icon, Message } from 'semantic-ui-react';
import { ViewSpecLinkButton } from "../routes";
import Moment from "react-moment";

const SpecItemError = ({specItem}) => (
    <Message icon negative data-testid='spec-item-error'>
        <Icon name='warning sign' />
        <Message.Content>
            <Message.Header>
                The following errors occurred while processing the specification file <a href={specItem.htmlUrl} target='_blank'>'{`${specItem.repository.nameWithOwner}/${specItem.filePath}`}' in branch '{specItem.ref}'</a>:
            </Message.Header>
            <List bulleted>
                {specItem.parseResult.errors.map((error, index) => (<List.Item key={index}>{error}</List.Item>))}
            </List>
        </Message.Content>
    </Message>
);

const SpecRevision = ({specItem, branchColor}) => {
    if (!branchColor) {
        branchColor='olive'
    }

    const specFileFullLocation = `${specItem.repository.nameWithOwner}/${specItem.ref}/${specItem.filePath}`;
    const selectButton = (<ViewSpecLinkButton specFileLocation={specFileFullLocation} />);

    if (specItem.parseResult.errors.length > 0) 
        return (<SpecItemError specItem={specItem} />);

    return (
        <div>
            <Label color={branchColor} as='a' href={specItem.htmlUrl} target='_blank'>
                <Icon name='code branch' />{specItem.ref}
            </Label>
            <Label circular>{specItem.parseResult.openApiSpec.version}</Label>
            <Moment fromNow>{specItem.lastModified}</Moment>
            {selectButton}
        </div>
    );
};

export default SpecRevision;
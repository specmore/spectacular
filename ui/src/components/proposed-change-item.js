import React from "react";
import { Label, List, Icon, Item, Message, Segment, Header } from 'semantic-ui-react'

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

const ChangedSpecItem = ({specItem}) => {
    if (specItem.parseResult.errors.length > 0) 
        return (<SpecItemError specItem={specItem} />);

    return (
        <div>
            <Label color='yellow' as='a' href={specItem.htmlUrl} target='_blank'>
                <Icon name='code branch' />{specItem.ref}
            </Label>
            <Label circular>{specItem.parseResult.openApiSpec.version}</Label>
        </div>
    );
};

const ProposedChange = ({pullRequest, specItem}) => (
    <div data-testid='proposed-change-item'>
        <a href={pullRequest.url} target='_blank'>
            <Label circular color='grey'>#{pullRequest.number}</Label> 
            <span>{pullRequest.title}</span>
        </a>
        {specItem && (<ChangedSpecItem  specItem={specItem} />)}
    </div>
);

export default ProposedChange;
import React from "react";
import { Label, List, Icon, Message, Segment } from 'semantic-ui-react'

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
    <Segment attached>
        <a href={pullRequest.url} target='_blank'>
            <Label circular color='grey'>#{pullRequest.number}</Label> 
            <span style={{marginLeft:'0.5em', fontWeight:'bold'}}>{pullRequest.title}</span>
        </a>
        <div style={{marginTop:'0.5em'}}>
            {specItem && (<ChangedSpecItem  specItem={specItem} />)}
        </div>
    </Segment>
);

export default ProposedChange;
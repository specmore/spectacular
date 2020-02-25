import React from "react";
import { Label, List, Icon, Item, Message, Segment, Header } from 'semantic-ui-react'
import { ViewSpecLinkButton } from "../routes";
import ProposedChangeItem from './proposed-change-item';

const SpecLogError = ({specFileFullLocation, errors}) => (
    <Message icon negative data-testid='spec-log-error'>
        <Icon name='warning sign' />
        <Message.Content>
            <Message.Header>The following errors occurred while processing the specification file '{specFileFullLocation}':</Message.Header>
            <List bulleted>
                {errors.map((error, index) => (<List.Item key={index}>{error}</List.Item>))}
            </List>
        </Message.Content>
    </Message>
);

const SpecLog = ({catalogueRepository, specLog}) => {
    const latestAgreedSpecItem = specLog.latestAgreed;
    const specFileFullLocation = `${latestAgreedSpecItem.repository.nameWithOwner}/${latestAgreedSpecItem.filePath}`;
    if (latestAgreedSpecItem.parseResult.errors.length > 0) return (<SpecLogError specFileFullLocation={specFileFullLocation} errors={latestAgreedSpecItem.parseResult.errors} />);

    const selectButton = (<ViewSpecLinkButton catalogueRepository={catalogueRepository} specFileLocation={specFileFullLocation} />);

    return (
        <div data-testid='spec-log'>
            <Header as='h4' attached='top' block><Icon name='file code'/>{latestAgreedSpecItem.parseResult.openApiSpec.title}</Header>
            <Segment attached>
                <Header as='h5'>Latest agreed version</Header>
                <Label color='olive' as='a' href={latestAgreedSpecItem.htmlUrl} target='_blank'>
                    <Icon name='code branch' />{latestAgreedSpecItem.ref}
                </Label>
                <Label circular>{latestAgreedSpecItem.parseResult.openApiSpec.version}</Label>
                {selectButton}
                <Header as='h5'>Proposed changes</Header>
                {specLog.proposedChanges.map((proposedChange, index) => (<ProposedChangeItem key={index} {...proposedChange} />))}
            </Segment>
        </div>
    );
};

export default SpecLog;
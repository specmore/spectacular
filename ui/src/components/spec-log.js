import React from "react";
import { Label, List, Icon, Message, Segment, Header } from 'semantic-ui-react'
import { ViewSpecLinkButton } from "../routes";
import LatestAgreedVersion from "./latest-agreed-version";
import ProposedChangesList from './proposed-changes-list';

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

const SpecLog = ({specLog}) => {
    const latestAgreedSpecItem = specLog.latestAgreed;
    const specFileFullLocation = `${latestAgreedSpecItem.repository.nameWithOwner}/${latestAgreedSpecItem.filePath}`;
    if (latestAgreedSpecItem.parseResult.errors.length > 0) return (<SpecLogError specFileFullLocation={specFileFullLocation} errors={latestAgreedSpecItem.parseResult.errors} />);

    return (
        <div data-testid='spec-log'>
            <Header as='h4' attached='top' block><Icon name='file code'/>{latestAgreedSpecItem.parseResult.openApiSpec.title}</Header>
            <Segment attached>
                <LatestAgreedVersion latestAgreedSpecItem={specLog.latestAgreed} />
                {specLog.proposedChanges && specLog.proposedChanges.length > 0 && (<ProposedChangesList proposedChanges={specLog.proposedChanges} />)}
            </Segment>
        </div>
    );
};

export default SpecLog;
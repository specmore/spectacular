import React from "react";
import { List, Icon, Message, Segment, Header } from 'semantic-ui-react';
import SpecLogItem from './spec-log-item';
import ProposedChangeItem from './proposed-change-item';
import LatestAgreedVersion from './latest-agreed-version';


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
                <Header as='h5' attached='top'>Latest agreed version</Header>
                <SpecLogItem specItem={specLog.latestAgreed}>
                    <LatestAgreedVersion latestAgreedSpecItem={specLog.latestAgreed} />
                </SpecLogItem>
                <Header as='h5' attached='top'>Open change proposals</Header>
                {specLog.proposedChanges.map((proposedChange, index) => (
                    <SpecLogItem key={index} specItem={proposedChange.specItem}>
                        <ProposedChangeItem {...proposedChange} />
                    </SpecLogItem>
                ))}
            </Segment>
        </div>
    );
};

export default SpecLog;
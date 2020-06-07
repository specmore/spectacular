import React, { FunctionComponent } from 'react';
import {
  Label, List, Icon, Message, Segment, Header,
} from 'semantic-ui-react';
import SpecLogItem from './spec-log-item';
import ProposedChangeItem from './proposed-change-item';
import LatestAgreedVersion from './latest-agreed-version';
import { SpecLog, SpecItem } from '../backend-api-client';


interface SpecItemProps {
  specItem: SpecItem
}

const SpecLogError: FunctionComponent<SpecItemProps> = ({ specItem }) => (
  <Message icon negative data-testid="spec-log-error">
    <Icon name="warning sign" />
    <Message.Content>
      <Message.Header>
        {`The following errors occurred while processing the specification file '${specItem.fullPath}':`}
      </Message.Header>
      <List bulleted>
        {specItem.parseResult.errors.map((error) => (<List.Item key={error}>{error}</List.Item>))}
      </List>
    </Message.Content>
  </Message>
);

interface SpecLogProps {
  specLog: SpecLog
}

const SpecLogContainer: FunctionComponent<SpecLogProps> = ({ specLog }) => {
  const latestAgreedSpecItem = specLog.latestAgreed;
  if (specLog.latestAgreed.parseResult.errors.length > 0) {
    return (
      <SpecLogError specItem={specLog.latestAgreed} />
    );
  }

  const proposedChangesCount = specLog.proposedChanges.length;

  return (
    <div data-testid="spec-log-container">
      <Header as="h4" attached="top" block>
        <Icon name="file code" />
        {latestAgreedSpecItem.parseResult.openApiSpec.title}
      </Header>
      <Segment attached>
        <Header as="h5" attached="top">Latest agreed version</Header>
        <SpecLogItem specItem={specLog.latestAgreed} type="latest-agreed-version">
          <LatestAgreedVersion latestAgreedSpecItem={specLog.latestAgreed} />
        </SpecLogItem>
        <Header as="h5" attached="top">
          Proposed Changes
          <Label size="small" color="grey">{proposedChangesCount}</Label>
        </Header>
        {specLog.proposedChanges.map((proposedChange) => (
          <SpecLogItem key={proposedChange.id} specItem={proposedChange.specItem} type="proposed-change-item">
            <ProposedChangeItem pullRequest={proposedChange.pullRequest} specItem={proposedChange.specItem} />
          </SpecLogItem>
        ))}
      </Segment>
    </div>
  );
};

export default SpecLogContainer;

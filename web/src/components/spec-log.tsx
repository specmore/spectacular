import React, { FunctionComponent } from 'react';
import {
  Label, List, Icon, Message, Item,
} from 'semantic-ui-react';
import { Link } from 'react-router-dom';
import SpecLogItem from './spec-log-item';
import ProposedChangeItem from './proposed-change-item';
import LatestAgreedVersion from './latest-agreed-version';
import { SpecLog, SpecItem, Catalogue } from '../backend-api-client';
import { CreateInterfaceLocation } from '../routes';


interface SpecItemProps {
  specItem: SpecItem;
}

const SpecLogError: FunctionComponent<SpecItemProps> = ({ specItem }) => (
  <Item data-testid="catalogue-list-item-error-item">
    <Item.Content>
      <Item.Description>
        <Message icon negative data-testid="spec-log-error">
          <Icon name="warning sign" />
          <Message.Content>
            <Message.Header>
              Spec Error
            </Message.Header>
            The following errors occurred while parsing the specification file
            {' '}
            <a href={specItem.htmlUrl} target="_blank" rel="noopener noreferrer">{specItem.fullPath}</a>
            <List bulleted>
              {specItem.parseResult.errors.map((error) => (<List.Item key={error}>{error}</List.Item>))}
            </List>
          </Message.Content>
        </Message>
      </Item.Description>
    </Item.Content>
  </Item>
);

interface SpecLogProps {
  catalogue: Catalogue;
  specLog: SpecLog;
}

const SpecLogContainer: FunctionComponent<SpecLogProps> = ({ catalogue, specLog }) => {
  const interfaceLocation = CreateInterfaceLocation(catalogue.encodedId, specLog.interfaceName);
  const latestAgreedSpecItem = specLog.latestAgreed;
  if (specLog.latestAgreed.parseResult.errors.length > 0) {
    return (
      <SpecLogError specItem={specLog.latestAgreed} />
    );
  }

  const proposedChangesCount = specLog.proposedChanges.length;

  return (
    <Item data-testid="spec-log-container">
      <Item.Content>
        <Item.Header as={Link} to={interfaceLocation}>
          {latestAgreedSpecItem.parseResult.openApiSpec.title}
        </Item.Header>
        <Item.Extra>
          <Label color="blue">
            {latestAgreedSpecItem.parseResult.openApiSpec.version}
          </Label>
          <Label color="green">
            <Icon name="code branch" />
            {proposedChangesCount}
          </Label>
          <a href={latestAgreedSpecItem.htmlUrl} target="_blank" rel="noopener noreferrer">
            <Icon name="file code" inverted circular size="small" color="grey" />
          </a>
        </Item.Extra>
      </Item.Content>
    </Item>
    // <div data-testid="spec-log-container">
    //   <Header as="h4" attached="top" block>
    //     <Icon name="file code" />
    //     {latestAgreedSpecItem.parseResult.openApiSpec.title}
    //   </Header>
    //   <Segment attached>
    //     <Header as="h5" attached="top">Latest agreed version</Header>
    //     <SpecLogItem interfaceName={specLog.interfaceName} specItem={specLog.latestAgreed} type="latest-agreed-version">
    //       <LatestAgreedVersion latestAgreedSpecItem={specLog.latestAgreed} />
    //     </SpecLogItem>
    //     <Header as="h5" attached="top">
    //       Proposed Changes
    //       <Label size="small" color="grey">{proposedChangesCount}</Label>
    //     </Header>
    //     {specLog.proposedChanges.map((proposedChange) => (
    //       <SpecLogItem
    //         key={proposedChange.id}
    //         interfaceName={specLog.interfaceName}
    //         specItem={proposedChange.specItem}
    //         type="proposed-change-item"
    //       >
    //         <ProposedChangeItem pullRequest={proposedChange.pullRequest} specItem={proposedChange.specItem} />
    //       </SpecLogItem>
    //     ))}
    //   </Segment>
    // </div>
  );
};

export default SpecLogContainer;

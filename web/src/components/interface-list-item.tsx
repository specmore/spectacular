import React, { FunctionComponent } from 'react';
import {
  Label, List, Icon, Message, Item,
} from 'semantic-ui-react';
import { Link } from 'react-router-dom';
import { SpecLog, SpecItem } from '../backend-api-client';
import { CreateInterfaceLocation, OpenSpecItemContentPageButton } from '../routes';


interface SpecItemProps {
  specItem: SpecItem;
}

const InterfaceListItemError: FunctionComponent<SpecItemProps> = ({ specItem }) => (
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

interface InterfaceListItemProps {
  catalogueEncodedId: string;
  specLog: SpecLog;
}

const InterfaceListItemContainer: FunctionComponent<InterfaceListItemProps> = ({ catalogueEncodedId, specLog }) => {
  const interfaceLocation = CreateInterfaceLocation(catalogueEncodedId, specLog.interfaceName);
  const latestAgreedSpecItem = specLog.latestAgreed;
  if (specLog.latestAgreed.parseResult.errors.length > 0) {
    return (
      <InterfaceListItemError specItem={specLog.latestAgreed} />
    );
  }

  const proposedChangesCount = specLog.proposedChanges.length;

  return (
    <Item data-testid="interface-list-item-container">
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
          <OpenSpecItemContentPageButton specItem={latestAgreedSpecItem} />
        </Item.Extra>
      </Item.Content>
    </Item>
  );
};

export default InterfaceListItemContainer;

import React, { FunctionComponent } from 'react';
import {
  Label, List, Icon, Message, Item, Popup,
} from 'semantic-ui-react';
import { Link } from 'react-router-dom';
import { SpecItem, SpecEvolutionSummary } from '../backend-api-client';
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
  installationId: number;
  catalogueEncodedId: string;
  specEvolutionSummary: SpecEvolutionSummary;
}

const InterfaceListItemContainer: FunctionComponent<InterfaceListItemProps> = ({
  installationId,
  catalogueEncodedId,
  specEvolutionSummary,
}) => {
  const interfaceLocation = CreateInterfaceLocation(installationId, catalogueEncodedId, specEvolutionSummary.interfaceName);
  const latestAgreedSpecItem = specEvolutionSummary.latestAgreed;
  if (latestAgreedSpecItem.parseResult.errors.length > 0) {
    return (
      <InterfaceListItemError specItem={latestAgreedSpecItem} />
    );
  }

  return (
    <Item data-testid="interface-list-item-container">
      <Item.Content>
        <Item.Header as={Link} to={interfaceLocation}>
          {latestAgreedSpecItem.parseResult.openApiSpec.title}
        </Item.Header>
        <Item.Extra>
          <Popup
            content="Latest Agreed Version"
            position="bottom center"
            trigger={(
              <Label color="blue">
                {latestAgreedSpecItem.parseResult.openApiSpec.version}
              </Label>
            )}
          />
          <Popup
            content="Number of Proposed Changes"
            position="bottom center"
            trigger={(
              <Label color="green" data-testid="proposed-changes-label">
                <Icon name="edit" />
                {specEvolutionSummary.proposedChangesCount}
              </Label>
            )}
          />
          <Popup
            content="Number of Upcoming Releases"
            position="bottom center"
            trigger={(
              <Label color="orange" data-testid="upcoming-releases-label">
                <Icon name="code branch" />
                {specEvolutionSummary.upcomingReleaseCount}
              </Label>
            )}
          />
          <OpenSpecItemContentPageButton specItem={latestAgreedSpecItem} />
        </Item.Extra>
      </Item.Content>
    </Item>
  );
};

export default InterfaceListItemContainer;

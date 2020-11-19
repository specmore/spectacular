import React, { FunctionComponent } from 'react';
import {
  Header, Item,
} from 'semantic-ui-react';
import { SpecItem, SpecLog } from '../backend-api-client';
import { CloseSpecEvolutionButton } from '../routes';

const MASTER_BRANCH_COLOR = '#5E81AC';
const PR_BRANCH_COLOR = '#8FBCBB';
const MESSAGE_COLOR = '#2E3440';

interface SpecLogItemProps {
  specItem: SpecItem;
}

const SpecLogItem: FunctionComponent<SpecLogItemProps> = ({ specItem }) => (
  <Item>
    <Item.Content>
      <div>{specItem.ref}</div>
    </Item.Content>
  </Item>
);

interface SpecLogProps {
  specLog: SpecLog;
}

const SpecEvolutionContainer: FunctionComponent<SpecLogProps> = ({ specLog }) => (
  <div data-testid="spec-evolution-container">
    <CloseSpecEvolutionButton />
    <Header as="h3">Spec Evolution</Header>
    <Item.Group divided>
      {
        specLog.proposedChanges.map((proposedChange) => (
          <SpecLogItem key={proposedChange.specItem.id} specItem={proposedChange.specItem} />
        ))
      }
      <SpecLogItem specItem={specLog.latestAgreed} />
    </Item.Group>
  </div>
);

export default SpecEvolutionContainer;

import React, { FunctionComponent } from 'react';
import {
  Header, Item, Label,
} from 'semantic-ui-react';
import { ChangeProposal, SpecItem, SpecLog } from '../backend-api-client';
import { CloseSpecEvolutionButton } from '../routes';

const MASTER_BRANCH_COLOR = '#5E81AC';
const PR_BRANCH_COLOR = '#8FBCBB';
const MESSAGE_COLOR = '#2E3440';

interface ChangeProposalProps {
  proposedChange: ChangeProposal;
}

const ChangeProposalItem: FunctionComponent<ChangeProposalProps> = ({ proposedChange }) => (
  <Item>
    <Item.Content>
      <Label color="green">
        PR #
        {proposedChange.pullRequest.number}
      </Label>
      <span>{proposedChange.pullRequest.title}</span>
    </Item.Content>
  </Item>
);

interface SpecLogItemProps {
  specItem: SpecItem;
}

const LatestAgreedLogItem: FunctionComponent<SpecLogItemProps> = ({ specItem }) => (
  <Item>
    <Item.Content>
      <Label color="blue">{specItem.ref}</Label>
      <Label color="blue" tag>{specItem.parseResult.openApiSpec.version}</Label>
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
          <ChangeProposalItem key={proposedChange.id} proposedChange={proposedChange} />
        ))
      }
      <LatestAgreedLogItem specItem={specLog.latestAgreed} />
    </Item.Group>
  </div>
);

export default SpecEvolutionContainer;

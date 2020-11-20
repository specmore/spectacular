import React, { FunctionComponent } from 'react';
import './spec-evolution.less';
import {
  Header, Item, Label,
} from 'semantic-ui-react';
import { ChangeProposal, SpecItem, SpecLog } from '../backend-api-client';
import { CloseSpecEvolutionButton } from '../routes';

interface SpecLogItemProps {
  specItem: SpecItem;
}

interface ChangeProposalProps {
  proposedChange: ChangeProposal;
}

const ChangeProposalItem: FunctionComponent<ChangeProposalProps> = ({ proposedChange }) => (
  <div className="log-item-container">
    <Label color="green">
      PR #
      {proposedChange.pullRequest.number}
    </Label>
    <div>{proposedChange.pullRequest.title}</div>
  </div>
);

const LatestAgreedLogItem: FunctionComponent<SpecLogItemProps> = ({ specItem }) => (
  <div>
    <Label color="blue">{specItem.ref}</Label>
    <Label color="blue" tag>{specItem.parseResult.openApiSpec.version}</Label>
  </div>
);

interface SpecLogProps {
  specLog: SpecLog;
}

const SpecEvolutionContainer: FunctionComponent<SpecLogProps> = ({ specLog }) => (
  <div data-testid="spec-evolution-container">
    <CloseSpecEvolutionButton />
    <Header as="h3">Spec Evolution</Header>
    <div className="spec-evolution-container">
      {
        specLog.proposedChanges.map((proposedChange) => (
          <div className="item">
            <ChangeProposalItem key={proposedChange.id} proposedChange={proposedChange} />
          </div>
        ))
      }
      <div className="item">
        <LatestAgreedLogItem specItem={specLog.latestAgreed} />
      </div>
    </div>
  </div>
);

export default SpecEvolutionContainer;

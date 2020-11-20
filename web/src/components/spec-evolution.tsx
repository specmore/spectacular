import React, { FunctionComponent } from 'react';
import './spec-evolution.less';
import {
  Header, Label,
} from 'semantic-ui-react';
import { ChangeProposal, SpecItem, SpecLog } from '../backend-api-client';
import { CloseSpecEvolutionButton, ViewSpecLinkButton } from '../routes';

interface SpecLogItemProps {
  specItem: SpecItem;
  interfaceName: string;
}

interface ChangeProposalProps {
  proposedChange: ChangeProposal;
  interfaceName: string;
}

const ChangeProposalItem: FunctionComponent<ChangeProposalProps> = ({ proposedChange, interfaceName }) => (
  <div className="log-entry-container">
    <div className="line-container" />
    <div className="details-container">
      <Label color="green">
        PR #
        {proposedChange.pullRequest.number}
      </Label>
      <div className="centre">{proposedChange.pullRequest.title}</div>
      <ViewSpecLinkButton refName={proposedChange.specItem.ref} interfaceName={interfaceName} />
    </div>
  </div>
);

const LatestAgreedLogItem: FunctionComponent<SpecLogItemProps> = ({ specItem, interfaceName }) => (
  <div className="log-entry-container">
    <div className="line-container" />
    <div className="details-container">
      <Label color="blue">{specItem.ref}</Label>
      <Label color="blue" tag>{specItem.parseResult.openApiSpec.version}</Label>
      <div className="centre" />
      <ViewSpecLinkButton refName={specItem.ref} interfaceName={interfaceName} />
    </div>
  </div>
);

interface SpecLogProps {
  specLog: SpecLog;
  interfaceName: string;
}

const SpecEvolutionContainer: FunctionComponent<SpecLogProps> = ({ specLog, interfaceName }) => (
  <div data-testid="spec-evolution-container">
    <CloseSpecEvolutionButton />
    <Header as="h3">Spec Evolution</Header>
    <div className="spec-evolution-container">
      {
        specLog.proposedChanges.map((proposedChange) => (
          <div className="item">
            <ChangeProposalItem key={proposedChange.id} proposedChange={proposedChange} interfaceName={interfaceName} />
          </div>
        ))
      }
      <div className="item">
        <LatestAgreedLogItem specItem={specLog.latestAgreed} interfaceName={interfaceName} />
      </div>
    </div>
  </div>
);

export default SpecEvolutionContainer;

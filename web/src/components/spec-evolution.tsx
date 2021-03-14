import React, { FunctionComponent } from 'react';
import './spec-evolution.less';
import {
  Button, Header, Icon, Label, Placeholder,
} from 'semantic-ui-react';
import {
  ChangeProposal, SpecItem, SpecLog, useGetInterfaceSpecEvolution,
} from '../backend-api-client';
import { CloseSpecEvolutionButton, OpenSpecItemContentPageButton, ViewSpecLinkButton } from '../routes';

interface SpecLogItemProps {
  specItem: SpecItem;
  interfaceName: string;
}

interface ChangeProposalProps {
  proposedChange: ChangeProposal;
  interfaceName: string;
}

const LatestAgreedPlaceholderItem: FunctionComponent<SpecLogItemProps> = ({ specItem, interfaceName }) => (
  <>
    <div className="item" data-testid="spec-evolution-placeholder">
      <div className="log-entry-container" data-testid="log-entry-container">
        <div className="line-container" />
        <div className="placeholder-container">
          <Placeholder>
            <Placeholder.Line />
            <Placeholder.Line />
          </Placeholder>
        </div>
      </div>
    </div>
    <div key={specItem.ref} className="item">
      <LatestAgreedLogItem specItem={specItem} interfaceName={interfaceName} />
    </div>
  </>
);

const ChangeProposalItem: FunctionComponent<ChangeProposalProps> = ({ proposedChange, interfaceName }) => (
  <div className="log-entry-container" data-testid="log-entry-container">
    <div className="line-container">
      <div className="latest-agreed line" />
      <div className="change-proposal line" />
    </div>
    <div className="details-container">
      <Button
        icon
        labelPosition="right"
        size="mini"
        color="green"
        href={proposedChange.pullRequest.url}
        target="_blank"
        rel="noopener noreferrer"
      >
        PR #
        {proposedChange.pullRequest.number}
        <Icon name="code branch" />
      </Button>
      <div className="centre">{proposedChange.pullRequest.title}</div>
      <OpenSpecItemContentPageButton specItem={proposedChange.specItem} />
      <ViewSpecLinkButton refName={proposedChange.specItem.ref} interfaceName={interfaceName} />
    </div>
  </div>
);

const LatestAgreedLogItem: FunctionComponent<SpecLogItemProps> = ({ specItem, interfaceName }) => (
  <div className="log-entry-container" data-testid="log-entry-container">
    <div className="line-container">
      <div className="latest-agreed line" />
    </div>
    <div className="details-container">
      <Label color="blue">{specItem.ref}</Label>
      <Label color="blue" tag>{specItem.parseResult.openApiSpec.version}</Label>
      <div className="centre" />
      <OpenSpecItemContentPageButton specItem={specItem} />
      <ViewSpecLinkButton refName={specItem.ref} interfaceName={interfaceName} />
    </div>
  </div>
);

interface SpecLogProps {
  specLog: SpecLog;
  interfaceName: string;
  encodedId: string;
}

const SpecEvolutionContainer: FunctionComponent<SpecLogProps> = ({ specLog, interfaceName, encodedId }) => {
  const getInterfaceSpecEvolution = useGetInterfaceSpecEvolution({ encodedId, interfaceName });

  const { data: interfaceSpecEvolutionResult, loading, error } = getInterfaceSpecEvolution;

  let specEvolutionItems = null;
  if (loading) {
    specEvolutionItems = (<LatestAgreedPlaceholderItem specItem={specLog.latestAgreed} interfaceName={interfaceName} />);
  }

  return (
    <div data-testid="spec-evolution-container">
      <CloseSpecEvolutionButton />
      <Header as="h3">Spec Evolution</Header>
      <div className="spec-evolution-log-container">
        {specEvolutionItems}
        {/* {
          specLog.proposedChanges.map((proposedChange) => (
            <div key={proposedChange.id} className="item">
              <ChangeProposalItem proposedChange={proposedChange} interfaceName={interfaceName} />
            </div>
          ))
        } */}
        {/* <div key={specLog.latestAgreed.ref} className="item">
          <LatestAgreedLogItem specItem={specLog.latestAgreed} interfaceName={interfaceName} />
        </div> */}
      </div>
    </div>
  );
};

export default SpecEvolutionContainer;

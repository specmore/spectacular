import React, { FunctionComponent } from 'react';
import './spec-evolution.less';
import {
  Button, Header, Icon, Label, Placeholder,
} from 'semantic-ui-react';
import {
  ChangeProposal, EvolutionBranch, EvolutionItem, SpecItem, SpecLog, useGetInterfaceSpecEvolution,
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

const PlaceholderLogItem: FunctionComponent<SpecLogItemProps> = ({ specItem, interfaceName }) => (
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

interface EvolutionItemProps {
  evolutionItem: EvolutionItem;
  isMain: boolean;
}

const EvolutionItemLines: FunctionComponent<EvolutionItemProps> = ({ evolutionItem, isMain }) => {
  const { pullRequest } = evolutionItem;

  const latestAgreedLine = isMain ? (<div className="latest-agreed line" />) : (<div className="line" />);
  const upcomingReleaseLine = !isMain ? (<div className="upcoming-release line" />) : null;
  const pullRequestLine = pullRequest ? (<div className="change-proposal line" />) : null;

  return (
    <div className="line-container">
      {latestAgreedLine}
      {upcomingReleaseLine}
      {pullRequestLine}
    </div>
  );
};

const EvolutionItemDetails: FunctionComponent<EvolutionItemProps> = ({ evolutionItem, isMain }) => {
  const { pullRequest, tag } = evolutionItem;

  const tagLabel = tag
    ? (<Label color="blue" tag>{tag}</Label>)
    : null;

  let prLabel = null;
  let prTitle = null;
  if (evolutionItem.pullRequest) {
    prLabel = (
      <Button
        icon
        labelPosition="right"
        size="mini"
        color="green"
        href={pullRequest.url}
        target="_blank"
        rel="noopener noreferrer"
      >
        PR #
        {pullRequest.number}
        <Icon name="code branch" />
      </Button>
    );
    prTitle = (<div className="centre">{pullRequest.title}</div>);
  }

  return (
    <div className="details-container">
      {tagLabel}
      {prLabel}
      {prTitle}
    </div>
  );
};

const buildLogItemsForEvolutionBranch = (evolutionBranch: EvolutionBranch, isMain: boolean) => {
  const { evolutionItems } = evolutionBranch;
  return evolutionItems.map((evolutionItem) => (
    <div className="item">
      <div className="log-entry-container" data-testid="log-entry-container">
        <EvolutionItemLines evolutionItem={evolutionItem} isMain={isMain} />
        <EvolutionItemDetails evolutionItem={evolutionItem} isMain={isMain} />
        {/* <div className="details-container">
          <Label color="blue">{evolutionItem}</Label>
          <Label color="blue" tag>{specItem.parseResult.openApiSpec.version}</Label>
          <div className="centre" />
          <OpenSpecItemContentPageButton specItem={specItem} />
          <ViewSpecLinkButton refName={specItem.ref} interfaceName={interfaceName} />
        </div> */}
      </div>
    </div>
  ));
};

interface SpecLogProps {
  specLog: SpecLog;
  interfaceName: string;
  encodedId: string;
}

const SpecEvolutionContainer: FunctionComponent<SpecLogProps> = ({ specLog, interfaceName, encodedId }) => {
  const getInterfaceSpecEvolution = useGetInterfaceSpecEvolution({ encodedId, interfaceName });

  const { data: interfaceSpecEvolutionResult, loading, error } = getInterfaceSpecEvolution;

  let logItems = null;
  if (loading) {
    logItems = (<PlaceholderLogItem specItem={specLog.latestAgreed} interfaceName={interfaceName} />);
  } else {
    const { main, releases } = interfaceSpecEvolutionResult.specEvolution;
    const mainBranchLogItems = buildLogItemsForEvolutionBranch(main, true);
    const releaseBranchLogItems = [].concat(...releases.map((releaseBranch) => buildLogItemsForEvolutionBranch(releaseBranch, false)));
    logItems = [...releaseBranchLogItems, ...mainBranchLogItems];
  }

  return (
    <div data-testid="spec-evolution-container">
      <CloseSpecEvolutionButton />
      <Header as="h3">Spec Evolution</Header>
      <div className="spec-evolution-log-container">
        {logItems}
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

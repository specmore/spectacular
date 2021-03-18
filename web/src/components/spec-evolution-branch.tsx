import React, { FunctionComponent } from 'react';
import { Button, Icon, Label } from 'semantic-ui-react';
import { EvolutionBranch, EvolutionItem } from '../backend-api-client';
import { ViewSpecLinkButton } from '../routes';

interface EvolutionItemProps {
  evolutionItem: EvolutionItem;
  isMain?: boolean;
}

const EvolutionItemLines: FunctionComponent<EvolutionItemProps> = ({ evolutionItem, isMain }) => {
  const { pullRequest, branchName } = evolutionItem;


  let mainLine = null;
  let upcomingReleaseLine = null;
  if (isMain) {
    mainLine = branchName || pullRequest ? (<div className="latest-agreed line" />) : (<div className="old-version line" />);
  } else {
    upcomingReleaseLine = (<div className="upcoming-release line" />);
  }
  const pullRequestLine = pullRequest ? (<div className="change-proposal line" />) : null;

  return (
    <div className="line-container">
      {mainLine}
      {upcomingReleaseLine}
      {pullRequestLine}
    </div>
  );
};

const EvolutionItemDetails: FunctionComponent<EvolutionItemProps> = ({ evolutionItem, isMain }) => {
  const {
    pullRequest, tag, branchName, ref,
  } = evolutionItem;

  let centreDiv = (<div className="centre" />);

  let tagLabel = null;
  if (tag) {
    let colourClassName;
    if (isMain) {
      colourClassName = branchName ? 'latest-agreed' : 'old-version';
    } else {
      colourClassName = 'upcoming-release';
    }
    tagLabel = (<Label className={colourClassName} tag>{tag}</Label>);
  }

  let branchNameLabel = null;
  if (branchName) {
    const colourClassName = isMain ? 'latest-agreed' : 'upcoming-release';
    branchNameLabel = (<Label className={colourClassName}>{branchName}</Label>);
  }

  let prLabel = null;
  if (pullRequest) {
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
    centreDiv = (<div className="centre">{pullRequest.title}</div>);
  }

  const viewSpecLinkButton = (<ViewSpecLinkButton refName={ref} />);

  return (
    <div className="details-container">
      {branchNameLabel}
      {tagLabel}
      {prLabel}
      {centreDiv}
      {viewSpecLinkButton}
    </div>
  );
};

interface EvolutionBranchProps {
  evolutionBranch: EvolutionBranch;
  isMain?: boolean;
}

const SpecEvolutionBranchContainer: FunctionComponent<EvolutionBranchProps> = ({ evolutionBranch, isMain }) => {
  const { evolutionItems } = evolutionBranch;
  const logItems = evolutionItems.map((evolutionItem) => (
    <div key={evolutionItem.ref} className="item">
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

  return (
    <div className="evolution-branch-container">
      {logItems}
    </div>
  );
};

export default SpecEvolutionBranchContainer;

import React, { FunctionComponent } from 'react';
import { Button, Icon, Label } from 'semantic-ui-react';
import { EvolutionBranch, EvolutionItem } from '../backend-api-client';
import { ViewSpecLinkButton } from '../routes';

interface EvolutionItemProps {
  evolutionItem: EvolutionItem;
  isMain?: boolean;
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

const EvolutionItemDetails: FunctionComponent<EvolutionItemProps> = ({ evolutionItem }) => {
  const { pullRequest, tag } = evolutionItem;

  let viewSpecRef = null;
  let centreDiv = (<div className="centre" />);

  let tagLabel = null;
  if (tag) {
    viewSpecRef = tag;
    tagLabel = (<Label color="blue" tag>{tag}</Label>);
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

  const viewSpecLinkButton = viewSpecRef ? (<ViewSpecLinkButton refName={viewSpecRef} />) : null;

  return (
    <div className="details-container">
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

  return (
    <div className="evolution-branch-container">
      {logItems}
    </div>
  );
};

export default SpecEvolutionBranchContainer;

import React, { FunctionComponent } from 'react';
import { EvolutionItem } from '../../backend-api-client';

interface EvolutionItemProps {
  evolutionItem: EvolutionItem;
  isMain?: boolean;
}

const EvolutionLinesItem: FunctionComponent<EvolutionItemProps> = ({ evolutionItem, isMain }) => {
  const { pullRequest, branchName } = evolutionItem;


  let mainLineStyle = null;
  let upcomingReleaseLine = null;

  if (isMain) {
    mainLineStyle = branchName || pullRequest ? 'latest-agreed line' : 'old-version line';
  } else {
    mainLineStyle = 'blank line';
    upcomingReleaseLine = (<div data-testid="release-branch-line" className="upcoming-release line" />);
  }

  const mainLine = (<div data-testid="main-branch-line" className={mainLineStyle} />);
  const pullRequestLine = pullRequest ? (<div data-testid="pull-request-line" className="change-proposal line" />) : null;

  return (
    <div data-testid="evolution-lines-container" className="line-container">
      {mainLine}
      {upcomingReleaseLine}
      {pullRequestLine}
    </div>
  );
};

export default EvolutionLinesItem;

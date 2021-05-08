import React, { FunctionComponent } from 'react';
import { EvolutionBranch, EvolutionItem } from '../../backend-api-client';
import EvolutionLinesItem from './evolution-lines-item';
import EvolutionItemDetails from './evolution-item-details';

interface EvolutionBranchProps {
  evolutionBranch: EvolutionBranch;
  isMain?: boolean;
}

interface LogEntryContainerProps {
  evolutionItem: EvolutionItem;
  isMain?: boolean;
}

const LogEntryContainer: FunctionComponent<LogEntryContainerProps> = ({ evolutionItem, isMain }) => (
  <div className="item">
    <div className="log-entry-container" data-testid="log-entry-container">
      <EvolutionLinesItem evolutionItem={evolutionItem} isMain={isMain} />
      <EvolutionItemDetails evolutionItem={evolutionItem} isMain={isMain} />
    </div>
  </div>
);

const PreviousVersionsItem: FunctionComponent = () => (
  <div className="item">
    <div className="log-entry-container" data-testid="log-entry-container">
      <div className="line-container" />
      <div className="details-container" data-testid="previous-versions-details-container">
        Previous Versions
      </div>
    </div>
  </div>
);

const SpecEvolutionBranchContainer: FunctionComponent<EvolutionBranchProps> = ({ evolutionBranch, isMain }) => {
  const { evolutionItems } = evolutionBranch;
  const branchHeadEvolutionItemIndex = evolutionItems.findIndex((evolutionItem) => evolutionItem.branchName);
  const headAndPrLogItems = evolutionItems.slice(0, branchHeadEvolutionItemIndex + 1).map((evolutionItem) => (
    <LogEntryContainer key={evolutionItem.ref} evolutionItem={evolutionItem} isMain={isMain} />
  ));
  const previousVersionLogItems = evolutionItems.slice(branchHeadEvolutionItemIndex + 1).map((evolutionItem) => (
    <LogEntryContainer key={evolutionItem.ref} evolutionItem={evolutionItem} isMain={isMain} />
  ));

  const previousVersionsEntry = isMain ? (<PreviousVersionsItem />) : null;

  return (
    <div className="evolution-branch-container" data-testid="evolution-branch-container">
      {headAndPrLogItems}
      {previousVersionsEntry}
      {previousVersionLogItems}
    </div>
  );
};

export default SpecEvolutionBranchContainer;

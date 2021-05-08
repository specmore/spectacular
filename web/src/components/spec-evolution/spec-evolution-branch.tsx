import React, { FunctionComponent } from 'react';
import { Icon, Label } from 'semantic-ui-react';
import { EvolutionBranch, EvolutionItem } from '../../backend-api-client';
import EvolutionLinesItem from './evolution-lines-item';
import EvolutionItemDetails from './evolution-item-details';
import { isShowSpecEvolutionPreviousVersions, ShowSpecEvolutionPreviousVersionsToggleButton } from '../../routes';

interface EvolutionBranchProps {
  evolutionBranch: EvolutionBranch;
  isMain?: boolean;
}

interface LogEntryContainerProps {
  evolutionItem: EvolutionItem;
  isMain?: boolean;
}

interface PreviousVersionsLabelProps {
  previousVersionsCount: number;
}

const LogEntryContainer: FunctionComponent<LogEntryContainerProps> = ({ evolutionItem, isMain }) => (
  <div className="item">
    <div className="log-entry-container" data-testid="log-entry-container">
      <EvolutionLinesItem evolutionItem={evolutionItem} isMain={isMain} />
      <EvolutionItemDetails evolutionItem={evolutionItem} isMain={isMain} />
    </div>
  </div>
);

const PreviousVersionsLabel: FunctionComponent<PreviousVersionsLabelProps> = ({ previousVersionsCount }) => (
  <div className="item">
    <div className="log-entry-container" data-testid="log-entry-container">
      <div className="line-container" />
      <div className="details-container" data-testid="previous-versions-details-container">
        <div className="centre">
          <span style={{ paddingRight: '1em' }}>Previous Versions</span>
          <Label className="old-version">
            <Icon name="history" />
            {previousVersionsCount}
          </Label>
        </div>
        <ShowSpecEvolutionPreviousVersionsToggleButton />
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

  const previousVersionsCount = evolutionItems.length - branchHeadEvolutionItemIndex - 1;
  const previousVersionsEntry = isMain ? (<PreviousVersionsLabel previousVersionsCount={previousVersionsCount} />) : null;
  const previousVersionLogItems = !isMain || isShowSpecEvolutionPreviousVersions()
    ? evolutionItems.slice(branchHeadEvolutionItemIndex + 1).map((evolutionItem) => (
      <LogEntryContainer key={evolutionItem.ref} evolutionItem={evolutionItem} isMain={isMain} />
    ))
    : null;

  return (
    <div className="evolution-branch-container" data-testid="evolution-branch-container">
      {headAndPrLogItems}
      {previousVersionsEntry}
      {previousVersionLogItems}
    </div>
  );
};

export default SpecEvolutionBranchContainer;

import React, { FunctionComponent } from 'react';
import { EvolutionBranch } from '../../backend-api-client';
import EvolutionLinesItem from './evolution-lines-item';
import EvolutionItemDetails from './evolution-item-details';

interface EvolutionBranchProps {
  evolutionBranch: EvolutionBranch;
  isMain?: boolean;
}

const SpecEvolutionBranchContainer: FunctionComponent<EvolutionBranchProps> = ({ evolutionBranch, isMain }) => {
  const { evolutionItems } = evolutionBranch;
  const logItems = evolutionItems.map((evolutionItem) => (
    <div key={evolutionItem.ref} className="item">
      <div className="log-entry-container" data-testid="log-entry-container">
        <EvolutionLinesItem evolutionItem={evolutionItem} isMain={isMain} />
        <EvolutionItemDetails evolutionItem={evolutionItem} isMain={isMain} />
        {/* <OpenSpecItemContentPageButton specItem={specItem} /> */}
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

import React, { FunctionComponent } from 'react';
import './spec-evolution.less';
import {
  Header,
} from 'semantic-ui-react';
import {
  SpecEvolution,
} from '../../backend-api-client';
import { CloseSpecEvolutionButton } from '../../routes';
import SpecEvolutionBranchContainer from './spec-evolution-branch';

interface SpecEvolutionContainerProps {
  specEvolution: SpecEvolution;
}

const SpecEvolutionContainer: FunctionComponent<SpecEvolutionContainerProps> = ({ specEvolution }) => {
  const { main, releases } = specEvolution;
  const mainBranch = (<SpecEvolutionBranchContainer key={main.branchName} evolutionBranch={main} isMain />);
  const releaseBranches = releases.map((releaseBranch) => (
    <SpecEvolutionBranchContainer key={releaseBranch.branchName} evolutionBranch={releaseBranch} />
  ));
  const evolutionBranches = [...releaseBranches, mainBranch];

  return (
    <div data-testid="spec-evolution-container">
      <CloseSpecEvolutionButton />
      <Header as="h3">Spec Evolution</Header>
      <div className="spec-evolution-log-container">
        {evolutionBranches}
      </div>
    </div>
  );
};

export default SpecEvolutionContainer;

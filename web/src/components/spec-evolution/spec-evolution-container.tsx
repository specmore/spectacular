import React, { FunctionComponent } from 'react';
import './spec-evolution.less';
import {
  Header, Label, Placeholder,
} from 'semantic-ui-react';
import {
  SpecItem, SpecLog, useGetInterfaceSpecEvolution,
} from '../../backend-api-client';
import { CloseSpecEvolutionButton, OpenSpecItemContentPageButton, ViewSpecLinkButton } from '../../routes';
import SpecEvolutionBranchContainer from './spec-evolution-branch';

interface SpecLogItemProps {
  specItem: SpecItem;
}

const PlaceholderEvolutionBranch: FunctionComponent<SpecLogItemProps> = ({ specItem }) => (
  <div className="evolution-branch-container">
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
      <div className="log-entry-container" data-testid="log-entry-container">
        <div className="line-container">
          <div className="latest-agreed line" />
        </div>
        <div className="details-container">
          <Label color="blue">{specItem.ref}</Label>
          <Label color="blue" tag>{specItem.parseResult.openApiSpec.version}</Label>
          <div className="centre" />
          <OpenSpecItemContentPageButton specItem={specItem} />
          <ViewSpecLinkButton refName={specItem.ref} />
        </div>
      </div>
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

  const { data: interfaceSpecEvolutionResult, loading } = getInterfaceSpecEvolution;

  let evolutionBranches = null;
  if (loading) {
    evolutionBranches = [(<PlaceholderEvolutionBranch key="placeholder" specItem={specLog.latestAgreed} />)];
  } else {
    const { main, releases } = interfaceSpecEvolutionResult.specEvolution;
    const mainBranch = (<SpecEvolutionBranchContainer key={main.branchName} evolutionBranch={main} isMain />);
    const releaseBranches = releases.map((releaseBranch) => (
      <SpecEvolutionBranchContainer key={releaseBranch.branchName} evolutionBranch={releaseBranch} />
    ));
    evolutionBranches = [...releaseBranches, mainBranch];
  }

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

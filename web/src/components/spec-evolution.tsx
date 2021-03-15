import React, { FunctionComponent } from 'react';
import './spec-evolution.less';
import {
  Header, Label, Placeholder,
} from 'semantic-ui-react';
import {
  SpecItem, SpecLog, useGetInterfaceSpecEvolution,
} from '../backend-api-client';
import { CloseSpecEvolutionButton, OpenSpecItemContentPageButton, ViewSpecLinkButton } from '../routes';
import SpecEvolutionBranchContainer from './spec-evolution-branch';

interface SpecLogItemProps {
  specItem: SpecItem;
  interfaceName: string;
}

const PlaceholderEvolutionBranch: FunctionComponent<SpecLogItemProps> = ({ specItem, interfaceName }) => (
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
          <ViewSpecLinkButton refName={specItem.ref} interfaceName={interfaceName} />
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
    evolutionBranches = [(<PlaceholderEvolutionBranch specItem={specLog.latestAgreed} interfaceName={interfaceName} />)];
  } else {
    const { main, releases } = interfaceSpecEvolutionResult.specEvolution;
    const mainEvolutionBranch = (<SpecEvolutionBranchContainer evolutionBranch={main} isMain />);
    const releaseEvolutionBranches = releases.map((releaseBranch) => (<SpecEvolutionBranchContainer evolutionBranch={releaseBranch} />));
    evolutionBranches = [...releaseEvolutionBranches, mainEvolutionBranch];
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

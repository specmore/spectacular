import React, { FunctionComponent } from 'react';
import {
  Label, Icon, Item, Button, Header,
} from 'semantic-ui-react';
import { SpecLog } from '../backend-api-client';
import { CloseSpecEvolutionButton } from '../routes';


interface SpecLogProps {
  specLog: SpecLog;
}

const SpecEvolutionContainer: FunctionComponent<SpecLogProps> = ({ specLog }) => {
  const proposedChangesCount = specLog.proposedChanges.length;

  return (
    <div data-testid="spec-evolution-container">
      <CloseSpecEvolutionButton />
      <Header as="h3">Spec Evolution</Header>
    </div>
  );
};

export default SpecEvolutionContainer;

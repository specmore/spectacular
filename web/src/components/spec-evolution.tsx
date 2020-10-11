import React, { FunctionComponent } from 'react';
import {
  Header,
} from 'semantic-ui-react';
import { Gitgraph, templateExtend, TemplateName } from '@gitgraph/react';
import { SpecLog } from '../backend-api-client';
import { CloseSpecEvolutionButton } from '../routes';

const MASTER_BRANCH_COLOR = '#5E81AC';
const PR_BRANCH_COLOR = '#8FBCBB';
const MESSAGE_COLOR = '#2E3440';

interface SpecLogProps {
  specLog: SpecLog;
}

const SpecEvolutionContainer: FunctionComponent<SpecLogProps> = ({ specLog }) => {
  // const gitgraphOption = { mode: Mode.Compact };
  const template = templateExtend(TemplateName.Metro, {
    commit: {
      message: {
        displayAuthor: false,
        displayHash: false,
        color: MESSAGE_COLOR,
      },
    },
  });
  const gitgraphOption = {
    author: ' ',
    template,
  };

  return (
    <div data-testid="spec-evolution-container">
      <CloseSpecEvolutionButton />
      <Header as="h3">Spec Evolution</Header>
      <Gitgraph options={gitgraphOption}>
        {(gitgraph) => {
          const master = gitgraph.branch({
            name: 'master',
            style: {
              color: MASTER_BRANCH_COLOR,
            },
            commitDefaultOptions: {
              style: {
                color: MASTER_BRANCH_COLOR,
                dot: {
                  color: MASTER_BRANCH_COLOR,
                },
              },
            },
          });
          const latestAgreedCommit = master.commit({
            subject: 'Latest agreed version',
            tag: specLog.latestAgreed.parseResult.openApiSpec.version,
          });

          console.log('latestAgreedCommit', latestAgreedCommit);

          specLog.proposedChanges.forEach((proposedChange) => {
            const branchName = `PR #${proposedChange.pullRequest.number}`;
            const prBranch = latestAgreedCommit.branch({
              name: branchName,
              style: {
                color: PR_BRANCH_COLOR,
              },
              commitDefaultOptions: {
                style: {
                  color: PR_BRANCH_COLOR,
                  dot: {
                    color: PR_BRANCH_COLOR,
                  },
                },
              },
            });
            prBranch.commit(proposedChange.pullRequest.title);
          });
        }}
      </Gitgraph>
    </div>
  );
};

export default SpecEvolutionContainer;

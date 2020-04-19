import React from 'react';
import { Label, Segment } from 'semantic-ui-react';
import SpecRevision from './spec-revision';

const PullRequestHeader = ({ pullRequest }) => (
  <a href={pullRequest.url} target="_blank">
    <Label circular color="grey">
      #
      {pullRequest.number}
    </Label>
    <span style={{ marginLeft: '0.5em', fontWeight: 'bold' }}>{pullRequest.title}</span>
  </a>
);

const PullRequestLabels = ({ pullRequest }) => (
  <>
    {pullRequest.labels.map((value, index) => (<Label key={index}>{value}</Label>)) }
  </>
);

const ProposedChange = ({ pullRequest, specItem }) => (
  <>
    <div>
      <PullRequestHeader pullRequest={pullRequest} />
    </div>
    <div style={{ marginTop: '0.5em' }}>
      <SpecRevision specItem={specItem} branchColor="yellow" />
    </div>
    <div style={{ marginTop: '0.5em' }}>
      <PullRequestLabels pullRequest={pullRequest} />
    </div>
  </>
);

export default ProposedChange;

import React, { FunctionComponent } from 'react';
import { Button, Icon, Label } from 'semantic-ui-react';
import { EvolutionItem } from '../../backend-api-client';
import { ViewSpecLinkButton } from '../../routes';

interface EvolutionItemProps {
  evolutionItem: EvolutionItem;
  isMain?: boolean;
}

const EvolutionItemDetails: FunctionComponent<EvolutionItemProps> = ({ evolutionItem, isMain }) => {
  const {
    pullRequest, tag, branchName, ref,
  } = evolutionItem;

  let centreDiv = (<div className="centre" />);

  let tagLabel = null;
  if (tag) {
    let colourClassName;
    if (isMain) {
      colourClassName = branchName ? 'latest-agreed' : 'old-version';
    } else {
      colourClassName = 'upcoming-release';
    }
    tagLabel = (<Label data-testid="tag-label" className={colourClassName} tag>{tag}</Label>);
  }

  let branchNameLabel = null;
  if (branchName) {
    const colourClassName = isMain ? 'latest-agreed' : 'upcoming-release';
    branchNameLabel = (<Label data-testid="branch-name-label" className={colourClassName}>{branchName}</Label>);
  }

  let prLabel = null;
  if (pullRequest) {
    prLabel = (
      <Button
        icon
        labelPosition="right"
        size="mini"
        color="green"
        href={pullRequest.url}
        target="_blank"
        rel="noopener noreferrer"
        data-testid="pull-request-button"
      >
        PR #
        {pullRequest.number}
        <Icon name="code branch" />
      </Button>
    );
    centreDiv = (<div data-testid="pull-request-title" className="centre">{pullRequest.title}</div>);
  }

  const viewSpecLinkButton = (<ViewSpecLinkButton refName={ref} withoutLabel />);

  return (
    <div className="details-container">
      {branchNameLabel}
      {tagLabel}
      {prLabel}
      {centreDiv}
      {viewSpecLinkButton}
    </div>
  );
};

export default EvolutionItemDetails;

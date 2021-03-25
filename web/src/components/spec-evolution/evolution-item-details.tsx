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
    pullRequest, tags, branchName, ref, specItem,
  } = evolutionItem;

  let centreDiv = (<div className="centre" />);

  let itemColourClassName: string;
  if (pullRequest) {
    itemColourClassName = 'change-proposal';
  } else {
    itemColourClassName = 'upcoming-release';
    if (isMain) {
      itemColourClassName = branchName ? 'latest-agreed' : 'old-version';
    }
  }

  let tagLabels = null;
  if (tags) {
    tagLabels = tags.map((tag) => (
      <Label key={tag} data-testid="tag-label" className={itemColourClassName} tag>{tag}</Label>
    ));
  }

  let branchNameLabel = null;
  if (branchName) {
    branchNameLabel = (<Label data-testid="branch-name-label" className={itemColourClassName}>{branchName}</Label>);
  }

  let fileVersionLabel = null;
  if (specItem) {
    if (specItem.parseResult && specItem.parseResult.openApiSpec) {
      fileVersionLabel = (
        <Label data-testid="file-version-label" className={itemColourClassName} pointing="left">
          {specItem.parseResult.openApiSpec.version}
        </Label>
      );
    }
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
      {prLabel}
      {branchNameLabel}
      {fileVersionLabel}
      {tagLabels}
      {centreDiv}
      {viewSpecLinkButton}
    </div>
  );
};

export default EvolutionItemDetails;

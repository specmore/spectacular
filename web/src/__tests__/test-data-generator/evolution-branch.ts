import { EvolutionBranch, EvolutionItem, PullRequest } from '../../backend-api-client';
import EvolutionItemGen from './evolution-item';
import PullRequestGen from './pull-request';

interface GenerateEvolutionBranchParameters {
  branchName?: string;
  numberPullRequests?: number;
  numberPreviousVersions?: number;
  evolutionItemsOverride?: EvolutionItem[];
}

const generateEvolutionBranch = ({
  branchName = 'mainBranch',
  numberPullRequests = 0,
  numberPreviousVersions = 0,
  evolutionItemsOverride = null,
}: GenerateEvolutionBranchParameters = {}): EvolutionBranch => {
  let evolutionItems: EvolutionItem[] = null;
  if (evolutionItemsOverride) {
    evolutionItems = evolutionItemsOverride;
  } else {
    const branchHeadItem = EvolutionItemGen.generateEvolutionItem({ ref: branchName, branchName });
    const pullRequestItems = [];
    for (let i = 0; i < numberPullRequests; i += 1) {
      const pullRequest: PullRequest = PullRequestGen.generatePullRequest({ number: i });
      pullRequestItems.push(EvolutionItemGen.generateEvolutionItem({ ref: pullRequest.branchName, pullRequest }));
    }
    const previousVersionItems = [];
    for (let i = 0; i < numberPreviousVersions; i += 1) {
      const tag = `1.${numberPreviousVersions - i}`;
      previousVersionItems.push(EvolutionItemGen.generateEvolutionItem({ ref: tag, tags: [tag] }));
    }
    evolutionItems = [...pullRequestItems, branchHeadItem, ...previousVersionItems];
  }
  return {
    branchName,
    evolutionItems,
  };
};


export default {
  generateEvolutionBranch,
};

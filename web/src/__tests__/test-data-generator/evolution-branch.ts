import { EvolutionBranch, EvolutionItem, PullRequest } from '../../backend-api-client';
import EvolutionItemGen from './evolution-item';
import PullRequestGen from './pull-request';

interface GenerateEvolutionBranchParameters {
  branchName?: string;
  numberPullRequests?: number;
  evolutionItemsOverride?: EvolutionItem[];
}

const generateEvolutionBranch = ({
  branchName = 'mainBranch',
  numberPullRequests = 0,
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
    evolutionItems = [branchHeadItem, ...pullRequestItems];
  }
  return {
    branchName,
    evolutionItems,
  };
};


export default {
  generateEvolutionBranch,
};

import { EvolutionItem, PullRequest, SpecItem } from '../../backend-api-client';
import SpecItemGen from './spec-item';

interface GenerateEvolutionItemParameters {
  ref?: string;
  branchName?: string;
  tags?: string[];
  pullRequest?: PullRequest;
  specItem?: SpecItem;
}

const generateEvolutionItem = ({
  ref = 'test-ref',
  branchName = null,
  tags = [],
  pullRequest = null,
  specItem = SpecItemGen.generateSpecItem(),
}: GenerateEvolutionItemParameters = {}): EvolutionItem => ({
  ref,
  branchName,
  tags,
  pullRequest,
  specItem,
});

export default {
  generateEvolutionItem,
};

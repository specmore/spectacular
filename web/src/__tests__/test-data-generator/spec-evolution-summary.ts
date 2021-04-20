import { SpecEvolutionSummary, SpecItem } from '../../backend-api-client';
import SpecItemGen from './spec-item';

interface GenerateSpecEvolutionSummaryParameters {
  interfaceName?: string,
  latestAgreed?: SpecItem;
  upcomingReleaseCount?: number;
  proposedChangesCount?: number;
  agreedVersionTagCount?: number;
}

const generateSpecEvolutionSummary = ({
  interfaceName = 'testInterface',
  latestAgreed = SpecItemGen.generateSpecItem(),
  upcomingReleaseCount = 0,
  proposedChangesCount = 0,
  agreedVersionTagCount = 0,
}: GenerateSpecEvolutionSummaryParameters = {}): SpecEvolutionSummary => ({
  interfaceName,
  latestAgreed,
  upcomingReleaseCount,
  proposedChangesCount,
  agreedVersionTagCount,
});

export default {
  generateSpecEvolutionSummary,
};

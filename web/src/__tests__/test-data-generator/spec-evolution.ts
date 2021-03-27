import { SpecEvolution } from '../../backend-api-client';
import EvolutionBranchGen from './evolution-branch';
import EvolutionItemGen from './evolution-item';
import SpecItemGen from './spec-item';

interface LatestAgreedEvolutionItemParameters {
  specFileTitle?: string;
  specFileVersion?: string;
  specFileParseErrorMessage?: string;
}

interface GenerateSpecEvolutionParameters {
  numberReleaseBranches?: number;
  numberPullRequestItemsPerBranch?: number;
  latestAgreedEvolutionItemParameters?: LatestAgreedEvolutionItemParameters;
}

const generateSpecEvolution = ({
  numberReleaseBranches = 0,
  numberPullRequestItemsPerBranch = 0,
  latestAgreedEvolutionItemParameters = null,
}: GenerateSpecEvolutionParameters = {}): SpecEvolution => {
  const releaseBranches = [];
  for (let i = 0; i < numberReleaseBranches; i += 1) {
    releaseBranches.push(EvolutionBranchGen.generateEvolutionBranch({
      branchName: `releaseBranch${i}`,
      numberPullRequests: numberPullRequestItemsPerBranch,
    }));
  }

  const mainBranch = EvolutionBranchGen.generateEvolutionBranch({
    numberPullRequests: numberPullRequestItemsPerBranch,
  });

  if (latestAgreedEvolutionItemParameters) {
    let latestAgreeSpecItem = null;
    if (latestAgreedEvolutionItemParameters.specFileParseErrorMessage) {
      latestAgreeSpecItem = SpecItemGen.generateSpecItemWithError(latestAgreedEvolutionItemParameters.specFileParseErrorMessage);
    } else {
      latestAgreeSpecItem = SpecItemGen.generateSpecItem({
        ref: mainBranch.branchName,
        specFileTitle: latestAgreedEvolutionItemParameters.specFileTitle,
        specFileVersion: latestAgreedEvolutionItemParameters.specFileVersion,
      });
    }
    const mainBranchHeadEvolutionItem = EvolutionItemGen.generateEvolutionItem({
      ref: mainBranch.branchName,
      branchName: mainBranch.branchName,
      specItem: latestAgreeSpecItem,
    });
    mainBranch.evolutionItems = [mainBranchHeadEvolutionItem];
  }
  return {
    interfaceName: 'testInterface',
    releases: releaseBranches,
    main: mainBranch,
  };
};

export default {
  generateSpecEvolution,
};

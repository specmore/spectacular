import { EvolutionBranch, EvolutionItem, SpecEvolution } from '../../backend-api-client';

interface GenerateSpecEvolutionParameters {
  numberReleaseBranches?: number;
}

const generateEvolutionBranch = ({ branchName = 'mainBranch' } = {}): EvolutionBranch => {
  const evolutionItems: EvolutionItem[] = [];
  return {
    branchName,
    evolutionItems,
  };
};

const generateSpecEvolution = ({ numberReleaseBranches = 0 }: GenerateSpecEvolutionParameters = {}): SpecEvolution => {
  const releaseBranches = [];
  for (let i = 0; i < numberReleaseBranches; i += 1) {
    releaseBranches.push(generateEvolutionBranch({ branchName: `releaseBranch${i}` }));
  }
  const mainBranch = generateEvolutionBranch();
  return {
    interfaceName: 'testInterface',
    releases: releaseBranches,
    main: mainBranch,
  };
};

export default {
  generateSpecEvolution,
};

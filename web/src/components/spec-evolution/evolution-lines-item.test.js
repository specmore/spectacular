import React from 'react';
import { render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import EvolutionLinesItem from './evolution-lines-item';

describe('EvolutionLinesItem component', () => {
  test('main branch evolution items behind the branch head renders an old version main branch line', async () => {
    // given a spec evolution item on the main branch behind the branch head
    const evolutionItem = {};
    const isMain = true;

    // when spec evolution component renders
    const { getByTestId } = render(<EvolutionLinesItem evolutionItem={evolutionItem} isMain={isMain} />);

    // then only one line is displayed
    const evolutionLines = getByTestId('evolution-lines-container');
    expect(evolutionLines).toBeInTheDocument();
    expect(evolutionLines.children).toHaveLength(1);

    // and the line is a main branch line styled for an old version item
    const mainBranchLine = evolutionLines.children[0];
    expect(mainBranchLine).toHaveClass('old-version');
  });

  test('main branch evolution items on the branch head renders a lasted agreed main branch line', async () => {
    // given a spec evolution item on the main branch on the branch head
    const evolutionItem = {
      branchName: 'a-branch',
    };
    const isMain = true;

    // when spec evolution component renders
    const { getByTestId } = render(<EvolutionLinesItem evolutionItem={evolutionItem} isMain={isMain} />);

    // then only one line is displayed
    const evolutionLines = getByTestId('evolution-lines-container');
    expect(evolutionLines).toBeInTheDocument();
    expect(evolutionLines.children).toHaveLength(1);

    // and the line is a main branch line styled for a lasted agreed item
    const mainBranchLine = evolutionLines.children[0];
    expect(mainBranchLine).toHaveClass('latest-agreed');
  });

  test('main branch pull request evolution items render a lasted agreed main branch line and change proposal line', async () => {
    // given a spec evolution item for a pull request to the main branch
    const evolutionItem = {
      pullRequest: 'a-branch',
    };
    const isMain = true;

    // when spec evolution component renders
    const { getByTestId } = render(<EvolutionLinesItem evolutionItem={evolutionItem} isMain={isMain} />);

    // then two lines are displayed
    const evolutionLines = getByTestId('evolution-lines-container');
    expect(evolutionLines).toBeInTheDocument();
    expect(evolutionLines.children).toHaveLength(2);

    // and the first line is a main branch line styled for a lasted agreed item
    const mainBranchLine = evolutionLines.children[0];
    expect(mainBranchLine).toHaveClass('latest-agreed');

    // and the second line is a pull request branch line
    const pullRequestLine = evolutionLines.children[1];
    expect(pullRequestLine).toHaveClass('change-proposal');
  });

  test('non-main branch evolution items render a hidden main branch line and release branch line', async () => {
    // given a spec evolution item not on the main branch
    const evolutionItem = {};
    const isMain = false;

    // when spec evolution component renders
    const { getByTestId } = render(<EvolutionLinesItem evolutionItem={evolutionItem} isMain={isMain} />);

    // then two lines are displayed
    const evolutionLines = getByTestId('evolution-lines-container');
    expect(evolutionLines).toBeInTheDocument();
    expect(evolutionLines.children).toHaveLength(2);

    // and the first line is a main branch line with a blank style
    const mainBranchLine = evolutionLines.children[0];
    expect(mainBranchLine).toHaveClass('blank');

    // and the second line is an upcoming release branch
    const upcomingReleaseLine = evolutionLines.children[1];
    expect(upcomingReleaseLine).toHaveClass('upcoming-release');
  });

  test('non-main branch pull request evolution items render a hidden main branch line and release branch line', async () => {
    // given a spec evolution item for a pull request not to the main branch
    const evolutionItem = {
      pullRequest: 'a-branch',
    };
    const isMain = false;

    // when spec evolution component renders
    const { getByTestId } = render(<EvolutionLinesItem evolutionItem={evolutionItem} isMain={isMain} />);

    // then three lines are displayed
    const evolutionLines = getByTestId('evolution-lines-container');
    expect(evolutionLines).toBeInTheDocument();
    expect(evolutionLines.children).toHaveLength(3);

    // and the first line is a main branch line with a blank style
    const mainBranchLine = evolutionLines.children[0];
    expect(mainBranchLine).toHaveClass('blank');

    // and the second line is an upcoming release branch
    const upcomingReleaseLine = evolutionLines.children[1];
    expect(upcomingReleaseLine).toHaveClass('upcoming-release');

    // and the third line is a pull request branch line
    const pullRequestLine = evolutionLines.children[2];
    expect(pullRequestLine).toHaveClass('change-proposal');
  });
});

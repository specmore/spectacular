import React from 'react';
import { render, within } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import {
  OpenSpecItemContentPageButton as OpenSpecItemContentPageButtonMock,
  ViewSpecLinkButton as ViewSpecLinkButtonMock,
} from '../../routes';
import EvolutionItemDetails from './evolution-item-details';
import testDataGenerator from '../../__tests__/test-data-generator';

jest.mock('../../routes', () => ({
  ViewSpecLinkButton: jest.fn(() => null),
  OpenSpecItemContentPageButton: jest.fn(() => null),
}));

describe('EvolutionItemDetails component', () => {
  test('evolution item renders a view spec button', async () => {
    // given a spec evolution item on with a tag without a branch name on a main branch
    const evolutionItem = {};
    const isMain = true;

    // when the EvolutionItemDetails component renders
    render(<EvolutionItemDetails evolutionItem={evolutionItem} isMain={isMain} />);

    // then a ViewSpecLinkButton is shown
    expect(ViewSpecLinkButtonMock).toHaveBeenCalledTimes(1);
  });

  test('evolution item with specItem htmlUrl renders a OpenSpecItemContentPageButton', async () => {
    const specItem = testDataGenerator.SpecItem.generateSpecItem();
    const evolutionItem = { specItem };
    const isMain = true;

    // when the EvolutionItemDetails component renders
    render(<EvolutionItemDetails evolutionItem={evolutionItem} isMain={isMain} />);

    // then a OpenSpecItemContentPageButtonMock is shown
    expect(OpenSpecItemContentPageButtonMock).toHaveBeenCalledTimes(1);
  });

  test('evolution item with tags behind the branch head renders an old version tag', async () => {
    // given a spec evolution item on with a tag without a branch name on a main branch
    const evolutionItem = { tags: ['a-tag'] };
    const isMain = true;

    // when the EvolutionItemDetails component renders
    const { getByTestId } = render(<EvolutionItemDetails evolutionItem={evolutionItem} isMain={isMain} />);

    // then an 'old version' styled tag is shown
    const tagLabel = getByTestId('tag-label');
    expect(tagLabel).toBeInTheDocument();
    expect(tagLabel).toHaveClass('old-version');
  });

  test('evolution item with tag on the branch head renders a latest agreed style tag', async () => {
    // given a spec evolution item on with a tag with a branch name on a main branch
    const evolutionItem = { tags: ['a-tag'], branchName: 'a-branch' };
    const isMain = true;

    // when the EvolutionItemDetails component renders
    const { getByTestId } = render(<EvolutionItemDetails evolutionItem={evolutionItem} isMain={isMain} />);

    // then an 'old version' styled tag is shown
    const tagLabel = getByTestId('tag-label');
    expect(tagLabel).toBeInTheDocument();
    expect(tagLabel).toHaveClass('latest-agreed');
  });

  test('evolution item with tag a release branch renders an upcoming release style tag', async () => {
    // given a spec evolution item on with a tag on a non-main branch
    const evolutionItem = { tags: ['a-tag'] };
    const isMain = false;

    // when the EvolutionItemDetails component renders
    const { getByTestId } = render(<EvolutionItemDetails evolutionItem={evolutionItem} isMain={isMain} />);

    // then an 'old version' styled tag is shown
    const tagLabel = getByTestId('tag-label');
    expect(tagLabel).toBeInTheDocument();
    expect(tagLabel).toHaveClass('upcoming-release');
  });

  test('evolution item on the branch head of the main branch renders a latest agreed styled branch name label', async () => {
    // given a spec evolution item on with a branch name on a main branch
    const evolutionItem = { branchName: 'a-branch' };
    const isMain = true;

    // when the EvolutionItemDetails component renders
    const { getByTestId } = render(<EvolutionItemDetails evolutionItem={evolutionItem} isMain={isMain} />);

    // then an 'latest-agreed' styled tag is shown
    const branchNameLabel = getByTestId('branch-name-label');
    expect(branchNameLabel).toBeInTheDocument();
    expect(branchNameLabel).toHaveClass('latest-agreed');
  });

  test('evolution item on a branch head with a successful file parse result render a file version label', async () => {
    // given a successful openapi file parse result
    const specItem = testDataGenerator.SpecItem.generateSpecItem();

    // and a spec evolution item on with a branch name on a main branch with the parse result
    const evolutionItem = { branchName: 'a-branch', specItem };
    const isMain = true;

    // when the EvolutionItemDetails component renders
    const { getByTestId } = render(<EvolutionItemDetails evolutionItem={evolutionItem} isMain={isMain} />);

    // then an 'latest-agreed' file version label is shown
    const branchNameLabel = getByTestId('file-version-label');
    expect(branchNameLabel).toBeInTheDocument();
    expect(branchNameLabel).toHaveClass('latest-agreed');
  });

  test('evolution item for a pull request renders a button with the PR number and a div with the title', async () => {
    // given a spec evolution item for a pull request
    const evolutionItem = {
      pullRequest: {
        number: 12345,
        title: 'test-title',
      },
    };
    const isMain = true;

    // when the EvolutionItemDetails component renders
    const { getByTestId } = render(<EvolutionItemDetails evolutionItem={evolutionItem} isMain={isMain} />);

    // then a pull request button is shown with the PR number
    const pullRequestButton = getByTestId('pull-request-button');
    expect(pullRequestButton).toBeInTheDocument();
    {
      const { getByText } = within(pullRequestButton);
      expect(getByText('12345', { exact: false })).toBeInTheDocument();
    }

    // and the pull request title is shown
    const pullRequestTitle = getByTestId('pull-request-title');
    expect(pullRequestTitle).toBeInTheDocument();
    {
      const { getByText } = within(pullRequestTitle);
      expect(getByText('test-title')).toBeInTheDocument();
    }
  });
});

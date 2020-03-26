import React from "react";
import '@testing-library/jest-dom/extend-expect';
import SpecRevision from "./spec-revision";
import { renderWithRouter } from '../common/test-utils';

const validSpecItem = { 
    "repository": { 
        "owner": "test-owner", "name": "specs-test", "htmlUrl": "https://github.com/test-owner/specs-test", "nameWithOwner": "test-owner/specs-test"
    }, 
    "filePath": "specs/example-template.yaml", 
    "ref": "master",
    "parseResult": { "openApiSpec": { "title": "An empty API spec", "version": "0.1.0", "operations": [] }, "errors": [] } 
};

describe("SpecRevision component", () => {
    test("shows latest agreed spec item with branch name and version", async () => {
        // given a spec item with branch name and version
        const specItem = validSpecItem;

        // when component renders
        const { getByText } = renderWithRouter(<SpecRevision specItem={specItem} />);

        // then the master branch is shown
        expect(getByText("master")).toBeInTheDocument();

        // and the open api spec version is shown
        expect(getByText("0.1.0")).toBeInTheDocument();
    });

    test("shows the branch color specified", async () => {
        // given a spec item with branch name and version
        const specItem = validSpecItem;

        // and a branch colour of yellow
        const branchColor = 'yellow'

        // when the component renders
        const { getByText } = renderWithRouter(<SpecRevision specItem={specItem} branchColor={branchColor} />);

        // then the master branch is shown in yellow
        expect(getByText("master")).toHaveClass('yellow');
    });

    test("defaults to the color olive if no branch color is specified", async () => {
        // given a spec item with branch name and version
        const specItem = validSpecItem;

        // when the component renders
        const { getByText } = renderWithRouter(<SpecRevision specItem={specItem} />);

        // then the master branch is shown in olive
        expect(getByText("master")).toHaveClass('olive');
    });

    test("shows a view spec button", async () => {
        // given a spec item with branch name and version
        const specItem = validSpecItem;

        // when the component renders
        const { getByTestId } = renderWithRouter(<SpecRevision specItem={specItem} />);

        // then the view spec button is shown
        expect(getByTestId('view-spec-button')).toBeInTheDocument();
    });
});
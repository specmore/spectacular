import React from "react";
import '@testing-library/jest-dom/extend-expect';
import SpecRevision from "./spec-revision";
import { renderWithRouter } from '../common/test-utils';
import moment from "moment";

const validSpecItem = { 
    "repository": { 
        "owner": "test-owner", "name": "specs-test", "htmlUrl": "https://github.com/test-owner/specs-test", "nameWithOwner": "test-owner/specs-test"
    }, 
    "filePath": "specs/example-template.yaml", 
    "ref": "master",
    "sha": "e6f9f693f080018158d1dd0394c53ab354a8be42",
    "lastModified": "2020-02-18T22:33:51Z",
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
    
    test("shows last modified date relative to now", async () => {
        // given a spec item with a last modified date 5 minutes ago
        const specItem = validSpecItem;
        specItem.lastModified = moment().subtract(5, 'minutes').toISOString();

        // when component renders
        const { getByText } = renderWithRouter(<SpecRevision specItem={specItem} />);

        // then the master branch is shown
        expect(getByText("master")).toBeInTheDocument();

        // and the open api spec version is shown
        expect(getByText("0.1.0")).toBeInTheDocument();

        // and last modified date is shown
        expect(getByText(/5 minutes ago/i)).toBeInTheDocument();
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
});
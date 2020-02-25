import React from "react";
import '@testing-library/jest-dom/extend-expect';
import ProposedChangeItem from './proposed-change-item';
import { renderWithRouter } from '../common/test-utils';

describe("ProposedChangeItem component", () => {
    test("shows pull request information for valid proposed item", async () => {
        // given a pull request
        const pullRequest = {
            "repository": {
                "owner":"test-owner",
                "name":"specs-test",
                "htmlUrl":"https://github.com/test-owner/specs-test",
                "nameWithOwner":"test-owner/specs-test"
            },
            "branchName": "change-branch",
            "number": 1,
            "url": "https://github.com/test-owner/specs-test/pull/1",
            "labels": ["project-x"],
            "changedFiles": ["specs/example-template.yaml"],
            "title":"example change to spec"
        };

        // // and valid proposed change spec item with title and version
        // const changedSpecItem = { 
        //     "repository": { 
        //         "owner": "test-owner", "name": "specs-test", "htmlUrl": "https://github.com/test-owner/specs-test", "nameWithOwner": "test-owner/specs-test"
        //     }, 
        //     "filePath": "specs/example-template.yaml", 
        //     "ref": "change-branch",
        //     "parseResult": { "openApiSpec": { "title": "An changed API spec", "version": "0.1.1", "operations": [] }, "errors": [] } 
        // };

        // and a proposed change for that pull request
        const proposedChange = {
            pullRequest
        };

        // when a proposed change item component is rendered with the given proposed item
        const { getByText, getByTestId } = renderWithRouter(<ProposedChangeItem {...proposedChange} />);

        // then the proposed change item segment is shown
        expect(getByTestId('proposed-change-item')).toBeInTheDocument();

        // and the pull request number and title header is shown
        expect(getByText("#1", { exact: false })).toBeInTheDocument();
        expect(getByText("example change to spec", { exact: false })).toBeInTheDocument();

        // and a link to the pull request page on github is given
        expect(getByText('#1', { exact: false }).closest('a')).toHaveAttribute('href', 'https://github.com/test-owner/specs-test/pull/1');

        // // and the source branch and open api spec version is shown
        // expect(getByText("change-branch")).toBeInTheDocument();
        // expect(getByText("0.1.1")).toBeInTheDocument();
    });
});
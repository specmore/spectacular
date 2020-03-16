import React from "react";
import '@testing-library/jest-dom/extend-expect';
import LatestAgreedVersion from "./latest-agreed-version";
import { renderWithRouter } from '../common/test-utils';

describe("LatestAgreedVersion component", () => {
    test("shows latest agreed spec item with branch name and version", async () => {
        // given a valid latest agreed spec item with title and version
        const specItem = { 
            "repository": { 
                "owner": "test-owner", "name": "specs-test", "htmlUrl": "https://github.com/test-owner/specs-test", "nameWithOwner": "test-owner/specs-test"
            }, 
            "filePath": "specs/example-template.yaml", 
            "ref": "master",
            "parseResult": { "openApiSpec": { "title": "An empty API spec", "version": "0.1.0", "operations": [] }, "errors": [] } 
        };

        // when the latest agreed version component renders
        const { getByText } = renderWithRouter(<LatestAgreedVersion latestAgreedSpecItem={specItem} />);

        // then the master branch is shown
        expect(getByText("master")).toBeInTheDocument();

        // and the open api spec title and version is shown
        expect(getByText("0.1.0")).toBeInTheDocument();
    });

    test("shows a view spec button", async () => {
        // given a valid latest agreed spec item with title and version
        const specItem = { 
            "repository": { 
                "owner": "test-owner", "name": "specs-test", "htmlUrl": "https://github.com/test-owner/specs-test", "nameWithOwner": "test-owner/specs-test"
            }, 
            "filePath": "specs/example-template.yaml", 
            "ref": "master",
            "parseResult": { "openApiSpec": { "title": "An empty API spec", "version": "0.1.0", "operations": [] }, "errors": [] } 
        };

        // when the latest agreed version component renders
        const { getByTestId } = renderWithRouter(<LatestAgreedVersion latestAgreedSpecItem={specItem} />);

        // then the view spec button is shown
        expect(getByTestId('view-spec-button')).toBeInTheDocument();
    });
});
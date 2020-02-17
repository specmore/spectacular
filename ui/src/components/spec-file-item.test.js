import React from "react";
import '@testing-library/jest-dom/extend-expect';
import SpecFileItem from "./spec-file-item";
import { renderWithRouter } from '../common/test-utils';

describe("SpecFileItem component", () => {
    test("shows spec item with valid openApiSpec title and version", async () => {
        // given a catalogue repository
        const repository = {
            "owner": "test-owner",
            "name": "specs-test",
            "htmlUrl": "https://github.com/test-owner/specs-test",
            "nameWithOwner": "test-owner/specs-test"
        };

        // and valid spec item with title and version
        const specItem = { 
            "repository": { 
                "owner": "test-owner", "name": "specs-test", "htmlUrl": "https://github.com/test-owner/specs-test", "nameWithOwner": "test-owner/specs-test"
            }, 
            "filePath": "specs/example-template.yaml", 
            "parseResult": { "openApiSpec": { "title": "An empty API spec", "version": "0.1.0", "operations": [] }, "errors": [] } 
        };

        // when spec file item component renders
        const { getByText } = renderWithRouter(<SpecFileItem catalogueRepository={repository} specItem={specItem} />);

        // then the file path suffixed by the repo name is shown
        expect(getByText("test-owner/specs-test/specs/example-template.yaml")).toBeInTheDocument();

        // and the open api spec title and version is shown
        expect(getByText("An empty API spec")).toBeInTheDocument();
        expect(getByText("0.1.0")).toBeInTheDocument();
    });

    test("renders a view spec button for spec item with no parse result errors", async () => {
        // given a catalogue repository
        const repository = {
            "owner": "test-owner",
            "name": "specs-test",
            "htmlUrl": "https://github.com/test-owner/specs-test",
            "nameWithOwner": "test-owner/specs-test"
        };

        // and spec item with no parse results errors
        const specItem = { 
            "repository": { 
                "owner": "test-owner", "name": "specs-test", "htmlUrl": "https://github.com/test-owner/specs-test", "nameWithOwner": "test-owner/specs-test"
            }, 
            "filePath": "specs/example-template.yaml", 
            "parseResult": { "openApiSpec": { "title": "An empty API spec", "version": "0.1.0", "operations": [] }, "errors": [] } 
        };

        // when spec file item component renders
        const { getByTestId } = renderWithRouter(<SpecFileItem catalogueRepository={repository} specItem={specItem} />);

        // then the view spec button is shown
        expect(getByTestId('view-spec-button')).toBeInTheDocument();
    });

    test("shows spec item error message for spec item with parse result errors", async () => {
        // given a catalogue repository
        const repository = {
            "owner": "test-owner",
            "name": "specs-test",
            "htmlUrl": "https://github.com/test-owner/specs-test",
            "nameWithOwner": "test-owner/specs-test"
        };

        // and spec item with parse errors
        const specItem = { 
            "repository": { 
                "owner": "test-owner", "name": "specs-test", "htmlUrl": "https://github.com/test-owner/specs-test", "nameWithOwner": "test-owner/specs-test"
            }, 
            "filePath": "specs/example-template.yaml", 
            "parseResult": { "openApiSpec":null,"errors":["The spec file could not be found."] } 
        };

        // when spec file item component renders
        const { getByText, getByTestId } = renderWithRouter(<SpecFileItem catalogueRepository={repository} specItem={specItem} />);

        // then the file path suffixed by the repo name is shown
        expect(getByText("test-owner/specs-test/specs/example-template.yaml", { exact: false })).toBeInTheDocument();

        // and the spec file error item is shown with error message
        expect(getByTestId('specification-file-item-error')).toBeInTheDocument();
        expect(getByText("The spec file could not be found.")).toBeInTheDocument();
    });
});
import React from "react";
import '@testing-library/jest-dom/extend-expect';
import SpecFileEvolution from "./spec-file-evolution";
import { renderWithRouter } from '../common/test-utils';

describe("SpecFileEvolution component", () => {
    test("shows latest agreed spec item with valid openApiSpec title and version", async () => {
        // given a catalogue repository
        const repository = {
            "owner": "test-owner",
            "name": "specs-test",
            "htmlUrl": "https://github.com/test-owner/specs-test",
            "nameWithOwner": "test-owner/specs-test"
        };

        // and valid latest agreed spec item with title and version
        const specItem = { 
            "repository": { 
                "owner": "test-owner", "name": "specs-test", "htmlUrl": "https://github.com/test-owner/specs-test", "nameWithOwner": "test-owner/specs-test"
            }, 
            "filePath": "specs/example-template.yaml", 
            "ref": "master",
            "parseResult": { "openApiSpec": { "title": "An empty API spec", "version": "0.1.0", "operations": [] }, "errors": [] } 
        };
        
        const specEvolution = {
            "latestAgreed": specItem
        };

        // when spec file item component renders
        const { getByText } = renderWithRouter(<SpecFileEvolution catalogueRepository={repository} specEvolution={specEvolution} />);

        // then the master branch is shown
        expect(getByText("master")).toBeInTheDocument();

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

        // and latest agreed spec item with no parse results errors
        const specItem = { 
            "repository": { 
                "owner": "test-owner", "name": "specs-test", "htmlUrl": "https://github.com/test-owner/specs-test", "nameWithOwner": "test-owner/specs-test"
            }, 
            "filePath": "specs/example-template.yaml", 
            "ref": "master",
            "parseResult": { "openApiSpec": { "title": "An empty API spec", "version": "0.1.0", "operations": [] }, "errors": [] } 
        };
        
        const specEvolution = {
            "latestAgreed": specItem
        };

        // when spec file item component renders
        const { getByTestId } = renderWithRouter(<SpecFileEvolution catalogueRepository={repository} specEvolution={specEvolution} />);

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
            "ref": "master",
            "parseResult": { "openApiSpec":null,"errors":["The spec file could not be found."] } 
        };
        
        const specEvolution = {
            "latestAgreed": specItem
        };

        // when spec file item component renders
        const { getByText, getByTestId } = renderWithRouter(<SpecFileEvolution catalogueRepository={repository} specEvolution={specEvolution} />);

        // then the file path suffixed by the repo name is shown
        expect(getByText("test-owner/specs-test/specs/example-template.yaml", { exact: false })).toBeInTheDocument();

        // and the spec file error item is shown with error message
        expect(getByTestId('specification-file-evolution-error')).toBeInTheDocument();
        expect(getByText("The spec file could not be found.")).toBeInTheDocument();
    });
});
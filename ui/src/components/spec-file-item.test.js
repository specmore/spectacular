import React from "react";
import '@testing-library/jest-dom/extend-expect';
import SpecFileItem from "./spec-file-item";
import { renderWithRouter } from '../common/test-utils';

describe("SpecFileItem component", () => {
    test("spec file nested under another repo shows repo name as suffix in spec file title", async () => {
        // given a catalogue repository
        const repository = {
            "owner": "test-owner",
            "name": "specs-test",
            "htmlUrl": "https://github.com/test-owner/specs-test",
            "nameWithOwner": "test-owner/specs-test"
        };

        // and a spec file in another repo
        const specFileLocation = {
            "repo": {"owner":"test-owner","name":"specs-repo1","htmlUrl":null,"nameWithOwner":"test-owner/specs-repo1"},
            "file-path": "specs/example-template.yaml"
        };

        // when spec file item component renders
        const { getByText } = renderWithRouter(<SpecFileItem catalogueRepository={repository} specFileLocation={specFileLocation} />);

        // then the title of the spec file shows the file path suffixed by the other repo name
        expect(getByText("test-owner/specs-repo1/specs/example-template.yaml")).toBeInTheDocument();
    });

    test("spec file not nested under another repo shows catalogue repo name as suffix in spec file title", async () => {
        // given a catalogue repository
        const repository = {
            "owner": "test-owner",
            "name": "specs-test",
            "htmlUrl": "https://github.com/test-owner/specs-test",
            "nameWithOwner": "test-owner/specs-test"
        };

        // and a spec file with no repo
        const specFileLocation = {
            "repo": null,
            "file-path": "specs/example-template.yaml"
        };

        // when spec file item component renders
        const { getByText } = renderWithRouter(<SpecFileItem catalogueRepository={repository} specFileLocation={specFileLocation} />);

        // then the name of the file path is shown
        expect(getByText("test-owner/specs-test/specs/example-template.yaml")).toBeInTheDocument();
    });

    test("renders a view spec button", async () => {
        // given a catalogue repository
        const repository = {
            "owner": "test-owner",
            "name": "specs-test",
            "htmlUrl": "https://github.com/test-owner/specs-test",
            "nameWithOwner": "test-owner/specs-test"
        };

        // and a spec file in another repo
        const specFileLocation = {
            "repo": {"owner":"test-owner","name":"specs-repo1","htmlUrl":null,"nameWithOwner":"test-owner/specs-repo1"},
            "file-path": "specs/example-template.yaml"
        };

        // when spec file item component renders
        const { getByTestId } = renderWithRouter(<SpecFileItem catalogueRepository={repository} specFileLocation={specFileLocation} />);

        // then the view spec button is shown
        expect(getByTestId('view-spec-button')).toBeInTheDocument();
    });
});
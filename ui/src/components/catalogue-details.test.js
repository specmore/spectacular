import React from "react";
import '@testing-library/jest-dom/extend-expect';
import CatalogueDetails from "./catalogue-details";
import { renderWithRouter } from '../common/test-utils';

describe("CatalogueDetails component", () => {
  test("renders catalogue details when no error is given", async () => {
    // given a catalogue data item with 2 spec files
    const catalogue = {
        "repository": {
            "owner": "test-owner",
            "name": "specs-test",
            "htmlUrl": "https://github.com/test-owner/specs-test",
            "nameWithOwner": "test-owner/specs-test"
        },
        "catalogueManifest": {
            "name": "Test Catalogue 1",
            "description": "Specifications for all the interfaces in the across the system X.",
            "spec-files": [
                {
                    "repo": null,
                    "file-path": "specs/example-template.yaml"
                },
                {
                    "repo": {"owner":"test-owner","name":"specs-test2","htmlUrl":null,"nameWithOwner":"test-owner/specs-test2"},
                    "file-path": "specs/example-spec.yaml"
                }
            ]
        },
        "error": null
    };

    // when catalogue details component renders
    const { getByTestId, getAllByTestId, getByText } = renderWithRouter(<CatalogueDetails {...catalogue} />);

    // then a catalogue details segment is found
    expect(getByTestId('catalogue-details-segment')).toBeInTheDocument();

    // and the name of the catalogue is shown
    expect(getByText("Test Catalogue 1")).toBeInTheDocument();

    // and the name of the repository is shown
    expect(getByText("test-owner/specs-test")).toBeInTheDocument();

    // and 2 spec file items are found
    expect(getAllByTestId('specification-file-item')).toHaveLength(2);
  });

  
  test("renders a catalogue details error message when an error is given", async () => {
    // given a catalogue data item
    const catalogue = {
        "repository": {
            "owner": "test-owner",
            "name": "specs-test",
            "htmlUrl": "https://github.com/test-owner/specs-test",
            "nameWithOwner": "test-owner/specs-test"
        },
        "catalogueManifest": null,
        "error": "An error occurred while parsing the catalogue manifest yaml file. The following field is missing: bla bla bla"
    };

    // when catalogue details component renders
    const { getByTestId, getByText } = renderWithRouter(<CatalogueDetails {...catalogue} />);

    // then a catalogue details error message is found
    expect(getByTestId('catalogue-details-error-message')).toBeInTheDocument();

    // and the name of the repository is shown
    expect(getByText("test-owner/specs-test")).toBeInTheDocument();

    // and the error message is shown
    expect(getByText("An error occurred while parsing the catalogue manifest yaml file. The following field is missing: bla bla bla")).toBeInTheDocument();
  });
});
import React from "react";
import '@testing-library/jest-dom/extend-expect';
import CatalogueListItem from "./catalogue-list-item";
import { renderWithRouter } from '../common/test-utils';

describe("CatalogueListItem component", () => {
  test("renders catalogue list item details when no error is given", async () => {
    // given a catalogue data item
    const catalogue = {
        "repository": {
            "owner": "pburls",
            "name": "specs-test",
            "htmlUrl": "https://github.com/pburls/specs-test",
            "nameWithOwner": "pburls/specs-test"
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
                    "repo": "pburls/specs-test2",
                    "file-path": "specs/example-spec.yaml"
                }
            ]
        },
        "error": null
    };

    // when catalogue list item component renders
    const { getByTestId, getByText } = renderWithRouter(<CatalogueListItem catalogue={catalogue} />);

    // then a catalogue list item details item is found
    expect(getByTestId('catalogue-list-item-details-item')).toBeInTheDocument();

    // and the name of the catalogue is shown
    expect(getByText("Test Catalogue 1")).toBeInTheDocument();

    // and the name of the repository is shown
    expect(getByText("pburls/specs-test")).toBeInTheDocument();
  });

  
  test("renders catalogue list item error item when an error are given", async () => {
    // given a catalogue data item
    const catalogue = {
        "repository": {
            "owner": "pburls",
            "name": "specs-test",
            "htmlUrl": "https://github.com/pburls/specs-test",
            "nameWithOwner": "pburls/specs-test"
        },
        "catalogueManifest": null,
        "error": "An error occurred while parsing the catalogue manifest yaml file. The following field is missing: bla bla bla"
    };

    // when catalogue list item component renders
    const { getByTestId, getByText } = renderWithRouter(<CatalogueListItem catalogue={catalogue} />);

    // then a catalogue list item details item is found
    expect(getByTestId('catalogue-list-item-error-item')).toBeInTheDocument();

    // and the name of the repository is shown
    expect(getByText("pburls/specs-test")).toBeInTheDocument();

    // and the error message is shown
    expect(getByText("An error occurred while parsing the catalogue manifest yaml file. The following field is missing: bla bla bla")).toBeInTheDocument();
  });
});
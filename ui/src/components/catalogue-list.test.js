import React from "react";
import '@testing-library/jest-dom/extend-expect';
import CatalogueList from "./catalogue-list";
import CatalogueListItem from "./catalogue-list-item";
import axiosMock from 'axios'
import { renderWithRouter } from '../common/test-utils';

jest.mock('axios');

// mock out the actual list items
jest.mock('./catalogue-list-item', () =>  jest.fn(() => null));

describe("CatalogueList component", () => {
  test("successful fetch displays catalogue items", async () => {
    // given a mocked successful catalogues response 
    const cataloguesResponse = { 
        data: {
          catalogues: [{
            repository: {
              nameWithOwner: "test-owner/repo1",
              htmlUrl: "http://github.com/test-owner/repo1",
              repo_image_url: "",
            },
            catalogueManifest: {
              name: "Test Catalogue 1",
              description: "A test catalogue of interface specifications for a systems",
              "spec-files": [],
              errors: ""
            },
          }, {
            repository: {
              nameWithOwner: "test-owner/repo2",
              htmlUrl: "http://github.com/test-owner/repo2",
              repo_image_url: "",
            },
            catalogueManifest: {
              name: "Test Catalogue 2",
              description: "A test catalogue of interface specifications for a department and all its systems",
              "spec-files": [],
              errors: ""
            },
          }]
        }
    };
    
    axiosMock.get.mockResolvedValueOnce(cataloguesResponse);

    // when catalogue list component renders
    const { findByTestId } = renderWithRouter(<CatalogueList org="test-org" />);

    // then a catalogue list item group should be found
    expect(await findByTestId('catalogue-list-item-group')).toBeInTheDocument();
  });

  test("unsuccessful fetch displays error message", async () => {
    // given a mocked error thrown   
    axiosMock.get.mockImplementation(() => {
        throw new Error("test error");
    });

    // when catalogue list component renders
    const { findByText } = renderWithRouter(<CatalogueList />);

    // then it contains an error message
    expect(await findByText("An error occurred while fetching catalogues.")).toBeInTheDocument();
  });

  test("loader is shown before fetch result", async () => {
    // given a mocked catalogues response that is not yet resolved
    const responsePromise = new Promise(() => {});
    axiosMock.get.mockImplementation(() => responsePromise);

    // when catalogue list component renders
    const { getByText, getByTestId } = renderWithRouter(<CatalogueList />);

    // then it contains a loading message
    expect(getByText("Loading")).toBeInTheDocument();

    // and it contains a place holder item
    expect(getByTestId('catalogue-list-placeholder-item-group')).toBeInTheDocument();
  });
});
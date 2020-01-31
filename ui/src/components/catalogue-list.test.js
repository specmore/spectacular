import React from "react";
import { render, fireEvent, waitForElement } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import CatalogueList from "./catalogue-list";
import axiosMock from 'axios'

jest.mock('axios');

describe("CatalogueList component", () => {
  test("successful fetch displays catalogue items", async () => {
    // given a mocked successful catalogues response 
    const cataloguesResponse = { 
        data: {
          catalogues: [{
            repository: {
              name: "test-owner/repo1",
              repo_url: "http://github.com/test-owner/repo1",
              repo_image_url: "",
            },
            catalogueManifest: {
              name: "Test Catalogue 1",
              description: "A test catalogue of interface specifications for a systems",
              spec_files: [],
              errors: []
            },
          }, {
            repository: {
              name: "test-owner/repo2",
              repo_url: "http://github.com/test-owner/repo2",
              repo_image_url: "",
            },
            catalogueManifest: {
              name: "Test Catalogue 2",
              description: "A test catalogue of interface specifications for a department and all its systems",
              spec_files: [],
              errors: []
            },
          }]
        }
    };
    
    axiosMock.get.mockResolvedValueOnce(cataloguesResponse);

    // when catalogue list component renders
    const { findByText } = render(<CatalogueList />);

    // then it contains an item for each catalogue in the response
    expect(await findByText("test-owner/repo1")).toBeInTheDocument();
    expect(await findByText("Test Catalogue 1")).toBeInTheDocument();
    expect(await findByText("test-owner/repo1")).toBeInTheDocument();
    expect(await findByText("Test Catalogue 2")).toBeInTheDocument();
  });

  test("unsuccessful fetch displays error message", async () => {
    // given a mocked error thrown   
    axiosMock.get.mockImplementation(() => {
        throw new Error("test error");
    });

    // when catalogue list component renders
    const { findByText } = render(<CatalogueList />);

    // then it contains an error message
    expect(await findByText("An error occurred while fetching catalogues.")).toBeInTheDocument();
  });

  test("loader is shown before fetch result", async () => {
    // given a mocked catalogues response that is not yet resolved
    const responsePromise = new Promise(() => {});
    axiosMock.get.mockImplementation(() => responsePromise);

    // when catalogue list component renders
    const { getByText } = render(<CatalogueList />);

    // then it contains a loading message
    expect(getByText("Loading")).toBeInTheDocument();
  });
});
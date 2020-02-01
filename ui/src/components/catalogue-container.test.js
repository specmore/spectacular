import React from "react";
import '@testing-library/jest-dom/extend-expect';
import CatalogueContainer from "./catalogue-container";
import axiosMock from 'axios'
import { renderWithRouter } from '../common/test-utils';

jest.mock('axios');

describe("CatalogueContainer component", () => {
  test("successful fetch displays catalogue", async () => {
    // given a repo for a catalogue
    const owner = "test-owner";
    const repo = "repo1";

    // and a mocked successful catalogue response 
    const catalogueResponse = { 
        data: {
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
        }
    };
    
    axiosMock.get.mockResolvedValueOnce(catalogueResponse);

    // when catalogue container component renders
    const { findByRole } = renderWithRouter(<CatalogueContainer />, `/test-route/${owner}/${repo}`, '/test-route/:owner/:repo');

    // then a catalogue header should be found
    expect(await findByRole("heading")).toBeInTheDocument();

    // and it fetched the catalogue details
    expect(axiosMock.get.mock.calls[0][0]).toBe(`/api/catalogues/${owner}/${repo}`)
  });

  test("unsuccessful fetch displays error message", async () => {
    // given a mocked error thrown   
    axiosMock.get.mockImplementation(() => {
        throw new Error("test error");
    });

    // when catalogue container component renders
    const { findByText } = renderWithRouter(<CatalogueContainer />, '/catalogue/test-owner/repo1', '/:owner/:repo');

    // then it contains an error message
    expect(await findByText("An error occurred while fetching catalogue details.")).toBeInTheDocument();
  });

  test("loader is shown before fetch result", async () => {
    // given a mocked catalogues response that is not yet resolved
    const responsePromise = new Promise(() => {});
    axiosMock.get.mockImplementation(() => responsePromise);

    // when catalogue container component renders
    const { getByText } = renderWithRouter(<CatalogueContainer />, '/catalogue/test-owner/repo1', '/:owner/:repo');

    // then it contains a loading message
    expect(getByText("Loading")).toBeInTheDocument();
  });
});
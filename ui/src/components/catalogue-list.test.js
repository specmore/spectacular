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
            catalogues: [
                {
                    repo: "repo1",
                    name: "name1"
                }, 
                {
                    repo: "repo2",
                    name: "name2"
                }
            ]
        }
    };
    
    axiosMock.get.mockResolvedValueOnce(cataloguesResponse);

    // when catalogue list component renders
    const { findByText } = render(<CatalogueList />);

    // then it contains an item for each catalogue in the response
    expect(await findByText("repo1")).toBeInTheDocument();
    expect(await findByText("name1")).toBeInTheDocument();
    expect(await findByText("repo2")).toBeInTheDocument();
    expect(await findByText("name2")).toBeInTheDocument();
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
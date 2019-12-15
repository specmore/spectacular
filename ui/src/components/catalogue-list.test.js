import React from "react";
import { render, fireEvent, waitForElement } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import CatalogueList from "./catalogue-list";
import axiosMock from 'axios'

jest.mock('axios');

describe("CatalogueList component", () => {
  test("displays catalogue items", async () => {
    // given mocked a catalogues response
    const cataloguesResponse = {
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
    
    axiosMock.get.mockResolvedValueOnce({ data: cataloguesResponse })

    // when catalogue list component renders
    const { findByText } = render(<CatalogueList />);

    // then it contains an item for each catalogue in the response
    expect(await findByText("repo1")).toBeInTheDocument();
    expect(await findByText("name1")).toBeInTheDocument();
    expect(await findByText("repo2")).toBeInTheDocument();
    expect(await findByText("name2")).toBeInTheDocument();
  });
});
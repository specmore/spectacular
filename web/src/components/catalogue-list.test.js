import React from "react";
import '@testing-library/jest-dom/extend-expect';
import CatalogueList from "./catalogue-list";
import CatalogueListItemMock from "./catalogue-list-item";
import axiosMock from 'axios'
import { renderWithRouter } from '../common/test-utils';

jest.mock('axios');

// mock out the actual list items
jest.mock('./catalogue-list-item', () =>  jest.fn(() => null));

describe("CatalogueList component", () => {
  test("successful fetch displays catalogue items", async () => {
    // given a mocked successful catalogues response with 2 catalogues
    const cataloguesResponse = { 
        data: {
          catalogues: [{}, {}]
        }
    };
    
    axiosMock.get.mockResolvedValueOnce(cataloguesResponse);

    // when catalogue list component renders
    const { findByTestId } = renderWithRouter(<CatalogueList org="test-org" />);

    // then a catalogue list item group should be found
    expect(await findByTestId('catalogue-list-item-group')).toBeInTheDocument();

    // and the 2 CatalogueListItem should have been created
    expect(CatalogueListItemMock).toHaveBeenCalledTimes(2);
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
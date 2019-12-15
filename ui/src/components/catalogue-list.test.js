import React from "react";
import { render, fireEvent, waitForElement } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import CatalogueList from "./catalogue-list";

describe("CatalogueList component", () => {
  test("displays catalogue item", () => {
    const { getByText, getByRole } = render(<CatalogueList />);
    expect(getByText("Test Catalogue 1")).toBeInTheDocument();
    expect(getByText("pburls/specs-test")).toBeInTheDocument();
  });
});
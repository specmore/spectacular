import React from "react";
import '@testing-library/jest-dom/extend-expect';
import ProposedChangesList from "./proposed-changes-list";
import { renderWithRouter } from '../common/test-utils';
import ProposedChangeItemMock from './proposed-change-item';

// mock out the actual proposed-change-item
jest.mock('./proposed-change-item', () =>  jest.fn(() => null));

describe("ProposedChangesList component", () => {
    test("shows proposed changes header and items", async () => {        
        // given two proposed changes
        const proposedChanges = [{}, {}];

        // when proposed changes list component renders
        const { getByText } = renderWithRouter(<ProposedChangesList proposedChanges={proposedChanges} />);

        // then a proposed changes heading is shown
        expect(getByText("Open change proposals")).toBeInTheDocument();

        // and 2 proposed change items are shown
        expect(ProposedChangeItemMock).toHaveBeenCalledTimes(2);
    });
});
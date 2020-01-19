import React from "react";
import { render, fireEvent, waitForElement } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import UserMenuBarItem from "./user-menu-bar-item";
import axiosMock from 'axios'

jest.mock('axios');

describe("UserMenuBarItem component", () => {
  test("successful fetch displays user menu", async () => {
    // given a mocked successful user response 
    const userResponse = { 
        data: {
          "login": "pburls",
          "name": "Patrick Burls",
          "avatar_url": "https://avatars2.githubusercontent.com/u/11502284?v=4"
        }
    };
    
    axiosMock.get.mockResolvedValueOnce(userResponse);

    // when user menu bar item component renders
    const { findByText } = render(<UserMenuBarItem />);

    // then it contains an item for each catalogue in the response
    expect(await findByText("Patrick Burls")).toBeInTheDocument();
    expect(await findByText("Signed in as pburls")).toBeInTheDocument();

    // and it contains a sign out item
    expect(await findByText("Sign out")).toBeInTheDocument();
  });

  // test("unsuccessful fetch displays error message", async () => {
  //   // given a mocked error thrown   
  //   axiosMock.get.mockImplementation(() => {
  //       throw new Error("test error");
  //   });

  //   // when user menu bar item component renders
  //   const { findByText } = render(<UserMenuBarItem />);

  //   // then it contains an error message
  //   expect(await findByText("An error occurred while fetching user info.")).toBeInTheDocument();
  // });

  test("loader is shown before fetch result", async () => {
    // given a mocked user response that is not yet resolved
    const responsePromise = new Promise(() => {});
    axiosMock.get.mockImplementation(() => responsePromise);

    // when user menu bar item component renders
    const { queryByText } = render(<UserMenuBarItem />);

    // nothing is rendered
    expect(queryByText("Sign out")).toBeNull();
  });
});
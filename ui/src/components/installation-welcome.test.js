import React from "react";
import { render, } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import InstallationWelcome from "./installation-welcome";
import axiosMock from 'axios'

jest.mock('axios');

describe("InstallationWelcome component", () => {
    test("successful fetch displays installation details", async () => {
      // given a mocked successful installation response 
      const installationResponse = { 
            data: {
                "id": 123456,
                "owner": "test-owner",
                "owner_avatar_url": "https://avatars2.githubusercontent.com/u/123456789?v=4"
            }
      };
      
      axiosMock.get.mockResolvedValueOnce(installationResponse);
  
      // when installation welcome component renders
      const { findByText } = render(<InstallationWelcome />);
  
      // then it contains the welcome message
      expect(await findByText("Welcome to Spectacular")).toBeInTheDocument();

      // and the installation owner name
      expect(await findByText("This installation is for the test-owner GitHub organization.")).toBeInTheDocument();
    });
  
    test("unsuccessful fetch displays error message", async () => {
      // given a mocked error thrown   
      axiosMock.get.mockImplementation(() => {
          throw new Error("test error");
      });
  
      // when installation welcome component renders
      const { findByText } = render(<InstallationWelcome />);
  
      // then it contains an error message
      expect(await findByText("An error occurred while fetching installation details.")).toBeInTheDocument();
    });
  
    test("loader is shown before fetch result", async () => {
      // given a mocked installation response that is not yet resolved
      const responsePromise = new Promise(() => {});
      axiosMock.get.mockImplementation(() => responsePromise);
  
      // when installation welcome component renders
      const { getByText } = render(<InstallationWelcome />);
  
      // then it contains a loading message
      expect(getByText("Loading")).toBeInTheDocument();
    });
});

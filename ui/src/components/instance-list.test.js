import React from "react";
import { render, } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import InstanceList from "./instance-list";
import axiosMock from 'axios'

jest.mock('axios');

describe("InstanceList component", () => {
  test("successful fetch displays instance items", async () => {
    // given a mocked successful instances response 
    const instancesResponse = { 
        data: {
          "instances": [
              {
                  "installationId": "1234",
                  "repository": {
                      "owner": "test-owner",
                      "name": "test-specs-app1",
                      "nameWithOwner": "test-owner/test-specs-app1"
                  },
                  "instanceConfigManifest": {
                      "name" : "Test Instance 1",
                      "catalogues": [
                          {
                              "repo": "test-owner/specs-cat1",
                              "name": "Test Catalogue 1"
                          }
                      ]
                  },
                  "error": null
              },
              {
                  "installationId": "1234",
                  "repository": {
                      "owner": "test-owner",
                      "name": "test-specs-app2",
                      "nameWithOwner": "test-owner/test-specs-app2"
                  },
                  "instanceConfigManifest": {
                      "name" : "Test Instance 2",
                      "catalogues": [
                          {
                              "repo": "test-owner/specs-cat1",
                              "name": "Test Catalogue 2"
                          }
                      ]
                  },
                  "error": null
              }
          ]
      }
    };
    
    axiosMock.get.mockResolvedValueOnce(instancesResponse);

    // when instance list component renders
    const { findByText } = render(<InstanceList />);

    // then it contains an item for each instance in the response
    expect(await findByText("Test Instance 1")).toBeInTheDocument();
    expect(await findByText("test-owner/test-specs-app1")).toBeInTheDocument();
    expect(await findByText("Test Instance 2")).toBeInTheDocument();
    expect(await findByText("test-owner/test-specs-app2")).toBeInTheDocument();
  });

  test("unsuccessful fetch displays error message", async () => {
    // given a mocked error thrown   
    axiosMock.get.mockImplementation(() => {
        throw new Error("test error");
    });

    // when instance list component renders
    const { findByText } = render(<InstanceList />);

    // then it contains an error message
    expect(await findByText("An error occurred while fetching instances.")).toBeInTheDocument();
  });

  test("loader is shown before fetch result", async () => {
    // given a mocked instances response that is not yet resolved
    const responsePromise = new Promise(() => {});
    axiosMock.get.mockImplementation(() => responsePromise);

    // when instance list component renders
    const { getByText } = render(<InstanceList />);

    // then it contains a loading message
    expect(getByText("Loading")).toBeInTheDocument();
  });
});
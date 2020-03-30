import React from "react";
import '@testing-library/jest-dom/extend-expect';
import SpecLogItem from "./spec-log-item";
import { renderWithRouter } from '../common/test-utils';
import { CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE, CreateViewSpecLocation } from '../routes';

describe("SpecLogItem component", () => {
    test("uses selected class if browser location is for given spec item", async () => {
        // given a spec item
        const specItem = { 
            "repository": { 
                "owner": "test-owner", "name": "specs-test", "htmlUrl": "https://github.com/test-owner/specs-test", "nameWithOwner": "test-owner/specs-test"
            }, 
            "filePath": "specs/example-template.yaml", 
            "ref": "master",
            "parseResult": { "openApiSpec": { "title": "An empty API spec", "version": "0.1.0", "operations": [] }, "errors": [] } 
        };

        // and a browser location with the spec item selected
        const specFileLocation = `${specItem.repository.nameWithOwner}/${specItem.ref}/${specItem.filePath}`;
        const location = CreateViewSpecLocation("any-catalogue-owner", "any-catalogue-repo", specFileLocation)

        // when spec log component renders
        const { getByTestId } = renderWithRouter(<SpecLogItem specItem={specItem} />, location, CATALOGUE_CONTAINER_WITH_SPEC_LOCATION_ROUTE);

        // then the tertiary class is set
        expect(getByTestId("spec-log-item-segment")).toHaveClass('selected');
    });
});
import React from "react";
import { Label, Icon, Item } from 'semantic-ui-react'
import { ViewSpecLinkButton } from "../routes";

const SpecFileItem = ({catalogueRepository, specFileLocation}) => {
    const specFileLocationSuffix = specFileLocation.repo ? specFileLocation.repo : catalogueRepository.nameWithOwner;
    const specFileFullLocation = `${specFileLocationSuffix}/${specFileLocation["file-path"]}`;
    const selectButton = (<ViewSpecLinkButton catalogueRepository={catalogueRepository} specFileLocation={specFileFullLocation} />);

    return (
        <Item data-testid='specification-file-item'>
            <Item.Content>
                <Item.Header>{specFileFullLocation}</Item.Header>
                {/* <Item.Description>
                    {catalogueManifest.description}
                </Item.Description> */}
                <Item.Extra>
                    {selectButton}
                    {/* {specFileLocation.repo && (<Label data-testid='specification-file-item-repo-label'><Icon name='github' />{specFileLocation.repo}</Label>)} */}
                </Item.Extra>
            </Item.Content>
        </Item>
    );
};

export default SpecFileItem;
import React from "react";
import { Label, Icon, Header } from 'semantic-ui-react'
import { ViewSpecLinkButton } from "../routes";

const LatestAgreedVersion = ({latestAgreedSpecItem}) => {
    const specFileFullLocation = `${latestAgreedSpecItem.repository.nameWithOwner}/${latestAgreedSpecItem.filePath}`;
    const selectButton = (<ViewSpecLinkButton specFileLocation={specFileFullLocation} />);

    return (
        <div data-testid='latest-agreed-version'>
            <Header as='h5'>Latest agreed version</Header>
            <Label color='olive' as='a' href={latestAgreedSpecItem.htmlUrl} target='_blank'>
                <Icon name='code branch' />{latestAgreedSpecItem.ref}
            </Label>
            <Label circular>{latestAgreedSpecItem.parseResult.openApiSpec.version}</Label>
            {selectButton}
        </div>
    );
};

export default LatestAgreedVersion;
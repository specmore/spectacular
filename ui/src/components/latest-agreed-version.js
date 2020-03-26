import React from "react";
import { Label, Icon, Header, Segment } from 'semantic-ui-react'
import { ViewSpecLinkButton } from "../routes";

const LatestAgreedVersion = ({latestAgreedSpecItem}) => {
    const specFileFullLocation = `${latestAgreedSpecItem.repository.nameWithOwner}/${latestAgreedSpecItem.filePath}`;
    const selectButton = (<ViewSpecLinkButton specFileLocation={specFileFullLocation} />);

    return (
        <React.Fragment>
            <Header as='h5' attached='top'>Latest agreed version</Header>
            <Segment attached data-testid='latest-agreed-version'>
                <Label color='olive' as='a' href={latestAgreedSpecItem.htmlUrl} target='_blank'>
                    <Icon name='code branch' />{latestAgreedSpecItem.ref}
                </Label>
                <Label circular>{latestAgreedSpecItem.parseResult.openApiSpec.version}</Label>
                {selectButton}
            </Segment>
        </React.Fragment>
    );
};

export default LatestAgreedVersion;
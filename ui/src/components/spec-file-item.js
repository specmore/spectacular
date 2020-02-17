import React from "react";
import { Label, List, Icon, Item, Message } from 'semantic-ui-react'
import { ViewSpecLinkButton } from "../routes";

const SpecFileItemError = ({specFileFullLocation, errors}) => (
    <Item data-testid='specification-file-item-error'>
            <Item.Content>
                <Item.Description>
                    <Message icon negative>
                        <Icon name='warning sign' />
                        <Message.Content>
                            <Message.Header>The following errors occurred while processing the specification file '{specFileFullLocation}':</Message.Header>
                            <List bulleted>
                                {errors.map((error, index) => (<List.Item key={index}>{error}</List.Item>))}
                            </List>
                        </Message.Content>
                    </Message>
                </Item.Description>
            </Item.Content>
        </Item>
);

const SpecFileItem = ({catalogueRepository, specItem}) => {
    const specFileFullLocation = `${specItem.repository.nameWithOwner}/${specItem.filePath}`;
    if (specItem.parseResult.errors.length > 0) return (<SpecFileItemError specFileFullLocation={specFileFullLocation} errors={specItem.parseResult.errors} />);

    const selectButton = (<ViewSpecLinkButton catalogueRepository={catalogueRepository} specFileLocation={specFileFullLocation} />);

    return (
        <Item data-testid='specification-file-item'>
            <Item.Content>
                <Item.Header>{specItem.parseResult.openApiSpec.title}</Item.Header>
                <Item.Description>
                    <Label>{specFileFullLocation}</Label>
                    <Label>{specItem.parseResult.openApiSpec.version}</Label>
                </Item.Description>
                <Item.Extra>
                    {selectButton}
                    {/* {specFileLocation.repo && (<Label data-testid='specification-file-item-repo-label'><Icon name='github' />{specFileLocation.repo}</Label>)} */}
                </Item.Extra>
            </Item.Content>
        </Item>
    );
};

export default SpecFileItem;
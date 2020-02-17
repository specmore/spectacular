import React from "react";
import { Label, List, Icon, Item, Message } from 'semantic-ui-react'
import { ViewSpecLinkButton } from "../routes";

const SpecFileItemError = ({specFileFullLocation, errors}) => (
    <Item data-testid='specification-file-item-error'>
            <Item.Content>
                <Item.Header>{specFileFullLocation}</Item.Header>
                <Item.Description>
                    <Message icon negative>
                        <Icon name='warning sign' />
                        <Message.Content>
                            <Message.Header>The following errors occurred while processing the specification file.</Message.Header>
                            <List bulleted>
                                {errors.map(error => (<List.Item>{error}</List.Item>))}
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
                <Item.Header>{specFileFullLocation}</Item.Header>
                <Item.Description>
                    <Label>{specItem.parseResult.openApiSpec.title}</Label>
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
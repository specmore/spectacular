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
                <Item.Header>
                    <span style={{paddingRight: '1em'}}>{specItem.parseResult.openApiSpec.title}</span>
                    <Label circular color='grey'>{specItem.parseResult.openApiSpec.version}</Label>
                </Item.Header>
                <Item.Description>
                    <Label><Icon name='github' />{specFileFullLocation}</Label>
                    {selectButton}
                </Item.Description>
            </Item.Content>
        </Item>
    );
};

export default SpecFileItem;
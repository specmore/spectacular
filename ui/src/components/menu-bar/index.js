import React from "react";
import { Menu, Icon } from 'semantic-ui-react';

const MenuBar = () => (
    <Menu fixed="top" inverted>
        <Menu.Item header><Icon inverted name="sitemap"/>Spectacular</Menu.Item>
    </Menu>
);

export default MenuBar;
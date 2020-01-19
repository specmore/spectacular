import React from "react";
import { Menu, Icon } from 'semantic-ui-react';
import UserMenuBarItem from '../user-menu-bar-item'

const MenuBar = () => (
    <Menu fixed="top" inverted>
        <Menu.Item header><Icon inverted name="sitemap"/>Spectacular</Menu.Item>
        <Menu.Menu position='right'><UserMenuBarItem/></Menu.Menu>
    </Menu>
);

export default MenuBar;
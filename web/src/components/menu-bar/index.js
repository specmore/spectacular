import React from 'react';
import { Menu, Icon } from 'semantic-ui-react';
import { Link } from 'react-router-dom';
import UserMenuBarItem from '../user-menu-bar-item';

const MenuBar = () => (
  <Menu fixed="top" inverted>
    <Menu.Item header as={Link} to="/">
      <Icon inverted name="sitemap" />
      Spectacular
    </Menu.Item>
    <Menu.Menu position="right"><UserMenuBarItem /></Menu.Menu>
  </Menu>
);

export default MenuBar;

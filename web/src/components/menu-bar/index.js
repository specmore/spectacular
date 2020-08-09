import React from 'react';
import { Menu, Icon } from 'semantic-ui-react';
import { Link } from 'react-router-dom';
import UserMenuBarItem from '../user-menu-bar-item';
import SpectacularLogo from '../../assets/images/spectacular-logo.svg';

const MenuBar = () => (
  <Menu fixed="top" inverted>
    <Menu.Item header as={Link} to="/">
      <img src={SpectacularLogo} alt="spectacular logo" style={{ width: '96px' }} />
    </Menu.Item>
    <Menu.Menu position="right"><UserMenuBarItem /></Menu.Menu>
  </Menu>
);

export default MenuBar;

import React from 'react';
import { Button, Icon, Label } from 'semantic-ui-react';
import './footer.less';

const FooterBar = () => (
  <footer className="footer-container">
    <div>Spectacular</div>
    <div>
      v
      {VERSION}
    </div>
    <div>
      <a href="https://github.com/specmore/spectacular" target="_blank" rel="noopener noreferrer">
        <Icon name="github" link />
      </a>
    </div>
  </footer>
);

export default FooterBar;

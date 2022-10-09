import React, { FunctionComponent } from 'react';
import { Icon } from 'semantic-ui-react';
import './footer.less';

declare const VERSION: string;

const FooterBar: FunctionComponent = () => (
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

import React from 'react';
import { Segment, Icon, Label } from 'semantic-ui-react';

const FooterBar = () => (
  <Segment textAlign="center">
    <p>
      Contribute to the
      <a href="https://github.com/pburls/spectacular" target="_blank">
        <Icon name="github" />
        GitHub project
      </a>
    </p>
    <Label size="tiny">
      Version
      <Label.Detail>{VERSION}</Label.Detail>
    </Label>
    <Label size="tiny">
      SHA
      <Label.Detail>{SHORTSHA}</Label.Detail>
    </Label>
  </Segment>
);

export default FooterBar;

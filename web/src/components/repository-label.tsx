import React, { FunctionComponent } from 'react';
import {
  Icon, Label,
} from 'semantic-ui-react';
import { Repository } from '../api-client/models';

interface RepositoryLabelProps {
  repository: Repository;
}

const RepositoryLabel: FunctionComponent<RepositoryLabelProps> = ({ repository }) => (
  <Label as="a" href={repository.htmlUrl} target="_blank">
    <Icon name="github" />
    {repository.nameWithOwner}
  </Label>
);

export default RepositoryLabel;

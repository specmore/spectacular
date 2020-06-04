import React, { FunctionComponent } from 'react';
import SpecRevision from './spec-revision';
import { SpecItem } from '../__generated__/backend-api-client';

interface LatestAgreedVersionProps {
  latestAgreedSpecItem: SpecItem;
}

const LatestAgreedVersion: FunctionComponent<LatestAgreedVersionProps> = ({ latestAgreedSpecItem }) => (
  <>
    <SpecRevision specItem={latestAgreedSpecItem} />
  </>
);

export default LatestAgreedVersion;

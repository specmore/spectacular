import React, { FunctionComponent } from 'react';
import './location-bar.less';

interface LocationBarProps {
  installationOwner: string;
}

const LocationBar: FunctionComponent<LocationBarProps> = ({ installationOwner }) => (
  <div className="location-bar">
    {installationOwner}
    {' '}
    Interface Specifications
  </div>
);

export default LocationBar;

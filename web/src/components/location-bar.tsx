import React, { FunctionComponent } from 'react';
import './location-bar.less';
import { Catalogue } from '../backend-api-client';

interface LocationBarProps {
  installationOwner: string;
  catalogue?: Catalogue;
}

const LocationBar: FunctionComponent<LocationBarProps> = ({ installationOwner, catalogue }) => {
  const additionalLinks = [];
  if (catalogue) {
    additionalLinks.push(catalogue.title);
  }

  return (
    <div className="location-bar">
      {installationOwner}
      {' > '}
      Interface Catalogues
      { additionalLinks.map((link) => (
        <>
          {' > '}
          { link }
        </>
      )) }
    </div>
  );
};

export default LocationBar;

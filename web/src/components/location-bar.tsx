import React, { FunctionComponent } from 'react';
import './location-bar.less';
import { Link } from 'react-router-dom';
import { CATALOGUE_LIST_ROUTE } from '../routes';

interface LocationBarProps {
  installationOwner: string;
  catalogueTitle?: string;
}

const LocationBar: FunctionComponent<LocationBarProps> = ({ installationOwner, catalogueTitle }) => {
  const breadCrumbs = [];
  if (!catalogueTitle) {
    breadCrumbs.push(<span>Interface Catalogues</span>);
  } else {
    breadCrumbs.push(<Link to={CATALOGUE_LIST_ROUTE}>Interface Catalogues</Link>);
    breadCrumbs.push(<span>{catalogueTitle}</span>);
  }

  return (
    <div className="location-bar">
      {installationOwner}
      { breadCrumbs.map((link) => (
        <>
          {' > '}
          { link }
        </>
      )) }
    </div>
  );
};

export default LocationBar;

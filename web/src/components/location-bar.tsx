import React, { FunctionComponent } from 'react';
import './location-bar.less';
import { Link } from 'react-router-dom';
import { CATALOGUE_LIST_ROUTE, CreateCatalogueContainerLocation } from '../routes';

interface LocationBarProps {
  installationOwner: string;
  catalogueTitle?: string;
  catalogueEncodedId?: string;
  interfaceTitle?: string;
}

const LocationBar: FunctionComponent<LocationBarProps> = ({
  installationOwner,
  catalogueTitle,
  catalogueEncodedId,
  interfaceTitle,
}) => {
  const breadcrumbList = [];
  if (!catalogueTitle) {
    breadcrumbList.push({
      id: 'interface-catalogues-text',
      element: (<span>Interface Catalogues</span>),
    });
  } else {
    breadcrumbList.push({
      id: 'interface-catalogues-link',
      element: (<Link to={CATALOGUE_LIST_ROUTE}>Interface Catalogues</Link>),
    });

    if (!catalogueEncodedId) {
      breadcrumbList.push({
        id: 'catalogue-title-text',
        element: (<span>{catalogueTitle}</span>),
      });
    } else {
      const catalogueLocation = CreateCatalogueContainerLocation(catalogueEncodedId);
      breadcrumbList.push({
        id: 'catalogue-title-text',
        element: (<Link to={catalogueLocation}>{catalogueTitle}</Link>),
      });

      if (interfaceTitle) {
        breadcrumbList.push({
          id: 'interface-title-text',
          element: (<span>{interfaceTitle}</span>),
        });
      }
    }
  }

  return (
    <div className="location-bar">
      {installationOwner}
      { breadcrumbList.map((breadcrumb) => (
        <React.Fragment key={breadcrumb.id}>
          {' > '}
          { breadcrumb.element }
        </React.Fragment>
      )) }
    </div>
  );
};

export default LocationBar;

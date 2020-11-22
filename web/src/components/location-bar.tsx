import React, { FunctionComponent } from 'react';
import './location-bar.less';
import { Link } from 'react-router-dom';
import { CATALOGUE_LIST_ROUTE, CreateCatalogueContainerLocation } from '../routes';
import { Catalogue } from '../backend-api-client';

interface LocationBarProps {
  installationOwner: string;
  catalogue?: Catalogue;
  interfaceTitle?: string;
}

const LocationBar: FunctionComponent<LocationBarProps> = ({
  installationOwner,
  catalogue,
  interfaceTitle,
}) => {
  const breadcrumbList = [];
  if (!catalogue) {
    breadcrumbList.push({
      id: 'interface-catalogues-text',
      element: (<span>Interface Catalogues</span>),
    });
  } else {
    breadcrumbList.push({
      id: 'interface-catalogues-link',
      element: (<Link to={CATALOGUE_LIST_ROUTE}>Interface Catalogues</Link>),
    });

    const catalogueLocation = CreateCatalogueContainerLocation(catalogue.encodedId);
    breadcrumbList.push({
      id: 'catalogue-title-text',
      element: (<Link to={catalogueLocation}>{catalogue.title}</Link>),
    });

    if (interfaceTitle) {
      breadcrumbList.push({
        id: 'interface-title-text',
        element: (<span>{interfaceTitle}</span>),
      });
    }
  }

  return (
    <div className="location-bar" data-testid="location-bar">
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

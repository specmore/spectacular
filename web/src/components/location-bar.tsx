import React, { FunctionComponent } from 'react';
import './location-bar.less';
import { Link, useParams } from 'react-router-dom';
import { CATALOGUE_LIST_ROUTE, CreateCatalogueContainerLocation, CreateInterfaceLocation } from '../routes';
import { Catalogue } from '../backend-api-client';

interface LocationBarProps {
  installationOwner: string;
  catalogue?: Catalogue;
}

const LocationBar: FunctionComponent<LocationBarProps> = ({
  installationOwner,
  catalogue,
}) => {
  const { interfaceName } = useParams();

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

    const specLog = catalogue.specLogs.find((specLogItem) => specLogItem.interfaceName === interfaceName);
    if (specLog) {
      const interfaceTitle = specLog.latestAgreed.parseResult.openApiSpec.title;
      const interfaceLocation = CreateInterfaceLocation(catalogue.encodedId, interfaceName);
      breadcrumbList.push({
        id: 'interface-title-text',
        element: (<Link to={interfaceLocation}>{interfaceTitle}</Link>),
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

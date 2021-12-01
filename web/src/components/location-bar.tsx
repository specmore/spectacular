import React, { FunctionComponent } from 'react';
import './location-bar.less';
import { Link } from 'react-router-dom';
import { CreateInstallationContainerLocation, CreateCatalogueContainerLocation, CreateInterfaceLocation } from '../routes';
import { Catalogue, SpecEvolutionSummary } from '../backend-api-client';

interface LocationBarProps {
  installationId: number;
  installationOwner: string;
  catalogue?: Catalogue;
  specEvolutionSummary?: SpecEvolutionSummary;
}

const LocationBar: FunctionComponent<LocationBarProps> = ({
  installationId,
  installationOwner,
  catalogue,
  specEvolutionSummary,
}) => {
  const breadcrumbList = [];
  if (!catalogue) {
    breadcrumbList.push({
      id: 'interface-catalogues-text',
      element: (<span>Interface Catalogues</span>),
    });
  } else {
    const installationLocation = CreateInstallationContainerLocation(installationId);
    breadcrumbList.push({
      id: 'interface-catalogues-link',
      element: (<Link to={installationLocation}>Interface Catalogues</Link>),
    });

    const catalogueLocation = CreateCatalogueContainerLocation(installationId, catalogue.encodedId);
    breadcrumbList.push({
      id: 'catalogue-title-text',
      element: (<Link to={catalogueLocation}>{catalogue.title}</Link>),
    });

    if (specEvolutionSummary) {
      const interfaceTitle = specEvolutionSummary.latestAgreed.parseResult.openApiSpec.title;
      const interfaceLocation = CreateInterfaceLocation(installationId, catalogue.encodedId, specEvolutionSummary.interfaceName);
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

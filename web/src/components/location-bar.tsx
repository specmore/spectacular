import React, { FunctionComponent } from 'react';
import './location-bar.less';
import { Link, useParams } from 'react-router-dom';
import { CATALOGUE_LIST_ROUTE, CreateCatalogueContainerLocation, CreateInterfaceLocation } from '../routes';
import { Catalogue, SpecEvolutionSummary } from '../backend-api-client';

interface LocationBarProps {
  installationOwner: string;
  catalogue?: Catalogue;
  specEvolutionSummary?: SpecEvolutionSummary;
}

const LocationBar: FunctionComponent<LocationBarProps> = ({
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
    breadcrumbList.push({
      id: 'interface-catalogues-link',
      element: (<Link to={CATALOGUE_LIST_ROUTE}>Interface Catalogues</Link>),
    });

    const catalogueLocation = CreateCatalogueContainerLocation(catalogue.encodedId);
    breadcrumbList.push({
      id: 'catalogue-title-text',
      element: (<Link to={catalogueLocation}>{catalogue.title}</Link>),
    });

    if (specEvolutionSummary) {
      const interfaceTitle = specEvolutionSummary.latestAgreed.parseResult.openApiSpec.title;
      const interfaceLocation = CreateInterfaceLocation(catalogue.encodedId, specEvolutionSummary.interfaceName);
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

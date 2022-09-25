import React from 'react';
import { MemoryRouter, Route } from 'react-router-dom';
import { QueryParamProvider } from 'use-query-params';
import { render } from '@testing-library/react';

export const renderWithRouter = (ui, location = '/', routePath = '/') => {
  function Wrapper({ children }) {
    return (
      <MemoryRouter initialEntries={[location]}>
        <Route path={routePath}>
          <QueryParamProvider ReactRouterRoute={Route}>
            {children}
          </QueryParamProvider>
        </Route>
      </MemoryRouter>
    );
  }
  return {
    ...render(ui, { wrapper: Wrapper }),
  };
};

export default renderWithRouter;


import React from 'react';
import { MemoryRouter, Route } from 'react-router-dom';
import { render } from '@testing-library/react';

export const renderWithRouter = (ui, location = '/', routePath = '/') => {
  const Wrapper = ({ children }) => (
    <MemoryRouter initialEntries={[location]}>
      <Route path={routePath}>
        {children}
      </Route>
    </MemoryRouter>
  );
  return {
    ...render(ui, { wrapper: Wrapper }),
  };
};

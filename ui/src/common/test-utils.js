
import React from "react";
import { MemoryRouter } from 'react-router-dom';
import { render } from '@testing-library/react';

export const renderWithRouter = (ui, route = '/') => {
    const Wrapper = ({ children }) => (
        <MemoryRouter initialEntries={[route]}>{children}</MemoryRouter>
    );
    return {
        ...render(ui, { wrapper: Wrapper }),
    };
};
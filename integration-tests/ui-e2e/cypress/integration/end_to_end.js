/// <reference types="Cypress" />

describe('End to End test without login', function() {
    beforeEach(function() {
        // given a valid logged in user
        cy.setCookie('jwt_token', 'eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwYnVybHMiLCJwaWN0dXJlIjoiaHR0cHM6Ly9hdmF0YXJzMi5naXRodWJ1c2VyY29udGVudC5jb20vdS8xMTUwMjI4ND92PTQiLCJuYW1lIjoiUGF0cmljayBCdXJscyIsIm9yaWdpbiI6ImdpdGh1YiIsImV4cCI6MTY3NTY3NjE1OTl9.Y1m5NpBOBeW239wpnxliBLYRFqVonfDFNYvJxXP19SKCUny_PkMAytFjFcYrVWIA0N01-ndnD0YhHxMCTkMM_A')

        // when visiting the home page
        cy.visit('/')
    });

    it('Shows menu bar with logged in user name', function() {
        // then the user menu item is shown with the user's display name
        cy.get('[data-testid=user-menu-bar-item] .text').should('have.text', 'Patrick Burls')

        // when the user menu item is clicked
        cy.get('[data-testid=user-menu-bar-item]').click()

        // then the signed in as dropdown menu item is shown with the user's github username
        cy.get('[data-testid=user-menu-bar-item] .item').first().should('have.text', 'Signed in as pburls')
    })

    it('Shows the welcome message, loads catalogues list, selects a catalogue and views a latest agreed spec', function() {
        // then the welcome message should be shown
        cy.get('[data-testid=installation-welcome] > .center').should('be.visible')

        // then the catalogue list should be shown with 2 children catalogue items
        cy.get('[data-testid=catalogue-list-item-group]').children().should('have.length', 2)
        
        // and an error catalogue item is shown for a catalogue manifest with a parsing error
        cy.get('[data-testid=catalogue-list-item-error-item]').should('be.visible')

        // and a normal catalogues list item is shown for a valid catalogue manifest
        cy.get('[data-testid=catalogue-list-item-details-item]').should('be.visible')

        // when selecting a catalogue
        cy.get('[data-testid=view-catalogue-button]').click()

        // then a catalogue page is shown with details block
        cy.get('[data-testid=catalogue-details-container]').should('be.visible')
        cy.get('[data-testid=catalogue-details-segment]').should('be.visible')

        // and an interfaces list should be shown with 4 items
        cy.get('[data-testid=catalogue-details-interface-list]').should('be.visible')

        // and 2 interface items are shown as spec log error messages for invalid spec files
        cy.get('[data-testid=spec-log-error]').should('have.length', 2)

        // and 2 interface items are shown as spec log items for valid spec files
        cy.get('[data-testid=spec-log-container]').should('have.length', 2)

        // and the first spec log should have a latest agreed version and 2 proposed changes
        cy.get('[data-testid=spec-log-container]').first().as('spec-log1')
        cy.get('@spec-log1').find('[data-testid=spec-log-item-segment-latest-agreed-version]').should('have.length', 1)
        cy.get('@spec-log1').find('[data-testid=spec-log-item-segment-proposed-change-item]').should('have.length', 2)

        // when selecting the first spec log's latest agreed version
        cy.get('@spec-log1').find('[data-testid=spec-log-item-segment-latest-agreed-version] [data-testid=view-spec-button]').click()

        // then the swagger ui element is shown
        cy.get('[data-testid=catalogue-container-swagger-ui]').should('be.visible')

        // when the swagger ui close button is clicked
        cy.get('[data-testid=close-spec-button]').click()

        // then the swagger ui element is removed
        cy.get('[data-testid=catalogue-container-swagger-ui]').should('not.exist')

        // when the back to catalogue list button is clicked
        cy.get('[data-testid=back-to-catalogue-list-button]').click()

        // then the catalogue page is removed and the catalogue list is shown again
        cy.get('[data-testid=catalogue-details-container]').should('not.exist')
        cy.get('[data-testid=catalogue-list-item-group]').should('be.visible')
    })
})
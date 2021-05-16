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

    it('Journey to view an interface', function() {
        // then Interface Catalogues location should be shown
        cy.get('[data-testid=location-bar]').contains('specmore > Interface Catalogues')

        // then the catalogue list should be shown with 3 children catalogue items
        cy.get('[data-testid=catalogue-list-item-group]').children().should('have.length', 3)
        
        // and an error catalogue item is shown for a catalogue manifest with a parsing error
        cy.get('[data-testid=catalogue-list-item-error-item]').should('have.length', 1)

        // and 2 normal catalogues list item should be shown for the valid catalogue manifests
        cy.get('[data-testid=catalogue-list-item-details-item]').should('have.length', 2)

        // when selecting Test Catalogue 1 item heading
        cy.contains('Test Catalogue 1').click()

        // then Test Catalogue 1 location should be shown
        cy.get('[data-testid=location-bar]').contains('specmore > Interface Catalogues > Test Catalogue 1')

        // then a catalogue page is shown with details block
        cy.get('[data-testid=catalogue-details-container]').should('be.visible')

        // and an interfaces list should be shown
        cy.get('[data-testid=catalogue-details-interface-list]').should('be.visible')

        // and 1 interface items are shown as interface error messages for invalid spec files
        cy.get('[data-testid=spec-log-error]').should('have.length', 1)

        // and 4 interface items are shown as interface items for valid spec files
        cy.get('[data-testid=interface-list-item-container]').should('have.length', 4)

        // when selecting the last interface item
        cy.get('[data-testid=catalogue-details-interface-list] > :nth-child(5) > .content > .header').click()

        // then Interface location should be shown
        cy.get('[data-testid=location-bar]').contains('specmore > Interface Catalogues > Test Catalogue 1 > An empty API spec')

        // then Interface details should be shown
        cy.get('[data-testid=interface-details-container]').should('be.visible')

        // when the view spec button on the latest agreed version is click
        cy.get('[data-testid=view-spec-button]').click()

        // then latest agree version swagger ui preview is shown
        cy.get('[data-testid=interface-container-swagger-ui]').should('be.visible')

        // when the swagger ui close button is clicked
        cy.get('[data-testid=close-spec-button]').click()

        // then the swagger ui element is removed
        cy.get('[data-testid=interface-container-swagger-ui]').should('not.exist')

        // when the view changed button is click
        cy.get('[data-testid=view-spec-evolution-button]').click()

        // then spec evolution details are shown
        cy.get('[data-testid=spec-evolution-container]').should('be.visible')

        // and 7 spec log entries and a previous versions label entry are shown
        cy.get('[data-testid=log-entry-container]').should('have.length', 8)

        // when the show previous versions toggle button is clicked
        cy.get('[data-testid=show-spec-evolution-previous-versions-toggle-button]').click()

        // then 10 spec log entries and a previous versions label entry are now shown
        cy.get('[data-testid=log-entry-container]').should('have.length', 11)

        // when the show previous versions toggle button is clicked again
        cy.get('[data-testid=show-spec-evolution-previous-versions-toggle-button]').click()

        // then only 7 spec log entries and a previous versions label entry are shown again
        cy.get('[data-testid=log-entry-container]').should('have.length', 8)

        // when the view spec button on the first PR is clicked
        cy.get('[data-testid=spec-evolution-log-container] > :nth-child(1) > :nth-child(1) [data-testid=view-spec-button]').click()

        // then latest agree version swagger ui preview is shown
        cy.get('[data-testid=interface-container-swagger-ui]').should('be.visible')

        // when the swagger ui close button is clicked
        cy.get('[data-testid=close-spec-button]').click()

        // then the swagger ui element is removed
        cy.get('[data-testid=interface-container-swagger-ui]').should('not.exist')

        // when the spec evolution close button is clicked
        cy.get('[data-testid=close-spec-evolution-button]').click()

        // then the spec evolution details are removed
        cy.get('[data-testid=spec-evolution-container]').should('not.exist')
    })

    it('Filtering the catalogue list by topic', function() {
        // then Interface Catalogues topic selection list should be shown
        cy.get('[data-testid=topic-selection-list-container]')

        // and there are 3 topic list items
        cy.get('[data-testid=topic-selection-list-container] > .list > .item').should('have.length', 3)

        // and the catalogue list should show 3 children catalogue items
        cy.get('[data-testid=catalogue-list-item-group]').children().should('have.length', 3)

        // when selecting domain-x topic item is checked
        cy.get('[data-testid=topic-selection-list-container]').contains('domain-x').click()

        // then the catalogue list should just show the one catalogue item
        cy.get('[data-testid=catalogue-list-item-group]').children().should('have.length', 1)
    })
})
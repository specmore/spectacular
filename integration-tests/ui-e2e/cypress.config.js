module.exports = {
  defaultCommandTimeout: 15000,
  reporter: 'junit',
  reporterOptions: {
    mochaFile: 'cypress/results/tests.xml',
    testsuitesTitle: 'cypress e2e tests',
  },
  e2e: {
    // We've imported your old cypress plugins here.
    // You may want to clean this up later by importing these.
    setupNodeEvents(on, config) {
      return require('./cypress/plugins/index.js')(on, config)
    },
    baseUrl: 'http://localhost:8080',
  },
}

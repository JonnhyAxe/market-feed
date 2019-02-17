// For authoring Nightwatch tests, see
// http://nightwatchjs.org/guide#usage

module.exports = {
  'default e2e tests': function (browser) {
    // automatically uses dev Server port from /config.index.js
    // default: http://localhost:8080
    // see nightwatch.conf.js
    const devServer = browser.globals.devServerURL

    browser
      .url(devServer)
      .waitForElementVisible('#app', 5000)
      .assert.elementPresent('.table')
      .waitForElementVisible('thead', 5000)
      .assert.elementPresent('thead')
      .assert.containsText('thead tr:nth-child(1) > th:nth-child(1)', 'Artist')
      .assert.containsText('thead tr:nth-child(1) > th:nth-child(2)', 'Name')
      .assert.containsText('thead tr:nth-child(1) > th:nth-child(3)', 'Show Details')
      // body content
      .assert.containsText('tbody tr:nth-child(1) > td:nth-child(1)', 'Latmun')
      .assert.containsText('tbody tr:nth-child(1) > td:nth-child(2)', 'Please Stop (Original Mix)')
      .assert.containsText('tbody tr:nth-child(1) > td:nth-child(3)', 'Show Details')

      .assert.containsText('tbody tr:nth-child(2) > td:nth-child(1)', 'Latmun')
      .assert.containsText('tbody tr:nth-child(2) > td:nth-child(2)', 'def (Original Mix)')
      .assert.containsText('tbody tr:nth-child(2) > td:nth-child(3)', 'Show Details')

      .assert.containsText('tbody tr:nth-child(3) > td:nth-child(1)', 'LatmunXPTO')
      .assert.containsText('tbody tr:nth-child(3) > td:nth-child(2)', 'Please Stop (Original Mix)XPTO')
      .assert.containsText('tbody tr:nth-child(3) > td:nth-child(3)', 'Show Details')

      // TODO : why does not work (BDD style DSL)
      // expect element  to be present in 1000ms
      // .expect.element('#app').to.be.present.before(10000)
      // expect element <#lst-ib> to have css property 'display'
      // .expect.element('#app').to.be.visible
      // .expect.element('#app').to.have.css('margin-left: 50px; margin-right: 50px')

      // .expect.element('.table').to.be.present.before(5000)
      // .expect.element('.table').to.be.visible

      // .expect.element('div:nth-child(2)').to.have.attribute('class').which.contains('service')

      // .expect.element('thead tr:nth-child(1) > th:nth-child(1)').which.contains('Artist')
      // .expect.element('thead tr:nth-child(1) > th:nth-child(2)').which.contains('Name')
      // .expect.element('thead tr:nth-child(1) > th:nth-child(3))').which.contains('Show Details')
      .end()
  }
}

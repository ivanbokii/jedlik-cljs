var mocha = require("mocha");
var expect = require("chai").expect;
var Jedlik = require("../target/jedlik");

describe("integration tests", function() {
  it("should produce correct query json", function() {
    var query = new Jedlik()
      .tablename('tablename')
      .hashkey('hashkey', 'hashkeyvalue')
      .rangekey('rangekey', 'rangekeyvalue', 'BEGINS_WITH')
      .attributes(['attribute1', 'attribute2'])
      .query();

      expect(query).to.deep.equal(require('./fixtures/query'));
  });
});
var mocha = require("mocha");
var expect = require("chai").expect;
var Jedlik = require("../target/jedlik");

describe("integration tests", function() {
  it("should produce correct query json", function() {
    var query = new Jedlik()
          .tablename('tablename')
          .hashkey('hash', 'hashkeyvalue')
          .rangekey('range', 'rangekeyvalue', 'BEGINS_WITH')
          .attributes(['attribute1', 'attribute2'])
          .query();

    expect(query).to.deep.equal(require('./fixtures/query'));
  });

  it("should produce correct query json for range key set in between", function() {
    var query = new Jedlik()
          .tablename('tablename')
          .hashkey('hashkey', 'hashkeyvalue')
          .rangekeyBetween('rangekey', 'valueFrom', 'valueTo')
          .query();

    expect(query).to.deep.equal(require('./fixtures/query-rangekey-between'));
  });

  it('should return a valid json for query with select', function() {
    var query = new Jedlik()
          .tablename('tablename')
          .hashkey('hashkey', 'hashkeyvalue')
          .rangekey('rangekey', 'rangekeyvalue', 'BEGINS_WITH')
          .select('COUNT')
          .query();

    expect(query).to.deep.equal(require('./fixtures/query-with-select'));
  });
});

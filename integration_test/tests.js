var mocha = require("mocha");
var expect = require("chai").expect;
var Jedlik = require("../target/jedlik");

describe("integration tests", function() {
  describe("query", function() {
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

    it('should return a valid json for query with select no rangekey', function() {
      var query = new Jedlik()
            .tablename('tablename')
            .hashkey('hashkey', 'hashkeyvalue')
            .select('COUNT')
            .query();

      expect(query).to.deep.equal(require('./fixtures/query-with-select-no-rangekey'));
    });

    it('should return a valid json for query with descending sort', function() {
      var query = new Jedlik()
            .tablename('tablename')
            .hashkey('hashkey', 'hashkeyvalue')
            .rangekey('rangekey', 'rangekeyvalue', 'BEGINS_WITH')
            .attributes(['attribute1', 'attribute2'])
            .ascending(false)
            .query();

      expect(query).to.deep.equal(require('./fixtures/query-with-sort-descending'));
    });

    it('should return a valid json for query with start key', function() {
      var query = new Jedlik()
            .tablename('tablename')
            .hashkey('hashkey', 'hashkeyvalue')
            .rangekey('rangekey', 'rangekeyvalue')
            .starthashkey('starthashkey', 'starthashkeyvalue')
            .startrangekey('startrangekey', 'startrangekeyvalue')
            .attributes(['attribute1', 'attribute2'])
            .query();

      expect(query).to.deep.equal(require('./fixtures/query-with-startkey'));
    });

    it('should return a valid json for query with start key (no range key)', function() {
      var query = new Jedlik()
            .tablename('tablename')
            .hashkey('hashkey', 'hashkeyvalue')
            .starthashkey('starthashkey', 'starthashkeyvalue')
            .attributes(['attribute1', 'attribute2'])
            .query();

      expect(query).to.deep.equal(require('./fixtures/query-with-startkey-hash-only'));
    });
  });

  describe("update", function() {
    it('should return a valid json for update', function() {
      var update = new Jedlik()
            .tablename('tablename')
            .hashkey('hashkey', 'hashkeyvalue')
            .rangekey('rangekey', 'rangekeyvalue')
            .attribute('attribute1', 'STR', 'PUT')
            .attribute('attribute2', '1234')
            .update();

      expect(update).to.deep.equal(require('./fixtures/update'));
    });

    it('should return a valid json for update', function() {
      var update = new Jedlik()
            .tablename('tablename')
            .hashkey('hashkey', 'hashkeyvalue')
            .attribute('attribute1', 'STR', 'PUT')
            .attribute('attribute2', '1234')
            .update();

      expect(update).to.deep.equal(require('./fixtures/update-without-rangekey'));
    });

    it('should return a valid json for update', function() {
      var update = new Jedlik()
            .tablename('tablename')
            .hashkey('hashkey', 'hashkeyvalue')
            .rangekey('rangekey', 'rangekeyvalue')
            .attribute('attribute1', 'STR', 'PUT')
            .attribute('attribute2', '1234')
            .returnvals('ALL_OLD')
            .update();

      expect(update).to.deep.equal(require('./fixtures/update-with-return-values'));
    });
  });
});

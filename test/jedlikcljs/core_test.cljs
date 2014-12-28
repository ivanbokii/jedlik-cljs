(ns jedlikcljs.core-test
  (:require [jedlikcljs.core :as core]
    [latte.chai :refer (expect)]
    [cljs.nodejs :as nodejs])
  (:require-macros [latte.core :refer (describe it)]))

(nodejs/enable-util-print!)

(describe "private api"
  (describe "generate-value-list"
    (it "should generate multiple values for a list with length > 1" []
      (expect (core/generate-value-list ["first" "second"]) :to.equal [{:S "first"} {:S "second"}]))

    (it "should generate multiple values for a list with length = 1" []
      (expect (core/generate-value-list ["first"]) :to.equal [{:S "first"}]))
    )

  (describe "generate-attribute-value-list"
    (it "should produce array of values if comparison operator is BETWEEN" []
      (let [result (core/generate-attribute-value-list {:comparison "BETWEEN" :value-from "from" :value-to "to"})]
        (expect result :to.equal {:ComparisonOperator "BETWEEN" :AttributeValueList [{:S "from"} {:S "to"}]})))

    (it "should produce array of one value if comparison operator is not BETWEEN" []
      (let [result (core/generate-attribute-value-list {:comparison "EQ" :value "value" :key "key" :type "type"})]
        (expect result :to.equal {:ComparisonOperator "EQ" :AttributeValueList [{:S "value"}]})))
    )
  )

(describe "api"
  (it "hashkey should save hashkey params to the _hashkey property" []
    (let [hashkey-params (:_hashkey (core/hashkey "key" "value" "type"))]
      (expect hashkey-params :to.equal {:key "key"
        :value "value"
        :type "type"}))
    )

  (it "rangekey should save rangekey params to the _rangekey property" []
    (let [rangekey-params (:_rangekey (core/rangekey "key" "value" "comparison-operator" "type"))]
      (expect rangekey-params :to.equal {:key "key"
       :value "value"
       :comparison "comparison-operator"
       :type "type"}))
    )

  (it "tablename should save tablename to the _table property" []
    (let [tablename (:_table (core/tablename "tablename"))]
      (expect tablename :to.equal "tablename"))
    )

  (it "attributes should save passed attributes to the _attributes property" []
    (let [attributes (:_attributes (core/attributes ["first" "second" "third"]))]
      (expect attributes :to.equal ["first" "second" "third"]))
    )

  (it "rangekey-between should save passed values to the _rangekey-between property" []
    (let [rangekey-between (:_rangekey-between (core/rangekey-between "key" "value from" "value to"))]
      (expect rangekey-between :to.equal {:key "key" :value-from "value from" :value-to "value to"}))
    )
)



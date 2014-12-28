(ns jedlikcljs.core-test
  (:require [jedlikcljs.core :as core]
    [latte.chai :refer (expect)]
    [cljs.nodejs :as nodejs])
  (:require-macros [latte.core :refer (describe it)]))

(nodejs/enable-util-print!)

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
)


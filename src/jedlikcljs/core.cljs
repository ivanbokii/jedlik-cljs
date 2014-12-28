(ns jedlikcljs.core
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

; build steps
(defn- attributes-to-get
  [result api]
  (assoc result :AttributesToGet (:_attributes api))
)

(defn- key-conditions
  [result api]
  (let [keys (select-keys api [:_hashkey :_rangekey])
        hashkey (:_hashkey keys)
        rangekey (:_rangekey keys)]

  (assoc result :KeyConditions {(:key hashkey) {:AttributeValueList [{:S (:value hashkey)}]
                                                :ComparisonOperator "EQ"}
                                (:key rangekey) {:AttributeValueList [{:S (:value rangekey)}]
                                                 :ComparisonOperator (:comparison rangekey)}}))
)

(defn- table-name
  [result api]
  (assoc result :TableName (:_table api))
)

; builders
(defn- build-query
  "builds-query based on the api"
  [api]
  (let [query-steps [attributes-to-get key-conditions table-name]]
    (reduce #(%2 %1 api) {} query-steps))
)

; public api
(defn hashkey
  "sets hashkey params to the _hashkey property"
  [key value type]
  (swap! api assoc :_hashkey {:key key :value value :type type})
  @api
)

(defn rangekey
  "sets rangekey params to the _rangekey property "
  [key value comparison-op type]
  (swap! api assoc :_rangekey {:key key :value value :type type :comparison comparison-op})
  @api
)

(defn tablename
  "sets tablename params to the _table property "
  [tablename]
  (swap! api assoc :_table tablename)
  @api
)

(defn attributes
  "sets attributes params to the _attributes property "
  [attributes]
  (let [parsed-attributes (js->clj attributes)]
    (swap! api assoc :_attributes parsed-attributes)
    @api)
)

; public api producers
(defn query
  "returns constructed dynamodb query based on the previously saved information "
  []
  (clj->js (build-query @api))
)

; store for api fields
(def api (atom {:hashkey #(clj->js (apply hashkey %&))
  :rangekey #(clj->js (apply rangekey %&))
  :tablename #(clj->js (apply tablename %&))
  :attributes #(clj->js (apply attributes %&))
  :query #(clj->js (apply query %&))})
)

(defn Jedlik []
  (clj->js @api)
)

(set! (.-exports js/module) Jedlik)
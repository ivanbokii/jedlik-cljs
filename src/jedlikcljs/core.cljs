(ns jedlikcljs.core
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

;; build steps utils
(defn- generate-value-list [values]
  (vec (map #(identity {:S %}) values))
  )

(defn- generate-attribute-value-list [key]
  (let [comparison (or (:comparison key) "EQ")
        result {:ComparisonOperator comparison}]
    (cond 
      (= comparison "BETWEEN") (assoc result :AttributeValueList (generate-value-list (vals (select-keys key [:value-from :value-to]))))
      :else (assoc result :AttributeValueList (generate-value-list [(:value key)]))))
  )

;; build steps
(defn- attributes-to-get [result api]
  (when-let [attributes (:_attributes api)]
    (assoc result :AttributesToGet attributes))
  )

(defn- key-conditions [result api]
  (let [keys (select-keys api [:_hashkey :_rangekey])
        hashkey (:_hashkey keys)
        rangekey (:_rangekey keys)]

    (assoc result :KeyConditions {(:key hashkey) (generate-attribute-value-list hashkey)
                                  (:key rangekey) (generate-attribute-value-list rangekey)}))
  )

(defn- table-name [result api]
  (assoc result :TableName (:_table api))
  )

;; builders
(defn- build-query
  "builds-query based on the api"
  [api]
  (let [query-steps [attributes-to-get key-conditions table-name]]
    (reduce #(%2 %1 api) {} query-steps))
  )

;; public api
(defn hashkey
  "sets hashkey params to the _hashkey property"
  [key value type]
  (swap! api assoc :_hashkey {:key key :value value :type type})
  @api
  )

(defn rangekey
  "sets rangekey params to the _rangekey property"
  [key value comparison-op type]
  (swap! api assoc :_rangekey {:key key :value value :type type :comparison comparison-op})
  @api
  )

(defn rangekey-between
  "sets rangekey-between params to the _rangekey-between property"
  [key value-from value-to]
  (swap! api assoc :_rangekey {:key key :value-from value-from :value-to value-to :comparison "BETWEEN"})
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

(defn reset 
  "resets the api"
  []
  (reset! api {:hashkey #(clj->js (apply hashkey %&))
                :rangekey #(clj->js (apply rangekey %&))
                :rangekeyBetween #(clj->js (apply rangekey-between %&))
                :tablename #(clj->js (apply tablename %&))
                :attributes #(clj->js (apply attributes %&))
                :query #(clj->js (apply query %&))}))

;; public api producers
(defn query
  "returns constructed dynamodb query based on the previously saved information "
  []
  (let [result (build-query @api)]
    (reset)
    result))

;; store for api fields
(def api (atom {}))

(defn Jedlik []
  (clj->js @api)
  )

(reset)
(set! (.-exports js/module) Jedlik)

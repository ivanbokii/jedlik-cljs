(ns jedlikcljs.core
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

;; build steps utils
(defn- generate-value-list [values]
  (vec (map #(identity {:S %}) values)))

(defn- generate-attribute-value-list [key]
  (let [comparison (or (:comparison key) "EQ")
        result {:ComparisonOperator comparison}]
    (cond
      (= comparison "BETWEEN") (assoc result :AttributeValueList (generate-value-list (vals (select-keys key [:value-from :value-to]))))
      :else (assoc result :AttributeValueList (generate-value-list [(:value key)])))))

;; build steps
(defn- attributes-to-get [result api]
  (if-let [attributes (:_attributes api)]
    (assoc result :AttributesToGet attributes)
    result))

(defn- scan-index-forward [result api]
  (if (= (:_ascending api) false)
    (assoc result :ScanIndexForward false)
    result))

(defn- select-param [result api]
  (if-let [select (:_select api)]
    (assoc result :Select select)
    result))

(defn- key-conditions [result api]
  (let [keys (select-keys api [:_hashkey :_rangekey])
        hashkey (:_hashkey keys)
        rangekey (:_rangekey keys)
        result-with-hashkey (assoc result :KeyConditions {(:key hashkey) (generate-attribute-value-list hashkey)})]

    (if rangekey
      (assoc-in result-with-hashkey [:KeyConditions (:key rangekey)] (generate-attribute-value-list rangekey))
      result-with-hashkey)))

(defn- table-name [result api]
  (assoc result :TableName (:_table api)))

;; builders
(defn- build-query
  "builds-query based on the api"
  [api]
  (let [query-steps [attributes-to-get key-conditions table-name scan-index-forward select-param]]
    (reduce #(%2 %1 api) {} query-steps)))

;; public api
(defn hashkey
  "Hash key of a request"
  [key value type]
  (swap! api assoc :_hashkey {:key key :value value :type type})
  @api)

(defn rangekey
  "Range key of a request"
  [key value comparison-op type]
  (swap! api assoc :_rangekey {:key key :value value :type type :comparison comparison-op})
  @api)

(defn rangekey-between
  "Range key that is greater than or equal to the first value, and less than or equal to the second value."
  [key value-from value-to]
  (swap! api assoc :_rangekey {:key key :value-from value-from :value-to value-to :comparison "BETWEEN"})
  @api)

(defn tablename
  "The name of the table containing the requested items."
  [tablename]
  (swap! api assoc :_table tablename)
  @api)

(defn attributes
  "The names of one or more attributes to retrieve. If no attribute names are specified, then all attributes will be returned. If any of the requested attributes are not found, they will not appear in the result."
  [attributes]
  (let [parsed-attributes (js->clj attributes)]
    (swap! api assoc :_attributes parsed-attributes)
    @api)
  )

(defn select
  "The attributes to be returned in the result. You can retrieve all item attributes, specific item attributes, the count of matching items, or in the case of an index, some or all of the attributes projected into the index."
  [select]
    (swap! api assoc :_select select)
    @api)

(defn ascending
  "A value that specifies ascending (true) or descending (false) traversal of the index."
  [value]
    (swap! api assoc :_ascending value)
    @api)

(defn reset
  "resets the api"
  []
  (reset! api {:hashkey #(clj->js (apply hashkey %&))
               :rangekey #(clj->js (apply rangekey %&))
               :rangekeyBetween #(clj->js (apply rangekey-between %&))
               :tablename #(clj->js (apply tablename %&))
               :attributes #(clj->js (apply attributes %&))
               :select #(clj->js (apply select %&))
               :ascending #(clj->js (apply ascending %&))
               :query #(clj->js (apply query %&))}))

;; public api producers
(defn query
  "Returns constructed dynamodb query based on the previously saved information "
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

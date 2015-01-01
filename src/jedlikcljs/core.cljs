(ns jedlikcljs.core
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

;; build steps utils
(defn- generate-attribute-value-list [key]
  (when key
    (let [comparison (:comparison key)]
      (if (not= comparison "BETWEEN")
        {(:key key) {:AttributeValueList [(generate-value (:value key))] :ComparisonOperator (or (:comparison key) "EQ")}}
        {(:key key) {:AttributeValueList (vec (map generate-value [(:value-from key) (:value-to key)])) :ComparisonOperator (:comparison key)}}))))

(defn- generate-attribute [attribute]
  (when attribute
    {(:key attribute) (generate-value (:value attribute))}))

(defn- generate-value [value]
  {:S value})

;; build steps
(defn- add-from-lookup [name gathered-name]
  (fn [result api]
    (let [value (gathered-name api)]
      (if-not (nil? value)
        (assoc result name value)
        result))))

(defn- key-conditions [result api]
  (let [hashkey (:_hashkey api)
        rangekey (:_rangekey api)
        hashkey-section (generate-attribute-value-list hashkey)
        rangekey-section (generate-attribute-value-list rangekey)]
    (assoc result :KeyConditions (merge hashkey-section rangekey-section))))

(defn- exclusive-key-conditions [result api]
  (if-let [starthashkey (:_starthashkey api)]
    (let [hashkey (generate-attribute (:_starthashkey api))
          rangekey (generate-attribute (:_startrangekey api))]
      (assoc result :ExclusiveStartKey (merge hashkey rangekey)))
    result))

(defn- key [result api]
  (let [key (apply merge (map generate-attribute (vals (select-keys api [:_hashkey :_rangekey]))))]
    (assoc result :Key key)))

;; builders
(defn- build-query
  "builds query based on the api"
  [api]
  (let [query-steps [(add-from-lookup :AttributesToGet :_attributes)
                     key-conditions
                     exclusive-key-conditions
                     (add-from-lookup :TableName :_table)
                     (add-from-lookup :ScanIndexForward :_ascending)
                     (add-from-lookup :Select :_select)]]
    (reduce #(%2 %1 api) {} query-steps)))

(defn- build-update
  "builds update-query based on the api"
  [api]
  (let [update-steps [key]]
    (reduce #(%2 %1 api) {} update-steps)))

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

(defn starthashkey
  "The primary key of the first item that this operation will evaluate. Use the value that was returned for LastEvaluatedKey in the previous operation. Hashkey."
  [key value]
  (swap! api assoc :_starthashkey {:key key :value value})
  @api)

(defn startrangekey
  "The primary key of the first item that this operation will evaluate. Use the value that was returned for LastEvaluatedKey in the previous operation. Rangekey."
  [key value]
  (swap! api assoc :_startrangekey {:key key :value value})
  @api)

(defn attribute
  "The names of attributes to be modified, the action to perform on each, and the new value for each."
  [key value action]
  (swap! api #(update-in % [:_attribute-values] conj {:key key :value value :action action}))
  @api)

(defn reset
  "resets the api"
  []
  (reset! api {
               ; common
               :hashkey #(clj->js (apply hashkey %&))
               :rangekey #(clj->js (apply rangekey %&))
               :tablename #(clj->js (apply tablename %&))

               ; query
               :rangekeyBetween #(clj->js (apply rangekey-between %&))
               :attributes #(clj->js (apply attributes %&))
               :select #(clj->js (apply select %&))
               :ascending #(clj->js (apply ascending %&))
               :starthashkey #(clj->js (apply starthashkey %&))
               :startrangekey #(clj->js (apply startrangekey %&))
               :query #(clj->js (apply query %&))

               ;update
               :attribute #(clj->js (apply attribute %&))
               :update #(clj->js (update-query %&))
               }))

;; public api producers
(defn query
  "Returns constructed dynamodb query based on the previously saved information "
  []
  (let [result (build-query @api)]
    (reset)
    result))

(defn update-query
  "Returns constructed dynamodb update query based on the previously saved information "
  []
  (let [result (build-update @api)]
    (reset)
    result))

;; store for api fields
(def api (atom {}))

(defn Jedlik []
  (clj->js @api)
  )

(reset)
(set! (.-exports js/module) Jedlik)

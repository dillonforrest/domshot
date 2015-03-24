(ns domshot.core
  (:require [goog.dom]))

(enable-console-print!)


;; ---- Qualifiers ---- ;;

(def node-qualifiers
  "An atom of a list of predicates. Each takes the current node as its only arg.
  All predicates should return `true` if you want `snapshot` to include the node."
  (atom ()))

(defn include-node? [node]
  (not-any? false? (map #(% node) @node-qualifiers)))

(def attr-qualifiers
  "An atom of a list of predicates. Each takes an attr name and val as its
  args. All predicates should return `true` if you want `snapshot` to include the
  attr name-val pair."
  (atom ()))

(defn include-attr? [name val]
  (not-any? false? (map #(% name val) @attr-qualifiers)))

(defmulti add-qualifier (fn [_ qualifier-type] qualifier-type))

(defmethod add-qualifier :attribute [fn]
  (swap! attr-qualifiers conj fn))

(defmethod add-qualifier :default [fn]
  (swap! node-qualifiers conj fn))


;; ---- Serializing the dom ---- ;;

(defn get-attrs [node]
  (if (.-attributes node)
    (into {}
          (let [attrs (.-attributes node)]
            (for [i (range (.-length attrs))
                  :let [attr  (aget attrs i)
                        name  (.-name attr)
                        value (.-value attr)]
                  :when (include-attr? name value)]
              [name value])))
    {}))

(defn handle-special-nodes
  "Fills in important node-specific key-val pairs"
  [node-map native-node]
  (let [node-types goog.dom/NodeType]
    (condp = (.-nodeType native-node)
      (.-TEXT node-types) (assoc node-map :text (.-nodeValue native-node))
      node-map)))

(defn gen-basic-node-map [node]
  {:node-name  (.-nodeName node)
   :attributes (get-attrs node)})

(defn snapshot
  "Recursively serialize validated nodes into a clojure data structure"
  ([]
   (let [all-html (.-documentElement (goog.dom/getDocument))]
     (snapshot all-html)))
  ([node]
   (let [node-map (-> node gen-basic-node-map (handle-special-nodes node))]
     (assoc node-map
       :children (if (not (.hasChildNodes node))
                   []
                   (vec (for [index (range (-> node .-childNodes .-length))
                              :let [child (aget (.-childNodes node) index)]
                              :when (include-node? child)]
                          (snapshot child))))))))


;; ---- Building the dom ---- ;;

(defn build
  "Takes a structure produced by `snapshot` as its only arg.
  Returns a dom tree reflecting the given structure."
  [structure]
  (let [node-name (:node-name structure)]
    (if (= "#text" node-name)
      (goog.dom/createTextNode (:text structure))
      (goog.dom/createDom node-name
                          (clj->js (:attributes structure))
                          (when (< 0 (count (:children structure)))
                            (clj->js (for [i (range (count (:children structure)))
                                           :let [child (nth (:children structure) i)]]
                                       (build child))))))))

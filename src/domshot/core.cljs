(ns domshot.core
  (:require [goog.dom]))

(enable-console-print!)


;; ---- Node validators ---- ;;

(def invalidators
  "An atom of a list of predicates. Each takes the current node as its only arg.
  Predicates should return `true` if you want `snapshot` to ignore the node."
  (atom ()))

(defn include-node? [node]
  (not-any? false? (map #(% node) @invalidators)))

(defn add-invalidator [fn]
  (swap! invalidators conj fn))


;; ---- Serializing the dom ---- ;;

(defn get-attrs [node]
  (if (.-attributes node)
    (into {}
          (let [attrs (.-attributes node)]
            (for [i (range (.-length attrs))
                  :let [attr  (aget attrs i)
                        name  (.-name attr)
                        value (.-value attr)]]
              [name value])))
    {}))

(defn handle-special-nodes
  "Fills in important node-specific key-val pairs"
  [node-map native-node]
  (let [node-types goog.dom/NodeType]
    (condp = (.-nodeType native-node)
      (.-COMMENT node-types) (assoc node-map :text (.-nodeValue native-node))
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



;; ---- Testing the api... TODO: delete these and test like a pro, not a noob D: ---- ;;

(add-invalidator #(not= "SCRIPT" (.-nodeName %)))

(.log js/console "snapshot result" (clj->js (snapshot)))

(ns domshot.core
  (:require [goog.dom]))

(enable-console-print!)

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
  {:node-name (.-nodeName node)
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
                              :let [child (aget (.-childNodes node) index)]]
                          (snapshot child))))))))

(.log js/console "snapshot result" (clj->js (snapshot)))

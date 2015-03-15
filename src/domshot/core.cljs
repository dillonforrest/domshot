(ns domshot.core
  (:require [goog.dom]))

(enable-console-print!)

(defn get-attrs [node]
  (if (.hasAttributes node)
    (into {}
          (let [attrs (.-attributes node)]
            (for [i (range (.-length attrs))
                  :let [attr  (aget attrs i)
                        name  (.-name attr)
                        value (.-value attr)]]
              [name value])))
    {}))

(defn get-children [node]
  (if (.hasChildNodes node)
    (let [child-nodes (.-childNodes node)]
      (for [i (range (.-length child-nodes))]
        (snapshot node)))
    []))

  (defn complete-map "Fills in important node-specific key-val pairs"
    [node-map native-node]
    (let [node-types goog.dom/NodeType]
      (condp = (.-nodeType native-node)
        (.-COMMENT node-types) (assoc node-map :text (.-nodeValue native-node))
        node-map)))

(defn snapshot
  "Recursively serialize validated nodes into a clojure data structure"
  ([]
   (let [all-html (.-documentElement (goog.dom/getDocument))]
     (snapshot all-html)))
  ([node]
   (let [basic-mapping {:node-name (.-nodeName node)
                        :attributes (get-attrs node)
                        :children (get-children node)}
         complete-mapping (complete-map basic-mapping node)]
     complete-mapping)))

(println "snapshot result" (snapshot))

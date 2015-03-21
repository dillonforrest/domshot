# domshot

Take a snapshot of your dom, a domshot.

## Purpose

To easily serialize your dom to either send across the wire or to recreate your dom at another time.

## Setup

[![Clojars Project](http://clojars.org/domshot/latest-version.svg)](http://clojars.org/domshot)

## API reference

#### `domshot.core/snapshot`

```clojure
=> (snapshot (goog.dom/getElement "example"))
;; {:node-name "DIV" :attributes {"id" "example" "style" "display:block;"}
;;  :children [{:node-name "SPAN" attributes {"id" "example-child"}}]}
```

#### `domshot.core/build`

```clojure
=> (def tree (snapshot (goog.dom/getElement "example"))
=> (build tree)
;; <div id="example" style="display:block;">...</div>
```

#### `domshot.core/add-qualifier`

By default, adding a qualifier predicate will determine whether or not domshot should include a node while snapshotting your dom. The predicate takes the native node as its only arg.

```clojure
=> (def example (goog.dom/getElement "example"))
=> (snapshot example)
;; {:node-name "DIV" :attributes {"id" "example" "style" "display:block;"}
;;  :children [{:node-name "SPAN" attributes {"id" "example-child"}}]}
=> (add-qualifier #(not= "SPAN" (.-nodeName %)))
=> (snapshot example)
;; {:node-name "DIV" :attributes {"id" "example" "style" "display:block;"} :children []}
```

You can also qualify attribute name-val pairs. Attribute predicates take the attribute name and value as its args. Continuing back to our repl...

```clojure
=> (add-qualifier (fn [name val] (not= "style" name)) :attribute) ;; <-- DO NOT FORGET THE :attribute ARG!!
=> (snapshot example)
;; {:node-name "DIV" :attributes {"id" "example"} :children []}
```

## License

[MIT](license.txt)
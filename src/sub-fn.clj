(ns sub-fn #_lambda-parts?)
; pure-sub-fn, sub-fn
; pure-parts, fn-parts, pure-export
; sub-lambda, lambda-parts

; namespace -> function/macro/object
; Implementation
; letfn & replace top-level letfn with custom macro that calls orig. clojure.core/letfn and it keeps a track of them.
; capture the tracked functions (their names as keywords and references).
; Take the user-provided body of letfn. Wrap it with (with-meta namespaced keyword => map {function name as keyword, function...}}.
; If there was such a meta already (most likely from an inner letfn), then (merge inner outer).
; Feed it to (load-string ..). That can access outer context.
;
; Def the functions - but in the user's namespace.
; <- make the "exporter" a macro, so it can access the namespace where it's called from? <- *ns*
;
; clojure.main/load-script uses @ or @/ as relative to classpath

; Tests?
; (macroexpand '(letfn [(same [i] i)] (same 1)) ) ;=> letfn*
;
; Alternatives for implementation, but not allowed by 4clojure.com:
; - (defmacro ) env => hashmap of local bounded symbols (for meta)
; - reader token known only in test mode => call the above macro
; - (eval ...)
; - (read-string "#=expression") as per https://clojure.org/guides/weird_characters#_reader_eval.

(ns-unmap *ns* 'let) ;Otherwise the following generated a warning.
(ns-unmap *ns* 'letfn)
(ns-unmap *ns* 'load-string)
(ns-unmap *ns* 'load-file)

(defmacro let "Mostly private." [bindings & body]
  (clojure.core/let [names# (map str (take-nth 2 bindings))]
   `(with-meta (clojure.core/let ~bindings ~@body) {:names '(~@names#)})))

(defmacro letfn "Mostly private." [bindings & body]
  (clojure.core/let []
   `(with-meta (clojure.core/letfn ~bindings ~@body) {:i 1})))

; Can we have a (protocol-based?) function that
#_(defmacro fn "Like clojure.core/fn, but: It defines the function if you give it a name. It also defines sub-functions TODO How about the enclosing scope??"
          [name-or-params body] ;Alternative names: code, def-parts
          ;TODO collect let, letfn
          `())
; replace let and clojure.core/let

(defmacro from-source "Like def-parts, but from source. The function must have been loaded on classpath (not only via `defn`)."
 [fn-name] `(sub-fn/load-string (clojure.repl/source ~fn-name) #_TODO-check-string-escaping))

(defmacro load-string "Mostly private." [code])

(defmacro load-file [path]) ;[file-path]

  ;(load-string "(let [one (fn [] 1)] one)")

; clojure.core/load: the working directory is set to wherever you invoked the JVM from, likely the project root.

#_(defmacro def-one []
  ;`(def one 1) ;=> expands to: (def sub-fn/one 1) - in the called (packaged) namespace
   '(def one 1)) ;=> expands to: (def one 1) - in the caller's namespace
  ;---the same as difference between `one and 'one
  ; (symbol ...) takes an optional namespace: (= `one (symbol "user/one") ;=> true

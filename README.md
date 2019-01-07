# sub-fn

Do you develop Clojure Lambda functions? For example for 4clojure.com.
Or have you ever debugged a function with local functions? You may want to keep those sub-functions inside, especially if they access the context. But debugging them is difficult.
sub-fn helps you to debug such code, while keeping it close to the intended format, or even intact.

## Usage

Don't use with `(ns (:refer sub-fn))`, neither `(ns (:use sub-fn))`, nor with :all. Why? Because it overrides `let`, `letfn`.
; an expression => (usually) a function
;
; Scope
; Yes for `(eval ...)` (even though 4clojure.com doesn't allow eval).
; No for `clojure.core/load-file`, `clojure.core/load-string`.
 

## License

Copyright © 2019 Peter Kehl

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

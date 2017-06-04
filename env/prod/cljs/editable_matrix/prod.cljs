(ns editable-matrix.prod
  (:require [editable-matrix.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)

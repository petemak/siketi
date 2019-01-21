(ns siketi.applicaion
  (:require [siketi.routes :as routes]
            [siketi.db.memdb :as memdb]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))


;; Entry point
(def app
  (let [strg (memdb/storage)
        app-routes (routes/app-routes strg)]
    
    (wrap-defaults app-routes api-defaults)))

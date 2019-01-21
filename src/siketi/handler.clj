(ns siketi.handler
  (:require [compojure.core :refer :all]
            [ring.util.request :as req]
            [ring.util.response :as resp]
            [siketi.db :as db]))




(defn add-ask
  [strg name desc]
  (if-let [id (db/add-task strg name desc)]
    (resp/response id)
    (-> "Failed to add a new task"
        (resp/response)
        (resp/status 403))))


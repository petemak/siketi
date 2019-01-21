(ns siketi.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [siketi.handler :as handler]))

(defn  app-routes
  [strg]
  (routes 
   (GET "/" [] "Hello World")
   (route/not-found "Not Found")))

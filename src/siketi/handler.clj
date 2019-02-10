(ns siketi.handler
  (:require [compojure.core :refer :all]
            [ring.util.request :as req]
            [ring.util.response :as resp]
            [ring.middleware.json :refer [wrap-json-response]]
            [siketi.storage :as strg]))

;; HTTP error codes
;; 1xx informational
;; 100 continue
;;
;; 2xx Sucsess
;; 200 OK
;; 201 created
;; 202 Accepted
;;
;; 3xx Redirection
;; 301 Moved permanently
;; 300 Multiple choice
;; 303 See othere
;;
;; 4xx Client error
;; 400 Bad request
;; 401 Unauthorized
;; 403 Forbidded
;; 404 Not found
;; 406 Not acceptable
;;
;; 5xx Server error
;; 500 Internal server error
;; 501 Not implemented;; 503 Service unavilable
;;

;;
(defn add-task
  "Adds a new task with the given name and description. 
  Returns the task id in the response body
  Note: destructures the body to :body, then to :name amd :description"
  [strg-impl {{name :name desc :description} :body}]
  (if-let [id (strg/add-task strg-impl name desc)]
    (resp/response id)
    (-> "Internal errror: failed to add a new task"
        (resp/response)
        (resp/status 500))))


;;
(defn update-task
  "Updates the task name, description or both depending on data ffrom the 
  body of the resquest"
  [strg-impl id {{name :name desc :description} :body}]
  (if-let [task (strg/update-task strg-impl id name desc)]
    (resp/response task)
    (-> (str  "Task with [" id "] was not found")
        (resp/response)
        (resp/status 404))))



;;
(defn change-state
  "Change the state of the task to the give state"
  [strg-impl id {{desc :descrption state :state} :body}]
  (if-let [task (strg/change-task-state strg-impl id state)]
    (resp/response task)
    (-> (str "Task with id [" id "] not found!")
        (resp/response)
        (resp/status 404))))



(defn get-task
  "Returns the task with the given id"
  [strg-impl id]
  (if-let [task (strg/retrieve-task strg-impl id)]
    (resp/response task)
    (-> (str "Task with id [" id "] not found!")
        (resp/response)
        (resp/status 404))))




(defn get-tasks
  "Returns the task with the given id"
  [strg-impl]
  (wrap-json-response 
   (fn[request] ;; request map not needed though
     (let [tasks (strg/retrieve-tasks strg-impl)]
       (println "*******> Got tasks: " tasks)
       (resp/response tasks)))))


(defn remove-task
  [strg-impl id]
  (if-let [task (strg/remove-task strg-impl id)]
    (resp/response task)
    (-> (str "Task with id [" id "] not found!")
        (resp/response)
        (resp/status 404))))



(defn clear-tasks
  "Delete all tasks"
  [strg-impl]
  (let [stg (strg/clear-tasks strg-impl)]
    resp/response stg))

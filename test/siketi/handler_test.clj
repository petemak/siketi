(ns siketi.handler-test
  (:require [clojure.test :refer :all]
            [siketi.handler :refer :all]
            [siketi.storage :as strg]
            [siketi.storage.mem-storage :as mem-strg]
            [ring.mock.request :as mock]
            [cheshire.core :as cheshire]))


(def strg-impl (mem-strg/mem-store))

(comment 
  (defn setup-storage
    []
    (let [name "Default ask"
          desc "Do something default something"]
      (strg/add-task strg-impl name desc))))


(defn teardown-storage
  []
  (strg/clear-tasks strg-impl))


(defn prepare-storage
  [f]
  (f)
  (teardown-storage))

;; Fixures
;; USe fixures each time a test is invoked
(use-fixtures :each prepare-storage)


;;
;; Create a mock request to call add task
;; but the body will be java.io.ByteArrayInputStream
;; so just assoc body with expected map with name and description
(deftest test-add-task-hadler
  (testing "Addtion of a new task"
    (let [task {:name "Task 1" :description "Do something fot task 1"}
          request (-> (mock/request :post "/tasks")
                      (assoc :body task))
          response (add-task strg-impl request)]
      
      (testing "that response stause is 200 OK"
        (is (= (:status response) 200)))
      (testing "that a valid task id is returned in response body"
        (is (not= (:body response) nil))))))


(deftest test-update-task-hander
  (testing "Update of an existing task"
    (let [task {:name "Task 1" :description "Do something fot task 1"}
          request (-> (mock/request :post "/tasks")
                      (assoc :body task))
          response (add-task strg-impl request)
          id (:body response)]
      
      (testing "when the task exists"
        (let [task-mod {:name "Task 2" :description "Do something for task 2"}
              request-mod (-> (mock/request :post "/tasks")
                              (assoc :body task-mod))
              response-mod (update-task strg-impl id request-mod)]

          (testing "that the modified task is returned"
            (is (= (-> (:body response-mod)
                       (:id))
                   (:body response))))
          (testing "that the modifications are correct"
            (is (= (-> (:body response-mod)
                       (:name))
                   (:name task-mod)))

            (is (= (-> (:body  response-mod)
                       (:description))
                   (:description task-mod)))))))))


(deftest test-change-state-task-hander
  (testing "Update of an existing task"
    (let [task {:name "Task 1" :description "Do something fot task 1" :state "Open"}
          request (-> (mock/request :post "/tasks")
                      (assoc :body task))
          response (add-task strg-impl request)
          id (:body response)]
      
      (testing "when the task exists"
        (let [task-mod {:description "Closing task" :state "Closed"}
              request-mod (-> (mock/request :patch "/tasks")
                              (assoc :body task-mod))
              response-mod (change-state strg-impl id request-mod)]

          (testing "that the modified task is returned"
            (is (= (-> (:body response-mod)
                       (:id))
                   (:body response))))
          
          (testing "that the modifications are correct"
            (is (= (-> (:body response-mod)
                       (:state))
                   (:state task-mod)))))))))



(deftest test-get-task-hander
  (testing "Retrieval of an existing task"
    (let [task {:name "Task 1" :description "Do something fot task 1" :state "Open"}
          request (-> (mock/request :post "/tasks")
                      (assoc :body task))
          response (add-task strg-impl request)
          id (:body response)]
      
      (testing "when the task exists"
        (let [get-response (get-task strg-impl id)]

          (testing "that the name of task is as expected"
            (is (= (-> (:body get-response)
                       (:name))
                   (:name task))))

          (testing "that the description of the task is as expected"
            (is (= (-> (:body get-response)
                       (:description))
                   (:description task))))))


      (testing "when no task exists"
        (let [no-response (get-task strg-impl "No-id")]
          (testing "that response status is 404 not found"
            (is (= (:status no-response)
                   404))))))))




(deftest test-get-tasks-hander
  (testing "Retrieval of an existing task"
    (let [tasks [{:name "Task 1" :description "Do something fot task 1" :state "Open"}
                 {:name "Task 2" :description "Do something fot task 2" :state "Open"}
                 {:name "Task 3" :description "Do something fot task 3" :state "Open"}]]
      (doseq [task tasks]
        (let [req (-> (mock/request :post "/tasks")
                      (assoc :body task))]
          (add-task strg-impl req)))
      
      (testing "when the tasks exists"
        (let [handler (get-tasks strg-impl)
              json-response (handler (mock/request :get "/tasks"))
              decoded-tasks (cheshire/decode (:body json-response))]
          
          (testing "that the name of task is as expected"
            (is (= 3 (count decoded-tasks))))
          
          ;; (some #(= {} %) seq)
          ;; Task went through wrap-json-response so keywords were turned
          ;; to strings
          (testing "that the description of the task is as expected"
            (is (= (some #(= (get (first decoded-tasks) "name") % )
                         (map :name tasks)) true))))))))


(deftest test-remove-task-hander
  (testing "Removal of all existing task"
    (let [tasks [{:name "Task 1" :description "Do something fot task 1" :state "Open"}
                 {:name "Task 2" :description "Do something fot task 2" :state "Open"}
                 {:name "Task 3" :description "Do something fot task 3" :state "Open"}]
          task-ids []]
      (doseq [task tasks]
        (let [req (-> (mock/request :post "/tasks")
                      (assoc :body task))]
          (->> (add-task strg-impl req)
               (:body)
               (conj task-ids))))
      
      (testing "when the tasks exists"
        (let [response (remove-task strg-impl (first task-ids))]
          (testing "that the name of task is as expected"
            (is (= (-> (:body response)
                       (:id))
                   (first task-ids)))))))))




(deftest test-remove-tasks-hander
  (testing "Removal of all existing task"
    (let [tasks [{:name "Task 1" :description "Do something fot task 1" :state "Open"}
                 {:name "Task 2" :description "Do something fot task 2" :state "Open"}
                 {:name "Task 3" :description "Do something fot task 3" :state "Open"}]]
      (doseq [task tasks]
        (let [req (-> (mock/request :post "/tasks")
                      (assoc :body task))]
          (add-task strg-impl req)))
      
      (testing "when the tasks exists"
        (let [strg (clear-tasks strg-impl)]
          (testing "that the name of task is as expected"
            (is (= 0 (count strg)))))))))


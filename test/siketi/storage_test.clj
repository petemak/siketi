(ns siketi.storage-test
  (:require [siketi.storage :refer :all]
            [siketi.storage.mem-storage :as ms]
            [clojure.test :refer :all]))




(defn valid-storage?
  "Given a storage implementation of the TaskStorage protocol
  asserts that it fullfils the contract"
  [strg]
  (let [name "Test memory store"
        desc "Asserts that it memort storage fullfils the 
              TaskStorage contract"
        id (add-task strg name desc)]
    (testing "add-task: Can store a new taks and return its ID"
      (is (not= id nil)))

    (testing "update-task: Can update an exisitng task"
      (let [new-name (str name "-modified")
            new-desc (str desc "-modified")
            task (update-task strg
                              id
                              new-name
                              new-desc)]
        (is (= (:id task) id))
        (is (= (:name task) new-name))
        (is (= (:description task) new-desc))))

    (testing "set-state: can change the state of the task"
      (let [task (change-task-state strg id "CLOSED")]
        (is (= (:id task) id))
        (is (= (:state task) "CLOSED"))))


    (testing "retrieve-task: can return as task"
      (let [task (retrieve-task strg id)]
        (is (= (:id task) id))))

    (testing "retrieve-tasks: can return as alls task"
      (let [tasks (retrieve-tasks strg)]
        (is (= (count tasks) 1))))
    

    (testing "remove-task: can delet a task"
      (let [task (remove-task strg id)]
        (is (= (:id task) id))
        (is (= (retrieve-task strg id) nil))))

    (testing "remove-tasks: can delete all task"
      (let [tasks [{:name "task1" :description "Desc 1"}
                   {:name "Task2" :description "Desc 2"}]]

        ;; add some tasks
        (doseq [task tasks]
          (add-task strg (:name task) (:descripion task)))

        ;; Clear the storage
        (clear-tasks strg)

        (testing "that the storage is empty after removal" 
          (let [tasks (retrieve-tasks strg)] 
            (is (empty? tasks))))))))



(deftest mem-storage-test
  (let [stg (ms/mem-store)]
    (valid-storage? stg)))

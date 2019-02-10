(ns siketi.storage.mem-storage
  (:require [siketi.storage :refer :all]
            [siketi.util.id-generator :as id-gen]))

;; Memory storage implementation of the protocol TaskStorage
(defrecord MemStorage [atm]
  TaskStorage
  (add-task [this name description]
    (let [id (id-gen/new-id)
          task {:id id :name name :description description}] 
      (swap! atm assoc id task)
      (:id task)))

  (update-task [this id nam description]
    (if-let [task (get @atm id)]
      (let [new-task (merge task {:name nam :description description})]
        (swap! atm assoc id new-task)
        new-task)))


  (change-task-state [this id state]
    (if-let [task (get @atm id)]
      (do        
        (swap! atm assoc-in [id :state] state)
        (get @atm id))))


  (retrieve-task [this id]
    (get @atm id))

  (retrieve-tasks [this]
    (vals @atm))

  (remove-task [this id]
    (if-let [task (get @atm id)]
      (do
        (swap! atm dissoc id)
        task)))

  (clear-tasks [this]
    (swap! atm empty)))


(defn mem-store
  "Returns the memory storage"
  []
  (->MemStorage (atom {}) ))

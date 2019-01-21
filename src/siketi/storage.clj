(ns siketi.storage)


(defprotocol TaskStorage
  (add-task [this name description]
    "Adds a new task to the inbox and returns the ID.")
  (update-task [this id name description]
    "Updates the tasks name or description and returns the modified task")
  (change-task-state [this id state]
    "Changes the status of a task with the given id and return the modified task")
  (retrieve-task [this id]
    "Returns the task with the specified id")
  (retrieve-tasks [this]
    "Returns a list of all tasks")
  (remove-task [this id]
    "Deletes the task with the give id")
  (clear-tasks [this]
    "Deletes all tasks in the storage"))

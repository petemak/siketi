(ns siketi.util.id-generator)

(defn new-id
  []
  (.toString (java.util.UUID/randomUUID)))

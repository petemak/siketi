(ns siketi.middleware)


(defn wrap-slurp-body
  "create a handler that reads the body if it 
  contains instance of the mutable input strema"
  [handler]
  (fn [request]
    (if (instance? java.io.InputStream (:body request))
      (let [slurped-request (update request :body slurp)]
        (handler slurped-request))
      (handler request))))

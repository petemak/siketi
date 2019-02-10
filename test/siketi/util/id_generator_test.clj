(ns siketi.util.id-generator-test
  (:require [clojure.test :refer :all]
            [siketi.util.id-generator :refer :all]))

(deftest id-generator-test
  "Testing the id-generator"
  (let [id1 (new-id)
        id2 (new-id)]
    (testing "that ids generated and not nil"
      (is (not= id nil)))
    (testing "that isd are not equal" 
      (is (not= id1 id2)))))

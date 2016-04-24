(ns reactive-shipping.core
  (:gen-class)
  (:require [clojure.core.async :as async]))

(defn println-err [& args]
  (binding [*out* *err*]
    (apply println args)))

(defn ship-it
  "ship my package"
  [package]
  ;; take some to label each package and pass the package
  (Thread/sleep 1000)
  (str package " shipped"))

(def stdin-reader
  (java.io.BufferedReader. *in*))

(def in-chan (async/chan))
(def out-chan (async/chan))

(defn shipping-consumers
  "Start num-consumers threads that will consume work
  from the in-chan and put it into the out-chan."
  [num-consumers]
  (dotimes [_ num-consumers]
    (async/thread
      (while true
        (let [package (async/<!! in-chan)
              data (ship-it package)]
          (async/>!! out-chan data))))))

(defn shipping-aggregator
  "Take items from the out-chan and print it."
  []
  (async/thread
    (while true
      (let [data (async/<!! out-chan)]
        (println data)))))

(defn read-and-ship [input-events]
  (do
    (shipping-consumers 8)
    (shipping-aggregator)
    (doseq [package (line-seq stdin-reader)]
      (async/>!! in-chan package))))

(defn -main
  "application"
  [& args]
  (let [[mode num-times] args]
    (println-err args)
    (case mode

      "async"
      (read-and-ship "input-events")

      "inline"
      ;; Read each package from stdin
      (doseq [package (line-seq stdin-reader)]
        (let [data (ship-it package)]
          (println data))))))

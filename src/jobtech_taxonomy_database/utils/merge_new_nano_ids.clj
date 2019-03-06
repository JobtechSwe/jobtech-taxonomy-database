(ns jobtech-taxonomy-database.utils.merge-new-nano-ids
  (:gen-class)
  (:require [cheshire.core :refer :all]
            [clojure.string :as str]
            [clojure.pprint :as pp]))

(defn merge-new-id "" [target new-struct]
  (let* [category         (first (keys new-struct))
         category-struct  (first (vals new-struct))
         id               (first (keys category-struct))
         id-struct        (first (vals category-struct))
         found-cat        (get target (keyword category))
         found-id         (get found-cat id)]
    (if found-id
      (binding [*out* *err*]
        (if (not= (get found-id :preferredTerm)
                  (get id-struct :preferredTerm))
          (println (format "very bad, id %s differens in preferredTerm: \"%s\" vs. \"%s\"" id (get found-id :preferredTerm) (get id-struct :preferredTerm))))))
    (assoc target (keyword category)
           (assoc found-cat (keyword id) id-struct))))

(defn merge-new-ids-rec "" [target lines rdr]
  (let [line (first lines)
        rest-lines (rest lines)
        merged-target (merge-new-id target (read-string line))]
    (if (> (count rest-lines) 0)
      (merge-new-ids-rec merged-target rest-lines rdr)
      merged-target)))

(defn merge-new-ids "" [org-struct fname-id-file]
  (let [fname-new-ids fname-id-file]
    (with-open [rdr (clojure.java.io/reader fname-new-ids)]
      (if-let [lines (line-seq rdr)]
        (merge-new-ids-rec org-struct lines rdr)
        org-struct))))

(defn save "" [data fname-out]
  (set! *print-length* 100000)
  (with-open [w (clojure.java.io/writer fname-out)]
    (binding [*out* w]
      (print data))))

(defn open-json
  "Open json, return map with json keyword formatted as clojure keywords."
  [fname-in]
  (parse-string (slurp fname-in) true))

(defn -main
  []
  (let* [org-struct  (open-json "resources/taxonomy_to_concept_v67.json")
         result (merge-new-ids org-struct "resources/ids-for-skill-headlines-and-mainheadlines_v67.json")]
    (save (generate-string result {:pretty true}) "resources/taxonomy_to_concept_with_headlines_and_mainheadlines_v67.json")
    result))
;; (-main)

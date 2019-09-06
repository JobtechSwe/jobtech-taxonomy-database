(ns jobtech-taxonomy-database.converters.SUN-education-field-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano-id-assigner]
            [dk.ative.docjure.spreadsheet :as dox]
            [jobtech-taxonomy-database.types :as t]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [clojure.string :as s]
            ))


(defn if-double-convert-to-int-string [stuff]
  (if (double? stuff)
    (str (int stuff))
    (str stuff)
    )
  )

(defn load-book []
  (dox/load-workbook "resources/suncode.xlsx")
  )

(def book (memoize load-book))


(defn load-sheet [sheet-name]
  (dox/select-sheet  sheet-name (book))
  )


(defn load-sun-levels-raw []
  (dox/select-columns {:A :level-1-code :B :level-1-label :D :level-2-code :E :level-2-label :G :level-3-code :H :level-3-label}  (load-sheet "Nivåer, klartext"))
  )

(def sun-levels-data (memoize load-sun-levels-raw))


(defn load-sun-fields-raw []
  (dox/select-columns {:A :field-1-code :B :field-1-label :D :field-2-code :E :field-2-label :G :field-3-code :H :field-3-label :J :field-4-code :K :field-4-label}  (load-sheet "Inriktning, klartext"))
  )

(def sun-fields-data (memoize load-sun-fields-raw))

(defn create-broader-temp-id [code type]
  (u/create-temp-id type (apply str (drop-last code)))
  )

(defn create-level-concept [temp-id concept-id definition label instance-type code-2020 external-standard]
  {:db/id                                        temp-id
   :concept/id                                   concept-id
   :concept/definition                           definition
   :concept/preferred-label                      label
   :concept/type                                 instance-type
   external-standard code-2020
   }
  )

(defn sun-level-as-concept

  ([code label instance-type external-standard]
   [(create-level-concept
     (u/create-temp-id instance-type code)
     (u/get-concept-id instance-type code)
     (s/trim label)
     (s/trim label)
     instance-type
     code
     external-standard
     )])
  ([code label instance-type external-standard broader-instance-type]
   (let [concept (first (sun-level-as-concept code label instance-type external-standard))
         relation  (u/create-broader-relation-to-concept concept (create-broader-temp-id code broader-instance-type ))
         ]

     [concept relation]
     )
   )
  )

(defn extract-level-1 [data]
  (mapcat (fn [{:keys [level-1-code level-1-label]}]
         (sun-level-as-concept
          (if-double-convert-to-int-string level-1-code)
          level-1-label
          t/sun-education-level-1
          :concept.external-standard/sun-education-level-code-2020
          )
         )
       (drop 4(take 11 data)))
  )


(defn get-sun-level-1 []
  (extract-level-1 (sun-levels-data))
  )



(defn extract-level-2 [data]
  (mapcat (fn [{:keys [level-2-code level-2-label]}]
         (sun-level-as-concept
          (if-double-convert-to-int-string level-2-code)
          level-2-label
          t/sun-education-level-2
          :concept.external-standard/sun-education-level-code-2020
          t/sun-education-level-1
          )
         )
       (drop 4(take 18 data)))
  )

(defn get-sun-level-2 []
  (extract-level-2 (sun-levels-data))
  )


(defn extract-level-3 [data]
  (mapcat (fn [{:keys [level-3-code level-3-label]}]
         (sun-level-as-concept
          (if-double-convert-to-int-string level-3-code)
          level-3-label
          t/sun-education-level-3
          :concept.external-standard/sun-education-level-code-2020
          t/sun-education-level-2
          )
         )
       (drop 4(take 54 data)))
  )

(defn get-sun-level-3 []
  (extract-level-3 (sun-levels-data))
  )



(defn extract-field-1 [data]
  (mapcat (fn [{:keys [field-1-code field-1-label]}]
            (sun-level-as-concept
             (if-double-convert-to-int-string field-1-code)
             field-1-label
             t/sun-education-field-1
             :concept.external-standard/sun-education-field-code-2020
             )
            )
          (drop 4 (take 14 data)))
  )


(defn get-sun-field-1 []
  (extract-field-1 (sun-fields-data))
  )



(defn extract-field-2 [data]
  (mapcat (fn [{:keys [field-2-code field-2-label]}]
            (sun-level-as-concept
             (if-double-convert-to-int-string field-2-code)
             field-2-label
             t/sun-education-field-2
             :concept.external-standard/sun-education-field-code-2020
             t/sun-education-field-1
             )
            )
          (drop 4 (take 30 data)))
  )


(defn get-sun-field-2 []
  (extract-field-2 (sun-fields-data))
  )


(defn extract-field-3 [data]
  (mapcat (fn [{:keys [field-3-code field-3-label]}]
            (sun-level-as-concept
             (if-double-convert-to-int-string field-3-code)
             field-3-label
             t/sun-education-field-3
             :concept.external-standard/sun-education-field-code-2020
             t/sun-education-field-2
             )
            )
          (drop 4 (take 122 data)))
  )


(defn get-sun-field-3 []
  (extract-field-3 (sun-fields-data))
  )


(defn extract-field-4 [data]
  (mapcat (fn [{:keys [field-4-code field-4-label]}]
            (sun-level-as-concept
             (if-double-convert-to-int-string field-4-code)
             field-4-label
             t/sun-education-field-4
             :concept.external-standard/sun-education-field-code-2020
             t/sun-education-field-3
             )
            )
          (drop 4 (take 381 data)))
  )


(defn get-sun-field-4 []
  (extract-field-4 (sun-fields-data))
  )


(defn convert []
  (concat
   (get-sun-level-1)
   (get-sun-level-2)
   (get-sun-level-3)
   (get-sun-field-1)
   (get-sun-field-2)
   (get-sun-field-3)
   (get-sun-field-4)
   )
  )

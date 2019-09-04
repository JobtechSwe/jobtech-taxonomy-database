(ns jobtech-taxonomy-database.converters.SUN-education-field-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano-id-assigner]
            [dk.ative.docjure.spreadsheet :as dox]
            [jobtech-taxonomy-database.types :as t]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            ))


(defn load-book []
  (dox/load-workbook "resources/suncode.xlsx")
  )

(def book (memoize load-book))


(defn load-sheet [sheet-name]
  (dox/select-sheet  sheet-name (book))
  )

(defn filter-nil [{:keys [code-2000 code-2020 label-2020 label-2000]}]
  (not (every? nil? [code-2000 code-2020 label-2000 label-2020]))
  )

(defn if-double-convert-to-int-string [stuff]
  (if (double? stuff)
    (str (int stuff))
    (str stuff)
    )
  )

(defn convert-double-to-int-string [data]
  (-> data
      (update :code-2000 if-double-convert-to-int-string)
      (update :code-2020 if-double-convert-to-int-string)
      )
  )


(defn get-sun-level-1 []
  (map convert-double-to-int-string
       (rest
        (filter filter-nil (dox/select-columns {:A :code-2000 :B :label-2000 :C :code-2020 :D :label-2020} (load-sheet  "1-siffer_2020-2000")))))
  )

(defn get-sun-level-2 []
  (map convert-double-to-int-string
       (rest
        (filter filter-nil (dox/select-columns {:A :code-2000 :B :label-2000 :C :code-2020 :D :label-2020} (load-sheet  "2-siffer_2020-2000")))))
  )

(defn get-sun-level-3 []
  (map convert-double-to-int-string
       (rest
        (filter filter-nil(dox/select-columns {:A :code-2000 :B :label-2000 :C :code-2020 :D :label-2020} (load-sheet  "3-siffer_2020-2000")))))
  )


(defn get-sun-level-4 []
  (filter filter-nil (rest  (dox/select-columns {:A :code-2000 :B :label-2000 :C :code-2020 :D :label-2020} (load-sheet  "4-siffer_2020-2000"))))
  )


(defn create-concept [temp-id concept-id definition label instance-type code-2020 code-2000]
  {:db/id                                        temp-id
   :concept/id                                   concept-id
   :concept/definition                           definition
   :concept/preferred-label                      label
   :concept/type                                 instance-type
   :concept.external-standard/sun-field-code-2020 code-2020
   :concept.external-standard/sun-field-code-2000 code-2000
   }
  )


(defn sun-as-concept [instance-type data]

  (let [{:keys [code-2000 label-2000 code-2020 label-2020]} data]

    (create-concept (u/create-temp-id instance-type code-2020)
                    (u/get-concept-id instance-type code-2020 )
                    label-2020
                    label-2020
                    instance-type
                    code-2020
                    code-2000))
  )


(defn convert-sun-level-1 []
  (map #(sun-as-concept t/sun-education-field-1 % ) (get-sun-level-1)  )
  )

(defn convert-sun-level-2 []
  (map #(sun-as-concept t/sun-education-field-2 % ) (get-sun-level-2)  )
  )

(defn convert-sun-level-3 []
  (map #(sun-as-concept t/sun-education-field-3 % ) (get-sun-level-3)  )
  )

(defn convert-sun-level-4 []
  (map #(sun-as-concept t/sun-education-field-4 % ) (get-sun-level-4)  )
  )

;;defn convert

(defn convert []
  (concat
   (convert-sun-level-1)
   (convert-sun-level-2)
   (convert-sun-level-3)
   (convert-sun-level-4)
   )
  )

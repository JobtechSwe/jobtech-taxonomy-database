(ns jobtech-taxonomy-database.converters.driving-licence-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]
            ))


(defn combination-to-relation [{:keys [kombinationsid körkortsid]}]
  (let [
        temp-id-1 (str "driving-licence-combination-" kombinationsid)
        temp-id-2 (str "driving-license-" körkortsid)
        ]
    (u/create-relation temp-id-1 temp-id-2 :hyperonym)
    )
  )

(defn convert-driving-licence-combination-grouped [grouped-combinations]
  (let [nano-id (nano/get-nano)
        driving-licence-id (first grouped-combinations)
        temp-id (str "driving-licence-combination-" driving-licence-id)
        relations (map combination-to-relation (second grouped-combinations))
        ]
    (concat
     relations
     [
      (u/create-concept nano-id temp-id temp-id temp-id :driving-licence-combination driving-licence-id)
      (u/create-term nano-id temp-id)
     ]
     )
    )
  )


(defn converter
  "Immutable driving licence converter."
;  (let [type :driving-licence                ;json-nyckeln
;        id-67 (str (:drivinglicenceid data))        ;ska matcha legacyAmsTaxonomyId i json
;        description-67 (:term data)]                ;ska matcha preferredTerm i json
    ;(let
      ;[nano-id (nano/get-nano category-67 (keyword id-67))]

      [{:keys [term description drivinglicenceid]}]
      {:pre [term description drivinglicenceid]}
      (let
        [concept (u/create-concept t/occupation-field term description drivinglicenceid)]
        [concept (u/create-term (:concept/id concept) term)]
        ))

#_
      [{:db/id                                          (str "driving-licence-" id-67)
        :concept/id                                     nano-id
        :concept/description                            (:description data)
        :concept/preferred-term                         nano-id
        :concept.external-database.ams-taxonomy-67/id   id-67
        :concept/category                               category-67
        :concept.category/sort-order                    (:displaysortorder data)
        :concept.external-standard/driving-licence-code (:drivinglicencecode data)
        }
       {:db/id          nano-id
        :term/base-form description-67}];;)))



;; (group-by :kombinationsid ( legacy-migration/fetch-data  legacy-migration/get-driving-licence-combination))


(defn convert
  "Query db for driving licences, convert each entity"
  []
  (concat
   (mapcat converter (legacy-migration/fetch-data legacy-migration/get-driving-licence))
   (mapcat convert-driving-licence-combination-grouped  (group-by :kombinationsid ( legacy-migration/fetch-data  legacy-migration/get-driving-licence-combination))))
  )

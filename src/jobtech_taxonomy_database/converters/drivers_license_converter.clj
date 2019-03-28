(ns jobtech-taxonomy-database.converters.drivers-license-converter
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano]
            [jobtech-taxonomy-database.converters.converter-util :as util]
            ))


(defn combination-to-relation [{:keys [kombinationsid körkortsid]}]
  (let [
        temp-id-1 (str "driving-licence-combination-" kombinationsid)
        temp-id-2 (str "driving-license-" körkortsid)
        ]
    (util/create-relation temp-id-1 temp-id-2 :hyperonym)
    )
  )

(defn convert-driving-lincense-combination-grouped [grouped-combinations]
  (let [nano-id (nano/get-nano)
        driving-license-id (first grouped-combinations)
        temp-id (str "driving-licence-combination-" driving-license-id)
        relations (map combination-to-relation (second grouped-combinations))
        ]
    (concat
     relations
     [
      (util/create-concept nano-id temp-id temp-id temp-id :driving-license-combination driving-license-id)
      (util/create-term nano-id temp-id)
     ]
     )
    )
  )


(defn converter
  "Immutable driver's license converter."
  [data]
  (let [category-67 :driving-license                ;json-nyckeln
        id-67 (str (:drivinglicenceid data))        ;ska matcha legacyAmsTaxonomyId i json
        description-67 (:term data)]                ;ska matcha preferredTerm i json
    (let [nano-id (nano/get-nano category-67 (keyword id-67))]
      [{:db/id                                          (str "driving-license-" id-67)
        :concept/id                                     nano-id
        :concept/description                            (:description data)
        :concept/preferred-term                         nano-id
        :concept.external-database.ams-taxonomy-67/id   id-67
        :concept/category                               category-67
        :concept.category/sort-order                    (:displaysortorder data)
        :concept.external-standard/drivers-licence-code (:drivinglicencecode data)
        }
       {:db/id          nano-id
        :term/base-form description-67}])))



;; (group-by :kombinationsid ( legacy-migration/fetch-data  legacy-migration/get-drivers-license-combination))


(defn convert
  "Query db for driver's licenses, convert each entity"
  []
  (concat
   (mapcat converter (legacy-migration/fetch-data legacy-migration/get-drivers-license))
   (mapcat convert-driving-lincense-combination-grouped  (group-by :kombinationsid ( legacy-migration/fetch-data  legacy-migration/get-drivers-license-combination))))
  )

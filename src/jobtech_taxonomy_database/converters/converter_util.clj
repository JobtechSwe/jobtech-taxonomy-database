(ns jobtech-taxonomy-database.converters.converter-util
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as legacy-migration]
            [jobtech-taxonomy-database.converters.nano-id-assigner :as nano-id-assigner]))


(defn create-concept [nano-id temp-id term description category legacy-ams-db-id]
  {:db/id                                        temp-id
   :concept/id                                   nano-id
   :concept/description                          description
   :concept/preferred-term                       nano-id
   :concept/alternative-terms                    #{nano-id}
   :concept/category                             category
   :concept.external-database.ams-taxonomy-67/id (str legacy-ams-db-id);; todo rename attribute
   }
  )

(defn create-term [nano-id term]
  {:db/id          nano-id
   :term/base-form term}
  )

(defn create-relation [concept1 concept2 type]
  {:relation/concept-1 concept1
   :relation/concept-2 concept2
   :relation/type type
   }
  )

(ns jobtech-taxonomy-database.converters.occupation-converter
  (:gen-class)
  (:require [datomic.client.api :as d]
            [jobtech-taxonomy-database.schema :refer :all :as schema]
            [jobtech-taxonomy-database.legacy-migration :refer :all]
            [jobtech-taxonomy-database.config :refer :all]
            [jobtech-taxonomy-database.datomic-connection :refer :all :as conn]
            [jobtech-taxonomy-database.converters.nano-id-assigner :refer :all]
            [cheshire.core :refer :all]
            ))


(comment
  (fetch-data get-occupation-group-ssyk)
  (fetch-data get-occupation-field)
  )


(defn create-concept [nano-id temp-id term description category]
  {:db/id                     temp-id
   :concept/id                nano-id
   :concept/description       description
   :concept/preferred-term    nano-id
   :concept/alternative-terms #{nano-id}
   :concept/category category
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

(defn convert-occupation-field
  [{:keys [term description localefieldid]}]
  {:pre [term description  localefieldid]}
  (let [nano-id (get-nano "occupation-field" (str localefieldid))
        temp-id  (str "occupation-field-" localefieldid)
        ]
    [
     (create-concept nano-id temp-id term description :occupation-field)
     (create-term nano-id term)
     ]
    )
  )

(defn  convert-ssyk
  [{:keys [localecode term description localegroupid localefieldid]}]
  {:pre [localecode term description localegroupid localefieldid]}

  (let [nano-id (get-nano "occupation-group" (str localecode))
        temp-id  (str "occupation-group-" localegroupid)
        temp-id-field (str "occupation-field-" localefieldid)
        ]
    [
     (create-concept nano-id temp-id term description :occupation-group)
     (create-term nano-id term)
     (create-relation temp-id temp-id-field :hyperonym)
     ]
    )
  )

(defn convert-occupation-name
  [{:keys [term occupationgroupid occupationnameid localegroupid]}]
  {:pre [term occupationgroupid occupationnameid localegroupid]}

  (let [nano-id (get-nano "occupation-name" (str occupationnameid))
        temp-id (str "occupation-name-" occupationnameid)
        temp-id-ssyk (str "occupation-group-" (str localegroupid) )
        ]
    [{
      :db/id                     temp-id
      :concept/id                nano-id
      :concept/description       term
      :concept/preferred-term    nano-id
      :concept/alternative-terms #{nano-id}

      :concept/category          :occupation-name}
     {:db/id          nano-id
      :term/base-form term}
     {

      :relation/concept-1 temp-id
      :relation/concept-2 temp-id-ssyk
      :relation/type :hyperonym    ;; TODO decide how to model this properly
      }
     ]
    )
  )

(defn convert
  ""
  []
  (concat
   (mapcat  convert-occupation-name (fetch-data get-occupation-name))
   (mapcat convert-ssyk (fetch-data get-occupation-group-ssyk))
   (mapcat convert-occupation-field (fetch-data  get-occupation-field)))
  )

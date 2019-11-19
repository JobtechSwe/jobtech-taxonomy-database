(ns jobtech-taxonomy-database.converters.employment-type-new-changes
  (:gen-class)
  (:require [jobtech-taxonomy-database.legacy-migration :as lm]
            [jobtech-taxonomy-database.converters.converter-util :as u]
            [jobtech-taxonomy-database.types :as t]
            [jobtech-taxonomy-database.converters.employment-type-converter :as etc]
            ))



(comment

  Tillsvidareanställning, inklusive provanställning
  Tidsbegränsad anställning/Vikariat
  Behovsanställning/Timanställning
  Säsongsanställning


  Och såhär ser hänvisningarna ut:

  Vanlig anställning -> Tillsvidareanställning, inklusive provanställning
  Sommarjobb/feriejobb -> Säsongsanställning
  Arbete utomlands -> utgår
  Behovsanställning -> Behovsanställning/Timanställning
  Tidsbegränsad anställning/Vikariat -> ny (edited)

  Och värdeförrådet byter också namn:
  Anställningstyp -> Anställningsform (edited)

  )


(def updated-employmet-types
  [
   {:new-term "Tillsvidareanställning, inklusive provanställning" :sort 1 :legacy-id 1}
   {:new-term "Säsongsanställning" :sort 2 :legacy-id 2}
   {:new-term "Behovsanställning/Timanställning" :sort 3 :legacy-id 4}
   ]
  )

(defn convert-deprecated []
  (u/deprecate-concept t/employment-type 3)
  )

(defn convert-new []
  (etc/converter {:beteckning "Tidsbegränsad anställning/Vikariat"
                  :anstallningtypjobbid 5
                  :isortering 4 })
  )

(def new-employment-type-vikariat
  {:db/id "employment-type-5",
   :concept/id "vWxw_BCd_VG6",
   :concept/definition "Tidsbegränsad anställning/Vikariat",
   :concept/preferred-label "Tidsbegränsad anställning/Vikariat",
   :concept/type "employment-type",
   :concept.external-database.ams-taxonomy-67/id "5",
   :concept/sort-order 4}
  )

(defn update [data]
  (let [entity-id (u/get-entity-id-by-legacy-id (:legacy-id data) t/employment-type )]
    (u/update-concept entity-id data)
    )
  )

(defn convert-updated []
  (mapcat update updated-employmet-types )
  )

(defn convert []
  (conj
        (convert-updated)
        new-employment-type-vikariat
        (convert-deprecated)
        )
  )

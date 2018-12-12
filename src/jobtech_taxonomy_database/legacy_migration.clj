(ns jobtech-taxonomy-database.legacy-migration
  (:gen-class)
  (:require
   [korma.db :refer :all]
   [korma.core :refer :all :exclude [update]]
   )

  )



(defdb db (mssql {:db "TaxonomyDB"
                  :user "SA"
                  :password "Taxonomy123!"}))


(declare Skill SkillTerm SkillHeadline)

(defentity Skill
  (pk :skillID)
  (has-one SkillTerm {:fk :skillID})
  )

(defentity SkillTerm
  (belongs-to Skill {:fk :skillID})
  )


(defn get-skill []
  (first (select Skill (with SkillTerm ))))

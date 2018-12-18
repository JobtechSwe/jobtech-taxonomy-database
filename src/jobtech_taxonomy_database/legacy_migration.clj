(ns jobtech-taxonomy-database.legacy-migration
  (:gen-class)
  (:require
   [korma.db :refer :all]
   [korma.core :refer :all :exclude [update]]))


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
  (first (select Skill (with SkillTerm )
                 (where {:skillID [like 34]}))))



(defn get-skillmainheadlines
  ""
  []
  (exec-raw ["SELECT SkillMainHeadlineTerm.*
FROM TaxonomyDB.dbo.SkillMainHeadlineTerm SkillMainHeadlineTerm
WHERE SkillMainHeadlineTerm.languageID = 502"] :results))


(defn get-skillheadlines
  ""
  [main-headline-id]
  (exec-raw [(format "SELECT SkillHeadline.*, SkillHeadlineTerm.*, SkillMainHeadline.*, SkillMainHeadlineTerm.*
FROM TaxonomyDB.dbo.SkillHeadline SkillHeadline, TaxonomyDB.dbo.SkillHeadlineTerm SkillHeadlineTerm, TaxonomyDB.dbo.SkillMainHeadline SkillMainHeadline, TaxonomyDB.dbo.SkillMainHeadlineTerm SkillMainHeadlineTerm
WHERE
	SkillHeadlineTerm.skillHeadlineID = SkillHeadline.skillHeadlineID
	AND SkillHeadLine.skillMainHeadlineID = SkillMainHeadLine.skillMainHeadlineID
	AND SkillMainHeadline.skillMainHeadlineID = %d
	AND SkillMainHeadlineTerm.skillMainHeadlineID = SkillMainHeadline.skillMainHeadlineID
	AND SkillMainHeadlineTerm.languageID = 502
	AND SkillHeadlineTerm.languageID = 502
ORDER BY SkillHeadline.skillHeadlineID
OFFSET 1 ROWS
 FETCH NEXT 2 ROWS ONLY" main-headline-id)] :results))

;(get-skillheadlines 2)
;(get-skillmainheadlines)


#_(exec-raw ["SELECT SkillMainHeadlineTerm.term AS mainheadline, SkillHeadlineTerm.term AS headline, SkillTerm.term AS term
FROM TaxonomyDB.dbo.Skill Skill, TaxonomyDB.dbo.SkillHeadline SkillHeadline, TaxonomyDB.dbo.SkillHeadlineTerm SkillHeadlineTerm, TaxonomyDB.dbo.SkillMainHeadline SkillMainHeadline, TaxonomyDB.dbo.SkillMainHeadlineTerm SkillMainHeadlineTerm, TaxonomyDB.dbo.SkillTerm SkillTerm
WHERE
	Skill.skillHeadlineID = SkillHeadline.skillHeadlineID
	AND SkillHeadlineTerm.skillHeadlineID = SkillHeadline.skillHeadlineID
	AND SkillHeadline.skillMainHeadlineID = SkillMainHeadline.skillMainHeadlineID
	AND SkillMainHeadlineTerm.skillMainHeadlineID = SkillMainHeadline.skillMainHeadlineID
	AND SkillTerm.skillID = Skill.skillID AND SkillTerm.countryID = Skill.countryID
	AND SkillTerm.languageID = 502
	AND SkillMainHeadlineTerm.languageID = 502
	AND SKillHeadlineTerm.languageID = 502"] :results)

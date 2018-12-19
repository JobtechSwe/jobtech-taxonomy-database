-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name all-skills :*
-- :doc Get all skills in Swedish
SELECT Skill.*, SkillTerm.*
FROM TaxonomyDB.dbo.Skill Skill, TaxonomyDB.dbo.SkillTerm SkillTerm
WHERE
SkillTerm.skillID = Skill.skillID AND SkillTerm.countryID = Skill.countryID
AND languageID = 502



-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-skillheadlines :? :1
-- :doc Get all skills in Swedish wut headlines
SELECT SkillMainHeadlineTerm.term AS mainheadline, SkillHeadlineTerm.term AS headline, SkillTerm.term AS term
FROM TaxonomyDB.dbo.Skill Skill, TaxonomyDB.dbo.SkillHeadline SkillHeadline, TaxonomyDB.dbo.SkillHeadlineTerm SkillHeadlineTerm, TaxonomyDB.dbo.SkillMainHeadline SkillMainHeadline, TaxonomyDB.dbo.SkillMainHeadlineTerm SkillMainHeadlineTerm, TaxonomyDB.dbo.SkillTerm SkillTerm
WHERE
SkillHeadlineTerm.skillHeadlineID = SkillHeadline.skillHeadlineID
AND SkillHeadLine.skillMainHeadlineID = SkillMainHeadLine.skillMainHeadlineID
AND SkillMainHeadline.skillMainHeadlineID = :id
AND SkillMainHeadlineTerm.skillMainHeadlineID = SkillMainHeadline.skillMainHeadlineID
AND SkillMainHeadlineTerm.languageID = 502
AND SkillHeadlineTerm.languageID = 502


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-skillmainheadlines :*
-- :doc Get all skills in Swedish wut headlines
SELECT SkillMainHeadlineTerm.*
FROM TaxonomyDB.dbo.SkillMainHeadlineTerm SkillMainHeadlineTerm
WHERE SkillMainHeadlineTerm.languageID = 502

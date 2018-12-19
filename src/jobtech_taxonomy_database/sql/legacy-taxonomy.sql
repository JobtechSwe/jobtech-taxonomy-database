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
-- :name all-skills-with-headlines :*
-- :doc Get all skills in Swedish wut headlines
SELECT SkillMainHeadlineTerm.term AS mainheadline, SkillHeadlineTerm.term AS headline, SkillTerm.term AS term
FROM TaxonomyDB.dbo.Skill Skill, TaxonomyDB.dbo.SkillHeadline SkillHeadline, TaxonomyDB.dbo.SkillHeadlineTerm SkillHeadlineTerm, TaxonomyDB.dbo.SkillMainHeadline SkillMainHeadline, TaxonomyDB.dbo.SkillMainHeadlineTerm SkillMainHeadlineTerm, TaxonomyDB.dbo.SkillTerm SkillTerm
WHERE
Skill.skillHeadlineID = SkillHeadline.skillHeadlineID
AND SkillHeadlineTerm.skillHeadlineID = SkillHeadline.skillHeadlineID
AND SkillHeadline.skillMainHeadlineID = SkillMainHeadline.skillMainHeadlineID
AND SkillMainHeadlineTerm.skillMainHeadlineID = SkillMainHeadline.skillMainHeadlineID
AND SkillTerm.skillID = Skill.skillID AND SkillTerm.countryID = Skill.countryID
AND SkillTerm.languageID = 502
AND SkillMainHeadlineTerm.languageID = 502
AND SKillHeadlineTerm.languageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name all-skills :*
-- :doc Get all skills in Swedish
SELECT Skill.*, SkillTerm.*
FROM TaxonomyDB.dbo.Skill Skill, TaxonomyDB.dbo.SkillTerm SkillTerm
WHERE SkillTerm.skillID = Skill.skillID
AND SkillTerm.countryID = Skill.countryID
AND languageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned.
-- TODO: check if this is still used. In the latest incarnation of the skill
-- converter, this is not used.0
-- :name get-skillheadlines-backend :? :1
-- :doc Get all skills in Swedish wut headlines
SELECT SkillMainHeadlineTerm.term AS mainheadline,
  SkillHeadlineTerm.term AS headline,
  SkillTerm.term AS term
FROM TaxonomyDB.dbo.Skill Skill,
  TaxonomyDB.dbo.SkillHeadline SkillHeadline,
  TaxonomyDB.dbo.SkillHeadlineTerm SkillHeadlineTerm,
  TaxonomyDB.dbo.SkillMainHeadline SkillMainHeadline,
  TaxonomyDB.dbo.SkillMainHeadlineTerm SkillMainHeadlineTerm,
  TaxonomyDB.dbo.SkillTerm SkillTerm
WHERE SkillHeadlineTerm.skillHeadlineID = SkillHeadline.skillHeadlineID
AND SkillHeadLine.skillMainHeadlineID = SkillMainHeadLine.skillMainHeadlineID
AND SkillMainHeadline.skillMainHeadlineID = :id
AND SkillMainHeadlineTerm.skillMainHeadlineID = SkillMainHeadline.skillMainHeadlineID
AND SkillMainHeadlineTerm.languageID = 502
AND SkillHeadlineTerm.languageID = 502



-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned.
-- TODO: check if this is still used. In the latest incarnation of the skill
-- converter, this is not used.0
-- Right now it uses a limit of 10 (in the weird Microsoft SQL syntax), as
-- it takes ages to retrieve all headlines from the database.
-- :name get-skillheadlines-converter-backend :? :1
-- :doc Get headline skills connected to a main headline. Used by the skill converter.
SELECT SkillHeadline.*, SkillHeadlineTerm.*, SkillMainHeadline.*, SkillMainHeadlineTerm.*
FROM TaxonomyDB.dbo.SkillHeadline SkillHeadline, TaxonomyDB.dbo.SkillHeadlineTerm SkillHeadlineTerm, TaxonomyDB.dbo.SkillMainHeadline SkillMainHeadline, TaxonomyDB.dbo.SkillMainHeadlineTerm SkillMainHeadlineTerm
WHERE
	SkillHeadlineTerm.skillHeadlineID = SkillHeadline.skillHeadlineID
	AND SkillHeadLine.skillMainHeadlineID = SkillMainHeadLine.skillMainHeadlineID
	AND SkillMainHeadline.skillMainHeadlineID = :id
	AND SkillMainHeadlineTerm.skillMainHeadlineID = SkillMainHeadline.skillMainHeadlineID
	AND SkillMainHeadlineTerm.languageID = 502
	AND SkillHeadlineTerm.languageID = 502
ORDER BY SkillHeadline.skillHeadlineID
OFFSET 1 ROWS
FETCH NEXT 10 ROWS ONLY


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-skillmainheadlines-backend :*
-- :doc Get all skills in Swedish wut headlines
SELECT SkillMainHeadlineTerm.*
FROM TaxonomyDB.dbo.SkillMainHeadlineTerm SkillMainHeadlineTerm
WHERE SkillMainHeadlineTerm.languageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-language :*
-- :doc Get all langages
SELECT Language.*, LanguageTerm.*
FROM TaxonomyDB.dbo.[Language] Language, TaxonomyDB.dbo.LanguageTerm LanguageTerm
WHERE LanguageTerm.translationLanguageID = 502
AND Language.languageID = 502
-- Todo continue with language after getting input from Rita

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-language-level :*
-- :doc Get all langage-levels
SELECT LanguageLevel.*, LanguageLevelTerm.*
FROM TaxonomyDB.dbo.LanguageLevel LanguageLevel, TaxonomyDB.dbo.LanguageLevelTerm LanguageLevelTerm
WHERE LanguageLevelTerm.languageLevelID = LanguageLevel.languageLevelID
AND LanguageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-worktime-extent :*
-- :doc Get all arbetstid
SELECT Arbetstid.*, ArbetstidTerm.*
FROM TaxonomiDBSvensk.dbo.Arbetstid Arbetstid, TaxonomiDBSvensk.dbo.ArbetstidTerm ArbetstidTerm
WHERE ArbetstidTerm.arbetstidsID = Arbetstid.arbetstidsID

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-country :*
-- :doc Get all countries
SELECT Country.*, CountryTerm.*
FROM TaxonomyDB.dbo.Country Country, TaxonomyDB.dbo.CountryTerm CountryTerm
WHERE CountryTerm.countryID = Country.countryID
AND LanguageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-continents :*
-- :doc Get all continents
SELECT Continent.*, ContinentTerm.*
FROM TaxonomyDB.dbo.Continent Continent, TaxonomyDB.dbo.ContinentTerm ContinentTerm
WHERE ContinentTerm.continentID = Continent.continentID
AND LanguageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-drivers-licenses :*
-- :doc Get all driver's license categories
SELECT DrivingLicence.*, DrivingLicenceTerm.*
FROM TaxonomyDB.dbo.DrivingLicence DrivingLicence, TaxonomyDB.dbo.DrivingLicenceTerm DrivingLicenceTerm
WHERE DrivingLicenceTerm.drivingLicenceID = DrivingLicence.drivingLicenceID
AND LanguageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-employment-duration :*
-- :doc Get all employment durations
SELECT Anställningsvaraktighet.*, AnställningsvaraktighetTerm.*
FROM TaxonomyDBSvensk.dbo.Anställningsvaraktighet Anställningsvaraktighet,
     TaxonomyDBSvensk.dbo.AnställningsvaraktighetTerm AnställningsvaraktighetTerm
WHERE AnställningsvaraktighetTerm.anställningsvaraktighetsID = Anställningsvaraktighet.anställningsvaraktighetsID


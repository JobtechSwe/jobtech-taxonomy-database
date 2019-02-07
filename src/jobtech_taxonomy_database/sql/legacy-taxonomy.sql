-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-skill-mainheadlines :*
-- :doc Get all main headline skills in Swedish
SELECT SkillMainHeadlineTerm.skillMainHeadlineID AS main_id,
       SkillMainHeadlineTerm.term AS main_term,
       SkillMainHeadlineTerm.languageID AS lang,
       SkillMainHeadlineTerm.modificationDate AS main_date
FROM   TaxonomyDB.dbo.SkillMainHeadlineTerm SkillMainHeadlineTerm
WHERE  SkillMainHeadlineTerm.languageID = 502



-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned.
-- TODO: check if this is still used. In the latest incarnation of the skill
-- converter, this is not used.0
-- Right now it uses a limit of 10 (in the weird Microsoft SQL syntax), as
-- it takes ages to retrieve all headlines from the database.
-- :name get-skill-headlines :?
-- :doc Get headline skills connected to a main headline. Used by the skill converter.
SELECT SkillHeadlineTerm.skillHeadlineID as head_id, SkillHeadlineTerm.term AS head_term, SkillHeadlineTerm.languageID AS lang
FROM TaxonomyDB.dbo.SkillHeadline SkillHeadline, TaxonomyDB.dbo.SkillHeadlineTerm SkillHeadlineTerm, TaxonomyDB.dbo.SkillMainHeadline SkillMainHeadline
WHERE
	SkillHeadlineTerm.skillHeadlineID = SkillHeadline.skillHeadlineID
	AND SkillHeadline.skillMainHeadlineID = :id
	AND SkillHeadlineTerm.languageID = 502


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-skills-for-headline :?
-- :doc Get all skills that belong to the given headline
SELECT Skill.skillID AS skill_id
FROM   TaxonomyDB.dbo.Skill Skill, TaxonomyDB.dbo.SkillHeadline SkillHeadline, TaxonomyDB.dbo.SkillHeadlineTerm SkillHeadlineTerm
WHERE
	Skill.skillHeadlineID = SkillHeadline.skillHeadlineID
	AND SkillHeadlineTerm.skillHeadlineID = SkillHeadline.skillHeadlineID
	AND SkillHeadline.SkillHeadlineID = :id
	AND SkillHeadlineTerm.languageID = 502


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-prefered-skill-term :?
-- :doc Get the prefered skill term that belong to the given skill (in Swedish, FIXME)
SELECT SkillTerm.term AS term,
       SkillTerm.languageID AS lang,
       SkillTerm.skillID AS skill_id
FROM TaxonomyDB.dbo.SkillTerm SkillTerm, TaxonomyDB.dbo.Skill
WHERE
	SkillTerm.skillID = :id
        AND SkillTerm.skillID = Skill.skillID
	AND SkillTerm.countryID = Skill.countryID
	AND SkillTerm.languageID = 502


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-referenced-skill-terms :?
-- :doc Get the referenced skill terms that belong to the given skill
SELECT SkillReference.term AS ref_term
FROM TaxonomyDB.dbo.SkillReference SkillReference, TaxonomyDB.dbo.Skill Skill
WHERE
	SKillReference.countryIDRef = SKill.countryID
	AND SKillReference.skillIDRef = SKill.skillID
	AND Skill.skillID = :id

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-language :*
-- :doc Get all langages
SELECT Language.*, LanguageTerm.*
FROM TaxonomyDB.dbo.[Language] Language, TaxonomyDB.dbo.LanguageTerm LanguageTerm
WHERE LanguageTerm.translationLanguageID = 502
AND Language.languageID = 502

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
-- :doc Get all worktime extents
SELECT Arbetstid.*, ArbetstidTerm.*
FROM TaxonomyDBSvensk.dbo.Arbetstid Arbetstid, TaxonomyDBSvensk.dbo.ArbetstidTerm ArbetstidTerm
WHERE ArbetstidTerm.arbetstidsID = Arbetstid.arbetstidsID



-- START Continents, Countries, Regions, other geographic places --

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-continent :*
-- :doc Get all continents
SELECT Continent.continentID AS continent_id,
		ContinentTerm.term AS continent_term
FROM TaxonomyDB.dbo.Continent Continent, TaxonomyDB.dbo.ContinentTerm ContinentTerm
WHERE ContinentTerm.continentID = Continent.continentID
AND LanguageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-country-for-continent :*
-- :doc Get all countries that belong to the given continent ID
SELECT Country.countryID AS country_id, CountryTerm.term as country_term
FROM TaxonomyDB.dbo.Country Country, TaxonomyDB.dbo.CountryTerm CountryTerm, TaxonomyDB.dbo.Continent Continent
WHERE Country.CountryID = CountryTerm.CountryID
AND Country.ContinentID = :id
AND Continent.ContinentID = :id
AND LanguageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-country :*
-- :doc Get all countries
SELECT Country.*, CountryTerm.*
FROM TaxonomyDB.dbo.Country Country, TaxonomyDB.dbo.CountryTerm CountryTerm
WHERE CountryTerm.countryID = Country.countryID
AND LanguageID = 502
--TODO dont add "sort", (i NUTSCode är NULL samma som inget)

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-nut-code :*
-- :doc Get all NUT level 3 codes for EU regions
SELECT EURegion.*, EURegionTerm.*
FROM TaxonomyDB.dbo.EURegion EURegion, TaxonomyDB.dbo.EURegionTerm EURegionTerm
WHERE EURegionTerm.EURegionID = EURegion.EURegionID
AND LanguageID = 502

-- END Continents, Countries, Regions, other geographic places --



-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-drivers-license :*
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

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-employment-type :*
-- :doc Get all employment types ;
SELECT AnstallningTypJobb.*, AnstallningTypJobbTerm.*
FROM TaxonomyDBSvensk.dbo.AnstallningTypJobb AnstallningTypJobb, TaxonomyDBSvensk.dbo.AnstallningTypJobbTerm AnstallningTypJobbTerm
WHERE AnstallningTypJobb.AnstallningTypJobbID = AnstallningTypJobbTerm.AnstallningTypJobbID

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-wage-type :*
-- :doc Get all wage types ;
SELECT Löneform.*, LöneformTerm.*
FROM TaxonomyDBSvensk.dbo.Löneform Löneform, TaxonomyDBSvensk.dbo.LöneformTerm LöneformTerm
WHERE LöneformTerm.löneformsID = Löneform.löneformsID

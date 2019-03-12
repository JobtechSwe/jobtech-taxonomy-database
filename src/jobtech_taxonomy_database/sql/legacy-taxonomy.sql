-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-skill-mainheadlines :*
-- :doc Get all main headline skills in Swedish
SELECT SkillMainHeadlineTerm.skillMainHeadlineID AS main_id,
       SkillMainHeadlineTerm.term AS main_term,
       SkillMainHeadlineTerm.languageID AS lang,
       SkillMainHeadlineTerm.modificationDate AS main_date
FROM   TaxonomyDBVersion.dbo.SkillMainHeadlineTerm SkillMainHeadlineTerm
WHERE  SkillMainHeadlineTerm.languageID = 502
       AND SkillMainHeadlineTerm.versionID = 67


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned.
-- TODO: check if this is still used. In the latest incarnation of the skill
-- converter, this is not used.0
-- Right now it uses a limit of 10 (in the weird Microsoft SQL syntax), as
-- it takes ages to retrieve all headlines from the database.
-- :name get-skill-headlines :?
-- :doc Get headline skills connected to a main headline. Used by the skill converter.
SELECT SkillHeadlineTerm.skillHeadlineID as head_id,
       SkillHeadlineTerm.term AS head_term,
       SkillHeadlineTerm.languageID AS lang
FROM TaxonomyDBVersion.dbo.SkillHeadline SkillHeadline, TaxonomyDBVersion.dbo.SkillHeadlineTerm SkillHeadlineTerm, TaxonomyDBVersion.dbo.SkillMainHeadline SkillMainHeadline
WHERE
	SkillHeadlineTerm.skillHeadlineID = SkillHeadline.skillHeadlineID
	AND SkillHeadline.skillMainHeadlineID = :id
	AND SkillHeadlineTerm.languageID = 502
        AND SkillHeadlineTerm.versionID = 67
        AND SkillHeadline.versionID = 67
        AND SkillMainHeadline.versionID = 67


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-skills-for-headline :?
-- :doc Get all skills that belong to the given headline
SELECT Skill.skillID AS skill_id
FROM   TaxonomyDBVersion.dbo.Skill Skill, TaxonomyDBVersion.dbo.SkillHeadline SkillHeadline, TaxonomyDBVersion.dbo.SkillHeadlineTerm SkillHeadlineTerm
WHERE
	Skill.skillHeadlineID = SkillHeadline.skillHeadlineID
	AND SkillHeadlineTerm.skillHeadlineID = SkillHeadline.skillHeadlineID
	AND SkillHeadline.SkillHeadlineID = :id
	AND SkillHeadlineTerm.languageID = 502
        AND SkillHeadlineTerm.versionID = 67
        AND SkillHeadline.versionID = 67
        AND Skill.versionID = 67


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-prefered-skill-term :?
-- :doc Get the prefered skill term that belong to the given skill (in Swedish, FIXME)
SELECT SkillTerm.term AS term,
       SkillTerm.languageID AS lang,
       SkillTerm.skillID AS skill_id
FROM TaxonomyDBVersion.dbo.SkillTerm SkillTerm, TaxonomyDBVersion.dbo.Skill
WHERE
	SkillTerm.skillID = :id
        AND SkillTerm.skillID = Skill.skillID
	AND SkillTerm.countryID = Skill.countryID
	AND SkillTerm.languageID = 502
        AND Skill.versionID = 67
        AND SkillTerm.versionID = 67


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-referenced-skill-terms :?
-- :doc Get the referenced skill terms that belong to the given skill
SELECT SkillReference.term AS term
FROM TaxonomyDBVersion.dbo.SkillReference SkillReference, TaxonomyDBVersion.dbo.Skill Skill
WHERE
	SKillReference.countryIDRef = SKill.countryID
	AND SKillReference.skillIDRef = SKill.skillID
	AND Skill.skillID = :id
        AND Skill.versionID = 67
        AND SkillReference.versionID = 67

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-language :*
-- :doc Get all langages
SELECT Language.*, LanguageTerm.*
FROM TaxonomyDBVersion.dbo.[Language] Language, TaxonomyDBVersion.dbo.LanguageTerm LanguageTerm
WHERE LanguageTerm.translationLanguageID = 502
AND Language.languageID = 502
AND Language.versionID = 67
AND LanguageTerm.versionID = 67


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-language-level :*
-- :doc Get all langage-levels
SELECT LanguageLevel.*, LanguageLevelTerm.*
FROM TaxonomyDBVersion.dbo.LanguageLevel LanguageLevel, TaxonomyDBVersion.dbo.LanguageLevelTerm LanguageLevelTerm
WHERE LanguageLevelTerm.languageLevelID = LanguageLevel.languageLevelID
AND LanguageID = 502
AND LanguageLevel.versionID = 67
AND LanguageLevelTerm.versionID = 67


-- A ":result" value of ":*" specifies a vector of records
-- NOTA BENE: the database contains versionID 1, not 67.
-- (as hashmaps) will be returned
-- :name get-worktime-extent :*
-- :doc Get all worktime extents
SELECT Arbetstid.*, ArbetstidTerm.*
FROM TaxonomyDBSvenskVersion.dbo.Arbetstid Arbetstid, TaxonomyDBSvensk.dbo.ArbetstidTerm ArbetstidTerm
WHERE ArbetstidTerm.arbetstidsID = Arbetstid.arbetstidsID
AND Arbetstid.versionID = 1
AND ArbetstidTerm.versionID = 1


------------------START EVEN NEWER geographic taxonomy--------------------

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-continents :*
-- :doc Get all continents
SELECT Continent.continentID AS [id],
	ContinentTerm.term AS [term]
FROM TaxonomyDBVersion.dbo.Continent Continent, TaxonomyDBVersion.dbo.ContinentTerm ContinentTerm
WHERE ContinentTerm.versionID = 67
AND Continent.versionID = 67
AND Continent.continentID = ContinentTerm.continentID

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-countries :*
-- :doc Get all countries
SELECT Country.continentID AS [parent-id],
	Country.countryID AS [id],
	CountryTerm.term AS [term],
	Country.countryCode AS [code]
FROM TaxonomyDBVersion.dbo.Country Country, TaxonomyDBVersion.dbo.CountryTerm CountryTerm
WHERE Country.versionID = 67
AND	CountryTerm.versionID = 67
AND Country.countryID = CountryTerm.countryID
AND languageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-EU-regions :*
-- :doc Get all EU regions
SELECT EURegion.countryID AS [parent-id],
	EURegion.EURegionID AS [id],
	EURegionTerm.term AS [term],
	EURegion.NUTSCodeLevel3 AS [code]
FROM TaxonomyDBVersion.dbo.EURegion EURegion, TaxonomyDBVersion.dbo.EURegionTerm EURegionTerm
WHERE EURegion.versionID = EURegionTerm.versionID
AND EURegion.EURegionID = EURegionTerm.EURegionID
AND EURegionTerm.languageID = 502
AND	EURegionTerm.versionID = 67
AND EURegion.versionID = 67

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-municipalities :*
-- :doc Get all municipalities
SELECT Municipality.EURegionID AS [parent-id],
    Municipality.municipalityID AS [id],
	MunicipalityTerm.term AS [term]
FROM TaxonomyDBVersion.dbo.Municipality Municipality, TaxonomyDBVersion.dbo.MunicipalityTerm MunicipalityTerm
WHERE Municipality.versionID = MunicipalityTerm.versionID
AND Municipality.municipalityID = MunicipalityTerm.municipalityID
AND	MunicipalityTerm.versionID = 67
AND Municipality.versionID = 67



------------------START NEW geogarphic taxonomy--------------------

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-geographic-places :*
-- :doc Get all continents. countries and EU regions, aka NUTS Code level 3
SELECT Continent.continentID AS [continent-id],
		ContinentTerm.term AS [continent-term],
		NULL AS [country-id],
		NULL AS [country-term],
		NULL AS [country-code],
		NULL AS [region-eu-id],
		NULL AS [region-nuts-code-level-3],
		NULL AS [region-eu-term]
FROM TaxonomyDBVersion.dbo.Continent Continent, TaxonomyDBVersion.dbo.ContinentTerm ContinentTerm
WHERE
	ContinentTerm.continentID = Continent.continentID
	AND Continent.continentID NOT IN (SELECT ContinentID FROM TaxonomyDBVersion.dbo.Country)
	AND ContinentTerm.languageID = 502
UNION
SELECT Continent.continentID AS [continent-id],
		ContinentTerm.term AS [continent-term],
		Country.countryID AS [country-id],
		CountryTerm.term AS [country-term],
		Country.countryCode AS [country-code],
		NULL AS [region-eu-id],
		NULL AS [region-nuts-code-level-3],
		NULL AS [region-eu-term]
FROM TaxonomyDBVersion.dbo.Continent Continent,
    TaxonomyDBVersion.dbo.ContinentTerm ContinentTerm,
    TaxonomyDBVersion.dbo.Country Country,
    TaxonomyDBVersion.dbo.CountryTerm CountryTerm
WHERE
	ContinentTerm.continentID = Continent.continentID
	AND Country.continentID = Continent.continentID
	AND CountryTerm.countryID = Country.countryID
	AND CountryTerm.countryID NOT IN (SELECT countryID FROM TaxonomyDBVersion.dbo.EURegion)
	AND ContinentTerm.languageID = 502
    AND CountryTerm.languageID = 502
UNION
SELECT Continent.continentID AS [continent-id],
		ContinentTerm.term AS [continent-term],
		Country.countryID AS [country-id],
		CountryTerm.term AS [country-term],
		Country.countryCode AS [country-code],
		EURegion.EURegionID AS [region-eu-id],
		EURegion.NUTSCodeLevel3 AS [region-nuts-code-level-3],
		EURegionTerm.term AS [region-eu-term]
FROM TaxonomyDBVersion.dbo.Continent Continent,
	TaxonomyDBVersion.dbo.ContinentTerm ContinentTerm,
	TaxonomyDBVersion.dbo.Country Country,
	TaxonomyDBVersion.dbo.CountryTerm CountryTerm,
	TaxonomyDBVersion.dbo.EURegion EURegion,
	TaxonomyDBVersion.dbo.EURegionTerm EURegionTerm
WHERE
	ContinentTerm.continentID = Continent.continentID
	AND Country.continentID = Continent.continentID
	AND CountryTerm.countryID = Country.countryID
	AND EURegion.countryID = Country.countryID
	AND EURegionTerm.EURegionID = EURegion.EURegionID
	AND ContinentTerm.languageID = 502
    AND CountryTerm.languageID = 502
    AND EURegionTerm.languageID = 502

-- START Continents, Countries, Regions, other geographic places --

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-continent :*
-- :doc Get all continents
SELECT Continent.continentID AS continent_id,
		ContinentTerm.term AS continent_term
FROM TaxonomyDBVersion.dbo.Continent Continent, TaxonomyDBVersion.dbo.ContinentTerm ContinentTerm
WHERE ContinentTerm.continentID = Continent.continentID
AND ContinentTerm.languageID = 502
AND Continent.versionID = 67
AND ContinentTerm.versionID = 67


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-country-for-continent :*
-- :doc Get all countries that belong to the given continent ID
SELECT Country.countryID AS country_id,
       CountryTerm.term as country_term
FROM TaxonomyDBVersion.dbo.Country Country, TaxonomyDBVersion.dbo.CountryTerm CountryTerm, TaxonomyDBVersion.dbo.Continent Continent
WHERE Country.CountryID = CountryTerm.CountryID
AND Country.ContinentID = :id
AND Continent.ContinentID = :id
AND LanguageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-country :*
-- :doc Get all countries
SELECT Country.*, CountryTerm.*
FROM TaxonomyDBVersion.dbo.Country Country, TaxonomyDBVersion.dbo.CountryTerm CountryTerm
WHERE CountryTerm.countryID = Country.countryID
AND LanguageID = 502
AND Country.versionID = 67
AND CountryTerm.versionID = 67
--TODO dont add "sort", (i NUTSCode är NULL samma som inget)


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-eu-region :*
-- :doc Get all NUT level 3 codes for EU regions
SELECT EURegion.NUTSCodeLevel3 as region-id,
	   EURegionTerm.term as region-term
FROM TaxonomyDBVersion.dbo.EURegion EURegion, TaxonomyDBVersion.dbo.EURegionTerm EURegionTerm
WHERE EURegion.countryID = :id
AND EURegionTerm.EURegionID = EURegion.EURegionID
AND LanguageID = 502


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-nuts-code :*
-- :doc Get all NUTS level 3 codes for EU regions
SELECT EURegion.*, EURegionTerm.*
FROM TaxonomyDBVersion.dbo.EURegion EURegion, TaxonomyDBVersion.dbo.EURegionTerm EURegionTerm
WHERE EURegionTerm.EURegionID = EURegion.EURegionID
AND LanguageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-drivers-license :*
-- :doc Get all driver's license categories
SELECT DrivingLicence.*, DrivingLicenceTerm.*
FROM TaxonomyDBVersion.dbo.DrivingLicence DrivingLicence, TaxonomyDBVersion.dbo.DrivingLicenceTerm DrivingLicenceTerm
WHERE DrivingLicenceTerm.drivingLicenceID = DrivingLicence.drivingLicenceID
AND LanguageID = 502
AND DrivingLicence.versionID = 67
AND DrivingLicenceTerm.versionID = 67


-- A ":result" value of ":*" specifies a vector of records
-- NOTA BENE: the database contains versionID 1, not 67.
-- (as hashmaps) will be returned
-- :name get-employment-duration :*
-- :doc Get all employment durations
SELECT Anställningsvaraktighet.*, AnställningsvaraktighetTerm.*
FROM TaxonomyDBSvensk.dbo.Anställningsvaraktighet Anställningsvaraktighet,
     TaxonomyDBSvensk.dbo.AnställningsvaraktighetTerm AnställningsvaraktighetTerm
WHERE AnställningsvaraktighetTerm.anställningsvaraktighetsID = Anställningsvaraktighet.anställningsvaraktighetsID
AND Anställningsvaraktighet.versionID = 1
AND AnställningsvaraktighetTerm.versionID = 1


-- A ":result" value of ":*" specifies a vector of records
-- NOTA BENE: the database contains versionID 1, not 67.
-- (as hashmaps) will be returned
-- :name get-employment-type :*
-- :doc Get all employment types ;
SELECT AnstallningTypJobb.*, AnstallningTypJobbTerm.*
FROM TaxonomyDBSvensk.dbo.AnstallningTypJobb AnstallningTypJobb, TaxonomyDBSvensk.dbo.AnstallningTypJobbTerm AnstallningTypJobbTerm
WHERE AnstallningTypJobb.AnstallningTypJobbID = AnstallningTypJobbTerm.AnstallningTypJobbID
AND AnstallningTypJobb.versionID = 1
AND AnstallningTypJobbTerm.versionID = 1


-- A ":result" value of ":*" specifies a vector of records
-- NOTA BENE: the database contains versionID 1, not 67.
-- (as hashmaps) will be returned
-- :name get-wage-type :*
-- :doc Get all wage types ;
SELECT Löneform.*, LöneformTerm.*
FROM TaxonomyDBSvensk.dbo.Löneform Löneform, TaxonomyDBSvensk.dbo.LöneformTerm LöneformTerm
WHERE LöneformTerm.löneformsID = Löneform.löneformsID
AND Löneform.versionID = 1
AND LöneformTerm.versionID = 1


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-occupation-name :*
-- :doc Get all occupation names ;
SELECT OccupationName.*, OccupationNameTerm.*
FROM TaxonomyDBVersion.dbo.OccupationName OccupationName, TaxonomyDBVersion.dbo.OccupationNameTerm OccupationNameTerm
WHERE   OccupationName.versionID = OccupationNameTerm.versionID
AND	OccupationName.occupationNameID = OccupationNameTerm.occupationNameID
AND	OccupationName.countryID = OccupationNameTerm.countryID
AND     OccupationName.versionID = 67
AND     OccupationNameTerm.languageID = 502


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-occupation-group-ssyk :*
-- :doc Get all ssyk, dont confuse this one with ISCO ;
SELECT LocaleGroup.*, LocaleGroupTerm.*
FROM TaxonomyDBVersion.dbo.LocaleGroup LocaleGroup, TaxonomyDBVersion.dbo.LocaleGroupTerm LocaleGroupTerm
WHERE
LocaleGroup.versionID = LocaleGroupTerm.versionID
AND LocaleGroup.localeGroupID = LocaleGroupTerm.localeGroupID
AND LocaleGroup.versionID = 67
AND LocaleGroupTerm.languageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-occupation-field :*
-- :doc Get yrkesomraden occupation-field ;
SELECT LocaleField.*, LocaleFieldTerm.*
FROM TaxonomyDBVersion.dbo.LocaleField LocaleField, TaxonomyDBVersion.dbo.LocaleFieldTerm LocaleFieldTerm
WHERE
LocaleField.versionID = LocaleFieldTerm.versionID
AND LocaleField.localeFieldID = LocaleFieldTerm.localeFieldID
AND LocaleField.versionID = 67
AND LocaleFieldTerm.languageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-isco-level-4 :*
-- :doc Get isco level 4 ;
SELECT OccupationGroup.*, OccupationGroupTerm.*
FROM TaxonomyDBVersion.dbo.OccupationGroup OccupationGroup, TaxonomyDBVersion.dbo.OccupationGroupTerm OccupationGroupTerm
WHERE
OccupationGroup.versionID = OccupationGroupTerm.versionID
AND OccupationGroup.occupationGroupID = OccupationGroupTerm.occupationGroupID
AND OccupationGroupTerm.languageID = 502
AND OccupationGroup.versionID = 67

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-ssyk-skill-relation:*
-- :doc get ssyk skill relation ;
SELECT versionID, skillID, countryID, localeGroupID, modificationDate
FROM TaxonomyDBVersion.dbo.LocaleGroup_Skill
WHERE TaxonomyDBVersion.dbo.LocaleGroup_Skill.versionID = 67

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-occupation-name-affinity:*
-- :doc get affinity relations between occupations ;
SELECT AffinityRate.*, OccupationNameAffinity.*
FROM TaxonomyDBVersion.dbo.AffinityRate AffinityRate, TaxonomyDBVersion.dbo.OccupationNameAffinity OccupationNameAffinity
WHERE
AffinityRate.versionID = OccupationNameAffinity.versionID
AND AffinityRate.affinityRateID = OccupationNameAffinity.affinityRateID



-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-occupation-collection:*
-- :doc get occupation collection ;
SELECT CollectionOccupation.*, OccupationCollection.*
FROM TaxonomyDBVersion.dbo.CollectionOccupation CollectionOccupation, TaxonomyDBVersion.dbo.OccupationCollection OccupationCollection
WHERE
OccupationCollection.versionID = CollectionOccupation.versionID
AND
OccupationCollection.collectionID = CollectionOccupation.collectionID

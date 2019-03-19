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
FROM TaxonomyDBSvenskVersion.dbo.Arbetstid Arbetstid,
     TaxonomyDBSvenskVersion.dbo.ArbetstidTerm ArbetstidTerm
WHERE ArbetstidTerm.arbetstidsID = Arbetstid.arbetstidsID
AND Arbetstid.versionID = 1
AND ArbetstidTerm.versionID = 1


------------------START geographic places--------------------

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-continents :*
-- :doc Get all continents
SELECT Continent.continentID AS [id],
	ContinentTerm.term AS [term]
FROM TaxonomyDBVersion.dbo.Continent Continent,
    TaxonomyDBVersion.dbo.ContinentTerm ContinentTerm
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
FROM TaxonomyDBVersion.dbo.Country Country,
    TaxonomyDBVersion.dbo.CountryTerm CountryTerm
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
FROM TaxonomyDBVersion.dbo.EURegion EURegion,
    TaxonomyDBVersion.dbo.EURegionTerm EURegionTerm
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
FROM TaxonomyDBVersion.dbo.Municipality Municipality,
    TaxonomyDBVersion.dbo.MunicipalityTerm MunicipalityTerm
WHERE Municipality.versionID = MunicipalityTerm.versionID
AND Municipality.municipalityID = MunicipalityTerm.municipalityID
AND	MunicipalityTerm.versionID = 67
AND Municipality.versionID = 67

------------------END geographic places--------------------

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
FROM TaxonomyDBSvenskVersion.dbo.Anställningsvaraktighet Anställningsvaraktighet,
     TaxonomyDBSvenskVersion.dbo.AnställningsvaraktighetTerm AnställningsvaraktighetTerm
WHERE AnställningsvaraktighetTerm.anställningsvaraktighetsID = Anställningsvaraktighet.anställningsvaraktighetsID
AND Anställningsvaraktighet.versionID = 1
AND AnställningsvaraktighetTerm.versionID = 1


-- A ":result" value of ":*" specifies a vector of records
-- NOTA BENE: the database contains versionID 1, not 67.
-- (as hashmaps) will be returned
-- :name get-employment-type :*
-- :doc Get all employment types ;
SELECT AnstallningTypJobb.*, AnstallningTypJobbTerm.*
FROM TaxonomyDBSvenskVersion.dbo.AnstallningTypJobb AnstallningTypJobb,
     TaxonomyDBSvenskVersion.dbo.AnstallningTypJobbTerm AnstallningTypJobbTerm
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


-- :name get-ssyk-level-1 :*
-- :doc Get ssyk level 1 ;
SELECT LocaleLevel1.*, LocaleLevel1Term.*
FROM TaxonomyDBVersion.dbo.LocaleLevel1 LocaleLevel1, TaxonomyDBVersion.dbo.LocaleLevel1Term LocaleLevel1Term
WHERE LocaleLevel1.versionID = LocaleLevel1Term.versionID
AND LocaleLevel1.localeLevel1ID = LocaleLevel1Term.localeLevel1ID
AND LocaleLevel1Term.languageID = 502
AND LocaleLevel1Term.versionID = 67


-- :name get-ssyk-level-2 :*
-- :doc Get ssyk level 2 ;
SELECT LocaleLevel2.*, LocaleLevel2Term.*
FROM TaxonomyDBVersion.dbo.LocaleLevel2 LocaleLevel2, TaxonomyDBVersion.dbo.LocaleLevel2Term LocaleLevel2Term
WHERE LocaleLevel2.versionID = LocaleLevel2Term.versionID
AND LocaleLevel2.localeLevel2ID = LocaleLevel2Term.localeLevel2ID
AND LocaleLevel2Term.languageID = 502
AND LocaleLevel2Term.versionID = 67

-- :name get-ssyk-level-3 :*
-- :doc Get ssyk level 3 ;
SELECT LocaleLevel3.*, LocaleLevel3Term.*
FROM TaxonomyDBVersion.dbo.LocaleLevel3 LocaleLevel3, TaxonomyDBVersion.dbo.LocaleLevel3Term LocaleLevel3Term
WHERE LocaleLevel3.versionID = LocaleLevel3Term.versionID
AND LocaleLevel3.localeLevel3ID = LocaleLevel3Term.localeLevel3ID
AND LocaleLevel3Term.languageID = 502
AND LocaleLevel3Term.versionID = 67

-- :name get-isco-level-4 :*
-- :doc Get isco level 4 ;
SELECT OccupationGroup.*, OccupationGroupTerm.*
FROM TaxonomyDBVersion.dbo.OccupationGroup OccupationGroup, TaxonomyDBVersion.dbo.OccupationGroupTerm OccupationGroupTerm
WHERE OccupationGroup.versionID = OccupationGroupTerm.versionID
AND OccupationGroup.occupationGroupID = OccupationGroupTerm.occupationGroupID
AND OccupationGroupTerm.languageID = 502
AND OccupationGroup.versionID = 67


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-ssyk-skill-relation :*
-- :doc get ssyk skill relation ;
SELECT versionID, skillID, countryID, localeGroupID, modificationDate
FROM TaxonomyDBVersion.dbo.LocaleGroup_Skill
WHERE TaxonomyDBVersion.dbo.LocaleGroup_Skill.versionID = 67

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-occupation-name-affinity :*
-- :doc get affinity relations between occupations ;
SELECT AffinityRate.*, OccupationNameAffinity.*
FROM TaxonomyDBVersion.dbo.AffinityRate AffinityRate, TaxonomyDBVersion.dbo.OccupationNameAffinity OccupationNameAffinity
WHERE AffinityRate.versionID = OccupationNameAffinity.versionID
AND AffinityRate.affinityRateID = OccupationNameAffinity.affinityRateID
AND OccupationNameAffinity.versionID = 67


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-occupation-collection :*
-- :doc get occupation collection ;
SELECT versionID, collectionID, collectionsetID, name, modificationDate
FROM TaxonomyDBVersion.dbo.OccupationCollection
WHERE versionID = 67;


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-occupation-collection-relations :*
-- :doc get occupation collection relations ;
SELECT versionID, collectionID, occupationNameID, countryID, modificationDate
FROM TaxonomyDBVersion.dbo.CollectionOccupation
WHERE versionID = 67;



-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-occupation-names-reference :*
-- :doc get occupation names that have been replaced by a newer occupation  ;
SELECT versionID, occupationNameID, countryID, term, standard, locale, occupationNameIDRef, countryIDRef, modificationDate
FROM TaxonomyDBVersion.dbo.OccupationNameReference
WHERE versionID = 67;

-- :name get-popular-synonym :*
-- :doc ge popular synonyms ;
SELECT versionID, popularSynonymID, term, modificationDate
FROM TaxonomyDBVersion.dbo.PopularSynonym
WHERE versionID = 67;

-- :name get-occupation-name-synonym :*
-- :doc get occupation name synonyms ;
SELECT versionID, occupationNameID, countryID, popularSynonymID, modificationDate
FROM TaxonomyDBVersion.dbo.OccupationNameSynonym
WHERE versionID = 67;



------------------START SUN education field--------------------

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-sun-field-1 :*
-- :doc get SUN education field 1 ;
SELECT SUNInriktning1.SUNInriktning1ID AS [id],
    SUNInriktning1.SUNKodInriktning1 AS [code],
    SUNInriktning1.versionID AS [version-id],
    SUNInriktning1Term.beteckning AS [term]
FROM TaxonomyDBSvenskVersion.dbo.SUNInriktning1 SUNInriktning1,
    TaxonomyDBSvenskVersion.dbo.SUNInriktning1Term SUNInriktning1Term
WHERE SUNInriktning1.versionID = SUNInriktning1Term.versionID
AND SUNInriktning1.SUNInriktning1ID = SUNInriktning1Term.SUNInriktning1ID

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-sun-field-2 :*
-- :doc get SUN education field 2 ;
SELECT SUNInriktning2.SUNInriktning1ID AS [parent-id],
    SUNInriktning2.SUNInriktning2ID AS [id],
    SUNInriktning2.SUNKodInriktning2 AS [code],
    SUNInriktning2.versionID AS [version-id],
    SUNInriktning2Term.beteckning AS [term]
FROM TaxonomyDBSvenskVersion.dbo.SUNInriktning2 SUNInriktning2,
    TaxonomyDBSvenskVersion.dbo.SUNInriktning2Term SUNInriktning2Term
WHERE SUNInriktning2.versionID = SUNInriktning2Term.versionID
AND SUNInriktning2.SUNInriktning2ID = SUNInriktning2Term.SUNInriktning2ID

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-sun-field-3 :*
-- :doc get SUN education field 3 ;
SELECT SUNInriktning3.SUNInriktning2ID AS [parent-id],
    SUNInriktning3.SUNInriktning3ID AS [id],
    SUNInriktning3.SUNKodInriktning3 AS [code],
    SUNInriktning3.versionID AS [version-id],
    SUNInriktning3Term.beteckning AS [term]
FROM TaxonomyDBSvenskVersion.dbo.SUNInriktning3 SUNInriktning3,
    TaxonomyDBSvenskVersion.dbo.SUNInriktning3Term SUNInriktning3Term
WHERE SUNInriktning3.versionID = SUNInriktning3Term.versionID
AND SUNInriktning3.SUNInriktning3ID = SUNInriktning3Term.SUNInriktning3ID

------------------END SUN education field--------------------

------------------START SUN education level--------------------
-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-sun-level-1 :*
-- :doc get SUN education level 1 ;
SELECT SUNNivå1.SUNNivå1ID AS [id],
    SUNNivå1.SUNKodNivå1 AS [code],
    SUNNivå1.versionID AS [version-id],
    SUNNivå1Term.beteckning AS [term]
FROM TaxonomyDBSvenskVersion.dbo.SUNNivå1 SUNNivå1,
	TaxonomyDBSvenskVersion.dbo.SUNNivå1Term SUNNivå1Term
WHERE SUNNivå1.versionID = SUNNivå1Term.versionID
	AND SUNNivå1.SUNNivå1ID = SUNNivå1Term.SUNNivå1ID

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-sun-level-2 :*
-- :doc get SUN education level 2 ;
SELECT SUNNivå2.SUNNivå1ID AS [parent-id],
    SUNNivå2.SUNNivå2ID AS [id],
    SUNNivå2.SUNKodNivå2 AS [code],
    SUNNivå2.versionID AS [version-id],
    SUNNivå2Term.beteckning AS [term]
FROM TaxonomyDBSvenskVersion.dbo.SUNNivå2 SUNNivå2,
	TaxonomyDBSvenskVersion.dbo.SUNNivå2Term SUNNivå2Term
WHERE SUNNivå2.versionID = SUNNivå2Term.versionID
AND SUNNivå2.SUNNivå2ID = SUNNivå2Term.SUNNivå2ID

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-sun-level-3 :*
-- :doc get SUN education level 3 ;
SELECT SUNNivå3.SUNNivå2ID AS [parent-id],
    SUNNivå3.SUNNivå3ID AS [id],
    SUNNivå3.SUNKodNivå3 AS [code],
    SUNNivå3.versionID AS [version-id],
    SUNNivå3Term.beteckning AS [term]
FROM TaxonomyDBSvenskVersion.dbo.SUNNivå3 SUNNivå3,
	TaxonomyDBSvenskVersion.dbo.SUNNivå3Term SUNNivå3Term
WHERE SUNNivå3.versionID = SUNNivå3Term.versionID
AND SUNNivå3.SUNNivå3ID = SUNNivå3Term.SUNNivå3ID

------------------END SUN education level--------------------

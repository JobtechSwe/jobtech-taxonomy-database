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
FROM TaxonomiDBSvenskVersion.dbo.Arbetstid Arbetstid,
     TaxonomiDBSvenskVersion.dbo.ArbetstidTerm ArbetstidTerm
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



-- :name get-drivers-license-combination :*
-- :doc Get all driver's license combinations
SELECT Körkortskombination.*, Körkortskoppling.*
FROM TaxonomiDBSvenskVersion.dbo.Körkortskombination Körkortskombination, TaxonomiDBSvenskVersion.dbo.Körkortskoppling Körkortskoppling
WHERE Körkortskoppling.kombinationsID = Körkortskombination.kombinationsID




-- A ":result" value of ":*" specifies a vector of records
-- NOTA BENE: the database contains versionID 1, not 67.
-- (as hashmaps) will be returned
-- :name get-employment-duration :*
-- :doc Get all employment durations
SELECT Anställningsvaraktighet.*, AnställningsvaraktighetTerm.*
FROM TaxonomiDBSvenskVersion.dbo.Anställningsvaraktighet Anställningsvaraktighet,
     TaxonomiDBSvenskVersion.dbo.AnställningsvaraktighetTerm AnställningsvaraktighetTerm
WHERE AnställningsvaraktighetTerm.anställningsvaraktighetsID = Anställningsvaraktighet.anställningsvaraktighetsID
AND Anställningsvaraktighet.versionID = 1
AND AnställningsvaraktighetTerm.versionID = 1


-- A ":result" value of ":*" specifies a vector of records
-- NOTA BENE: the database contains versionID 1, not 67.
-- (as hashmaps) will be returned
-- :name get-employment-type :*
-- :doc Get all employment types ;
SELECT AnstallningTypJobb.*, AnstallningTypJobbTerm.*
FROM TaxonomiDBSvenskVersion.dbo.AnstallningTypJobb AnstallningTypJobb,
     TaxonomiDBSvenskVersion.dbo.AnstallningTypJobbTerm AnstallningTypJobbTerm
WHERE AnstallningTypJobb.AnstallningTypJobbID = AnstallningTypJobbTerm.AnstallningTypJobbID
AND AnstallningTypJobb.versionID = 1
AND AnstallningTypJobbTerm.versionID = 1


-- A ":result" value of ":*" specifies a vector of records
-- NOTA BENE: the database contains versionID 1, not 67.
-- (as hashmaps) will be returned
-- :name get-wage-type :*
-- :doc Get all wage types ;
SELECT Löneform.*, LöneformTerm.*
FROM TaxonomiDBSvenskVersion.dbo.Löneform Löneform, TaxonomiDBSvenskVersion.dbo.LöneformTerm LöneformTerm
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
FROM TaxonomiDBSvenskVersion.dbo.SUNInriktning1 SUNInriktning1,
    TaxonomiDBSvenskVersion.dbo.SUNInriktning1Term SUNInriktning1Term
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
FROM TaxonomiDBSvenskVersion.dbo.SUNInriktning2 SUNInriktning2,
    TaxonomiDBSvenskVersion.dbo.SUNInriktning2Term SUNInriktning2Term
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
FROM TaxonomiDBSvenskVersion.dbo.SUNInriktning3 SUNInriktning3,
    TaxonomiDBSvenskVersion.dbo.SUNInriktning3Term SUNInriktning3Term
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
FROM TaxonomiDBSvenskVersion.dbo.SUNNivå1 SUNNivå1,
	TaxonomiDBSvenskVersion.dbo.SUNNivå1Term SUNNivå1Term
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
FROM TaxonomiDBSvenskVersion.dbo.SUNNivå2 SUNNivå2,
	TaxonomiDBSvenskVersion.dbo.SUNNivå2Term SUNNivå2Term
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
FROM TaxonomiDBSvenskVersion.dbo.SUNNivå3 SUNNivå3,
	TaxonomiDBSvenskVersion.dbo.SUNNivå3Term SUNNivå3Term
WHERE SUNNivå3.versionID = SUNNivå3Term.versionID
AND SUNNivå3.SUNNivå3ID = SUNNivå3Term.SUNNivå3ID

------------------END SUN education level--------------------
---- START NACE LEVEL

-- :name get-sni-level-1 :*
-- :doc get SNI koder level 1 ;
SELECT NaceLevel1.*, NaceLevel1Term.*
FROM TaxonomyDBVersion.dbo.NaceLevel1 NaceLevel1, TaxonomyDBVersion.dbo.NaceLevel1Term NaceLevel1Term
WHERE NaceLevel1.versionID = NaceLevel1Term.versionID
AND NaceLevel1.naceLevel1ID = NaceLevel1Term.naceLevel1ID
AND NaceLevel1.versionID = 67


-- :name get-sni-level-2 :*
-- :doc get SNI koder level 2 ;
SELECT NaceLevel2.*, NaceLevel2Term.*
FROM TaxonomyDBVersion.dbo.NaceLevel2 NaceLevel2, TaxonomyDBVersion.dbo.NaceLevel2Term NaceLevel2Term
WHERE NaceLevel2.versionID = NaceLevel2Term.versionID
AND NaceLevel2.naceLevel2ID = NaceLevel2Term.naceLevel2ID
AND NaceLevel2.versionID = 67



------------------- Version 68
------------------------------------------------------------------


-- :name get-deprecated-occupation-name :*
-- :doc get occupation names that have been deprecated after version 67  ;
SELECT OccupationNameTerm1.occupationNameID, OccupationNameTerm2.occupationNameID as deprecatedOccupation
FROM TaxonomyDB.dbo.OccupationNameTerm as OccupationNameTerm1
RIGHT JOIN TaxonomyDBVersion.dbo.OccupationNameTerm as OccupationNameTerm2
ON OccupationNameTerm1.occupationNameID = OccupationNameTerm2.occupationNameID
WHERE OccupationNameTerm1.occupationNameID is NULL
AND OccupationNameTerm2.versionID = 67


-- :name get-new-occupation-name :*
-- :doc get occupation names that has been added after version 67 ;
SELECT OccupationName.*, OccupationNameTerm.*
FROM TaxonomyDB.dbo.OccupationName OccupationName, TaxonomyDB.dbo.OccupationNameTerm OccupationNameTerm
WHERE OccupationName.occupationNameID = OccupationNameTerm.occupationNameID
AND OccupationNameTerm.languageID = 502
AND OccupationName.countryID = OccupationNameTerm.countryID
AND OccupationName.modificationDate > (
SELECT created
FROM TaxonomyDBVersion.dbo.Version
WHERE versionID = 67
)


-- :name get-replaced--occupation-name :*
-- :doc get replaced occupation names after version 67; omkoppling
SELECT occupationNameID, countryID, term, standard, locale, occupationNameIDRef, countryIDRef, modificationDate
FROM TaxonomyDB.dbo.OccupationNameReference
WHERE
modificationDate > (
SELECT created
FROM TaxonomyDBVersion.dbo.Version
WHERE versionID = 67
)


-- :name get-renamed-occupation-name :*
-- :doc get renamed occupation names after version 67
SELECT DISTINCT OccupationNameTerm.term, OccupationNameTerm.occupationNameID
FROM TaxonomyDB.dbo.OccupationNameTerm OccupationNameTerm, TaxonomyDB.dbo.OccupationNameTermHistory OccupationNameTermHistory
WHERE
OccupationNameTermHistory.occupationNameID = OccupationNameTerm.occupationNameID
AND OccupationNameTermHistory.countryID = OccupationNameTerm.countryID
AND OccupationNameTerm.languageID = 502
AND OccupationNameTermHistory.modificationDate > (
SELECT created
FROM TaxonomyDBVersion.dbo.Version
WHERE versionID = 67
)


--DRIVER'S LICENCE-- (No differences between versions!!!)

-- :name get-deprecated-drivers-licence :*
-- :doc get deprecated driver's licences, id's existing in version 67 but not version 68
SELECT [db-67].drivingLicenceID AS [67-id], [db-67].term AS [67-term]
FROM TaxonomyDBVersion.dbo.DrivingLicenceTerm AS [db-67]
WHERE [db-67].versionID = 67
AND [db-67].languageID = 502
AND [db-67].drivingLicenceID NOT IN
	(SELECT [db-68].drivingLicenceID
	FROM TaxonomyDB.dbo.DrivingLicenceTerm AS [db-68]
	WHERE [db-68].languageID = 502)

-- :name get-new-drivers-licence :*
-- :doc get new driver's licences, id's existing in version 68 but not in version 67
SELECT [db-68].drivingLicenceID AS [68-id], [db-68].term AS [68-term]
FROM TaxonomyDB.dbo.DrivingLicenceTerm AS [db-68]
WHERE [db-68].languageID = 502
AND [db-68].drivingLicenceID NOT IN
	(SELECT [db-67].drivingLicenceID
	FROM TaxonomyDBVersion.dbo.DrivingLicenceTerm AS [db-67]
	WHERE [db-67].versionID = 67
	AND [db-67].languageID = 502)

-- :name get-updated-drivers-licence-term :*
-- :doc get updated driver's licences where term/label differs between version 68 and version 67
SELECT [db-68].drivingLicenceID  AS [68-id],
	[db-68].term AS [68-term],
	[db-67].drivingLicenceID AS [67-id],
	[db-67].term [67-term]
FROM TaxonomyDB.dbo.DrivingLicenceTerm AS [db-68], TaxonomyDBVersion.dbo.DrivingLicenceTerm AS [db-67]
WHERE [db-68].drivingLicenceID = [db-67].drivingLicenceID
AND [db-68].languageID = 502
AND [db-67].languageID = 502
AND [db-67].versionID = 67
AND [db-67].term != [db-68].term

-- :name get-updated-drivers-licence-description :*
-- :doc get updated driver's licences where description differs between version 68 and version 67
SELECT [db-68].drivingLicenceID  AS [68-id],
	[db-68].description AS [68-description],
	[db-67].drivingLicenceID AS [67-id],
	[db-67].description AS [67-description]
FROM TaxonomyDB.dbo.DrivingLicenceTerm AS [db-68], TaxonomyDBVersion.dbo.DrivingLicenceTerm AS [db-67]
WHERE [db-68].drivingLicenceID = [db-67].drivingLicenceID
AND [db-68].languageID = 502
AND [db-67].languageID = 502
AND [db-67].versionID = 67
AND [db-67].description NOT LIKE [db-68].description

--EMPLOYMENT DURATION-- (No deprecated, one new, five updated)

-- :name get-deprecated-employment-duration :*
-- :doc get deprecated employment durations, id's existing in version 67 but not in version 68
SELECT [db-67].anställningsvaraktighetsID AS [67-id], [db-67].beteckning AS [67-term]
FROM TaxonomiDBSvenskVersion.dbo.AnställningsvaraktighetTerm AS [db-67]
WHERE [db-67].språkID = 502
AND [db-67].anställningsvaraktighetsID NOT IN
	(SELECT [db-68].anställningsvaraktighetsID
	FROM TaxonomiDBSvensk.dbo.AnställningsvaraktighetTerm AS [db-68]
	WHERE [db-68].språkID = 502)

-- :name get-new-employment-duration :*
-- :doc get new employment durations, id's existing in version 68 but not in version 67
SELECT [db-68].anställningsvaraktighetsID AS [68-id], [db-68].beteckning AS [68-term]
FROM TaxonomiDBSvensk.dbo.AnställningsvaraktighetTerm AS [db-68]
WHERE [db-68].språkID = 502
AND [db-68].anställningsvaraktighetsID NOT IN
	(SELECT [db-67].anställningsvaraktighetsID
	FROM TaxonomiDBSvenskVersion.dbo.AnställningsvaraktighetTerm AS [db-67]
	WHERE [db-67].språkID = 502)

-- :name get-updated-employment-duration-term :*
-- :doc get updated employment durations where term/label differs between version 68 and version 67
SELECT [db-68].anställningsvaraktighetsID  AS [68-id],
	[db-68].beteckning AS [68-term],
	[db-67].anställningsvaraktighetsID AS [67-id],
	[db-67].beteckning [67-term]
FROM TaxonomiDBSvensk.dbo.AnställningsvaraktighetTerm AS [db-68], TaxonomiDBSvenskVersion.dbo.AnställningsvaraktighetTerm AS [db-67]
WHERE [db-68].anställningsvaraktighetsID = [db-67].anställningsvaraktighetsID
AND [db-68].språkID = 502
AND [db-67].språkID = 502
AND [db-67].beteckning != [db-68].beteckning

--EMPLOYMENT TYPE-- (No differences between versions!!!)

-- :name get-deprecated-employment-type :*
-- :doc get deprecated employment types, id's existing in version 67 but not in version 68
SELECT [db-67].AnstallningTypJobbID AS [67-id], [db-67].beteckning AS [67-term]
FROM TaxonomiDBSvenskVersion.dbo.AnstallningTypJobbTerm AS [db-67]
WHERE [db-67].språkID = 502
AND [db-67].AnstallningTypJobbID NOT IN
	(SELECT [db-68].AnstallningTypJobbID
	FROM TaxonomiDBSvensk.dbo.AnstallningTypJobbTerm AS [db-68]
	WHERE [db-68].språkID = 502)

-- :name get-new-employment-type :*
-- :doc get new employment types, id's existing in version 68 but not in version 67
SELECT [db-68].AnstallningTypJobbID AS [68-id], [db-68].beteckning AS [68-term]
FROM TaxonomiDBSvensk.dbo.AnstallningTypJobbTerm AS [db-68]
WHERE [db-68].språkID = 502
AND [db-68].AnstallningTypJobbID NOT IN
	(SELECT [db-67].AnstallningTypJobbID
	FROM TaxonomiDBSvenskVersion.dbo.AnstallningTypJobbTerm AS [db-67]
	WHERE [db-67].språkID = 502)

-- :name get-updated-employment-type-term :*
-- :doc get updated employment types where term/label differs between version 68 and version 67
SELECT [db-68].AnstallningTypJobbID  AS [68-id],
	[db-68].beteckning AS [68-term],
	[db-67].AnstallningTypJobbID AS [67-id],
	[db-67].beteckning [67-term]
FROM TaxonomiDBSvensk.dbo.AnstallningTypJobbTerm AS [db-68], TaxonomiDBSvenskVersion.dbo.AnstallningTypJobbTerm AS [db-67]
WHERE [db-68].AnstallningTypJobbID = [db-67].AnstallningTypJobbID
AND [db-68].språkID = 502
AND [db-67].språkID = 502
AND [db-67].beteckning != [db-68].beteckning

--GEOGRAPHIC PLACES--

--CONTINENTS-- (No difference!)

-- :name get-deprecated-continent :*
-- :doc get deprecated continent, id's existing in version 67 but not in version 68
SELECT [db-67].continentID AS [67-id], [db-67].term AS [67-term]
FROM TaxonomyDBVersion.dbo.ContinentTerm AS [db-67]
WHERE [db-67].languageID = 502
AND [db-67].versionID = 67
AND [db-67].continentID NOT IN
	(SELECT [db-68].continentID
	FROM TaxonomyDB.dbo.ContinentTerm AS [db-68]
	WHERE [db-68].languageID = 502)

-- :name get-new-continent :*
-- :doc get new continent, id's existing in version 68 but not in version 67
SELECT [db-68].continentID AS [68-id], [db-68].term AS [68-term]
FROM TaxonomyDB.dbo.ContinentTerm AS [db-68]
WHERE [db-68].languageID = 502
AND [db-68].continentID NOT IN
	(SELECT [db-67].continentID
	FROM TaxonomyDBVersion.dbo.ContinentTerm AS [db-67]
	WHERE [db-67].languageID = 502
	AND [db-67].versionID = 67)

-- :name get-updated-continent-term :*
-- :doc get updated continent where term/label differs between version 68 and version 67
SELECT [db-68].continentID AS [68-id],
	[db-68].term AS [68-term],
	[db-67].continentID AS [67-id],
	[db-67].term [67-term]
FROM TaxonomyDB.dbo.ContinentTerm AS [db-68], TaxonomyDBVersion.dbo.ContinentTerm AS [db-67]
WHERE [db-68].continentID = [db-67].continentID
AND [db-67].versionID = 67
AND [db-68].languageID = 502
AND [db-67].languageID = 502
AND [db-67].term != [db-68].term

--COUNTRIES-- (No deprecated, no new, three updated)

-- :name get-deprecated-country :*
-- :doc get deprecated countries, id's existing in version 67 but not in version 68
SELECT [db-67].countryID AS [67-id], [db-67].term AS [67-term]
FROM TaxonomyDBVersion.dbo.CountryTerm AS [db-67]
WHERE [db-67].languageID = 502
AND [db-67].versionID = 67
AND [db-67].countryID NOT IN
	(SELECT [db-68].countryID
	FROM TaxonomyDB.dbo.CountryTerm AS [db-68]
	WHERE [db-68].languageID = 502)

-- :name get-new-country :*
-- :doc get new countries, id's existing in version 68 but not in version 67
SELECT [db-68].countryID AS [68-id], [db-68].term AS [68-term]
FROM TaxonomyDB.dbo.CountryTerm AS [db-68]
WHERE [db-68].languageID = 502
AND [db-68].countryID NOT IN
	(SELECT [db-67].countryID
	FROM TaxonomyDBVersion.dbo.CountryTerm AS [db-67]
	WHERE [db-67].languageID = 502
	AND [db-67].versionID = 67)

-- :name get-updated-country-term :*
-- :doc get updated countries where term/label differs between version 68 and version 67
SELECT [db-68].countryID AS [68-id],
	[db-68].term AS [68-term],
	[db-67].countryID AS [67-id],
	[db-67].term [67-term]
FROM TaxonomyDB.dbo.CountryTerm AS [db-68], TaxonomyDBVersion.dbo.CountryTerm AS [db-67]
WHERE [db-68].countryID = [db-67].countryID
AND [db-67].versionID = 67
AND [db-68].languageID = 502
AND [db-67].languageID = 502
AND [db-67].term != [db-68].term

--REGIONS--(No difference between versions!!!)

-- :name get-deprecated-region :*
-- :doc get deprecated regions, id's existing in version 67 but not in version 68
SELECT [db-67].EURegionID AS [67-id], [db-67].term AS [67-term]
FROM TaxonomyDBVersion.dbo.EURegionTerm AS [db-67]
WHERE [db-67].languageID = 502
AND [db-67].versionID = 67
AND [db-67].EURegionID NOT IN
	(SELECT [db-68].EURegionID
	FROM TaxonomyDB.dbo.EURegionTerm AS [db-68]
	WHERE [db-68].languageID = 502)

-- :name get-new-region :*
-- :doc get new regions, id's existing in version 68 but not in version 67
SELECT [db-68].EURegionID AS [68-id], [db-68].term AS [68-term]
FROM TaxonomyDB.dbo.EURegionTerm AS [db-68]
WHERE [db-68].languageID = 502
AND [db-68].EURegionID NOT IN
	(SELECT [db-67].EURegionID
	FROM TaxonomyDBVersion.dbo.EURegionTerm AS [db-67]
	WHERE [db-67].languageID = 502
	AND [db-67].versionID = 67)

-- :name get-updated-region-term :*
-- :doc get updated regions where term/label differs between version 68 and version 67
SELECT [db-68].EURegionID AS [68-id],
	[db-68].term AS [68-term],
	[db-67].EURegionID AS [67-id],
	[db-67].term [67-term]
FROM TaxonomyDB.dbo.EURegionTerm AS [db-68], TaxonomyDBVersion.dbo.EURegionTerm AS [db-67]
WHERE [db-68].EURegionID = [db-67].EURegionID
AND [db-67].versionID = 67
AND [db-68].languageID = 502
AND [db-67].languageID = 502
AND [db-67].term != [db-68].term

--MUNICIPALITIES--(No difference between versions!!!)

-- :name get-deprecated-municipality :*
-- :doc get deprecated municipalities, id's existing in version 67 but not in version 68
SELECT [db-67].municipalityID AS [67-id], [db-67].term AS [67-term]
FROM TaxonomyDBVersion.dbo.MunicipalityTerm AS [db-67]
WHERE [db-67].languageID = 502
AND [db-67].versionID = 67
AND [db-67].municipalityID NOT IN
	(SELECT [db-68].municipalityID
	FROM TaxonomyDB.dbo.MunicipalityTerm AS [db-68]
	WHERE [db-68].languageID = 502)

-- :name get-new-municipality :*
-- :doc get new municipalities, id's existing in version 68 but not in version 67
SELECT [db-68].municipalityID AS [68-id], [db-68].term AS [68-term]
FROM TaxonomyDB.dbo.MunicipalityTerm AS [db-68]
WHERE [db-68].languageID = 502
AND [db-68].municipalityID NOT IN
	(SELECT [db-67].municipalityID
	FROM TaxonomyDBVersion.dbo.MunicipalityTerm AS [db-67]
	WHERE [db-67].languageID = 502
	AND [db-67].versionID = 67)

-- :name get-updated-municipality-term :*
-- :doc get updated municipalities where term/label differs between version 68 and version 67
SELECT [db-68].municipalityID AS [68-id],
	[db-68].term AS [68-term],
	[db-67].municipalityID AS [67-id],
	[db-67].term [67-term]
FROM TaxonomyDB.dbo.MunicipalityTerm AS [db-68], TaxonomyDBVersion.dbo.MunicipalityTerm AS [db-67]
WHERE [db-68].municipalityID = [db-67].municipalityID
AND [db-67].versionID = 67
AND [db-68].languageID = 502
AND [db-67].languageID = 502
AND [db-67].term != [db-68].term

--LANGUAGE--(No difference between versions!!!)

-- :name get-deprecated-language :*
-- :doc get deprecated languages, id's existing in version 67 but not in version 68
SELECT [db-67].languageID AS [67-id], [db-67].term AS [67-term]
FROM TaxonomyDBVersion.dbo.LanguageTerm AS [db-67]
WHERE [db-67].translationLanguageID = 502
AND [db-67].versionID = 67
AND [db-67].languageID NOT IN
	(SELECT [db-68].languageID
	FROM TaxonomyDB.dbo.LanguageTerm AS [db-68]
	WHERE [db-68].translationLanguageID = 502)

-- :name get-new-language :*
-- :doc get new languages, id's existing in version 68 but not in version 67
SELECT [db-68].languageID AS [68-id], [db-68].term AS [68-term]
FROM TaxonomyDB.dbo.LanguageTerm AS [db-68]
WHERE [db-68].translationLanguageID = 502
AND [db-68].languageID NOT IN
	(SELECT [db-67].languageID
	FROM TaxonomyDBVersion.dbo.LanguageTerm AS [db-67]
	WHERE [db-67].translationLanguageID = 502
	AND [db-67].versionID = 67)

-- :name get-updated-language-term :*
-- :doc get updated languages where term/label differs between version 68 and version 67
SELECT [db-68].languageID  AS [68-id],
	[db-68].term AS [68-term],
	[db-67].languageID AS [67-id],
	[db-67].term [67-term]
FROM TaxonomyDB.dbo.LanguageTerm AS [db-68], TaxonomyDBVersion.dbo.LanguageTerm AS [db-67]
WHERE [db-68].languageID = [db-67].languageID
AND [db-67].versionID = 67
AND [db-68].translationLanguageID = 502
AND [db-67].translationLanguageID = 502
AND [db-67].term != [db-68].term

--LANGUAGE LEVEL-- (No differences between versions!!!)

-- :name get-deprecated-language-level :*
-- :doc get deprecated language levels, id's existing in version 67 but not in version 68
SELECT [db-67].languageLevelID AS [67-id], [db-67].term AS [67-term]
FROM TaxonomyDBVersion.dbo.LanguageLevelTerm AS [db-67]
WHERE [db-67].languageID = 502
AND [db-67].versionID = 67
AND [db-67].languageLevelID NOT IN
	(SELECT [db-68].languageLevelID
	FROM TaxonomyDB.dbo.LanguageLevelTerm AS [db-68]
	WHERE [db-68].languageID = 502)

-- :name get-new-language-level :*
-- :doc get new language levels, id's existing in version 68 but not in version 67
SELECT [db-68].languageLevelID AS [68-id], [db-68].term AS [68-term]
FROM TaxonomyDB.dbo.LanguageLevelTerm AS [db-68]
WHERE [db-68].languageID = 502
AND [db-68].languageLevelID NOT IN
	(SELECT [db-67].languageLevelID
	FROM TaxonomyDBVersion.dbo.LanguageLevelTerm AS [db-67]
	WHERE [db-67].languageID = 502
	AND [db-67].versionID = 67)

-- :name get-updated-language-level-term :*
-- :doc get updated language levels where term/label differs between version 68 and version 67
SELECT [db-68].languageLevelID AS [68-id],
	[db-68].term AS [68-term],
	[db-67].languageLevelID AS [67-id],
	[db-67].term [67-term]
FROM TaxonomyDB.dbo.LanguageLevelTerm AS [db-68], TaxonomyDBVersion.dbo.LanguageLevelTerm AS [db-67]
WHERE [db-68].languageLevelID = [db-67].languageLevelID
AND [db-67].versionID = 67
AND [db-68].languageID = 502
AND [db-67].languageID = 502
AND [db-67].term != [db-68].term

--WAGE TYPE-- (No differences between versions!!!)

-- :name get-deprecated-wage-type :*
-- :doc get deprecated wage types, id's existing in version 67 but not in version 68
SELECT [db-67].löneformsID AS [67-id], [db-67].beteckning AS [67-term]
FROM TaxonomiDBSvenskVersion.dbo.LöneformTerm AS [db-67]
WHERE [db-67].språkID = 502
AND [db-67].löneformsID NOT IN
	(SELECT [db-68].löneformsID
	FROM TaxonomiDBSvensk.dbo.LöneformTerm AS [db-68]
	WHERE [db-68].språkID = 502)

-- :name get-new-wage-type :*
-- :doc get new wage types, id's existing in version 68 but not in version 67
SELECT [db-68].löneformsID AS [68-id], [db-68].beteckning AS [68-term]
FROM TaxonomiDBSvensk.dbo.LöneformTerm AS [db-68]
WHERE [db-68].språkID = 502
AND [db-68].löneformsID NOT IN
	(SELECT [db-67].löneformsID
	FROM TaxonomiDBSvenskVersion.dbo.LöneformTerm AS [db-67]
	WHERE [db-67].språkID = 502)

-- :name get-updated-wage-type-term :*
-- :doc get updated wage types where term/label differs between version 68 and version 67
SELECT [db-68].löneformsID  AS [68-id],
	[db-68].beteckning AS [68-term],
	[db-67].löneformsID AS [67-id],
	[db-67].beteckning [67-term]
FROM TaxonomiDBSvensk.dbo.LöneformTerm AS [db-68], TaxonomiDBSvenskVersion.dbo.LöneformTerm AS [db-67]
WHERE [db-68].löneformsID = [db-67].löneformsID
AND [db-68].språkID = 502
AND [db-67].språkID = 502
AND [db-67].beteckning != [db-68].beteckning

--WORKTIME EXTENT-- (One deprecated, no new, no updated)

-- :name get-deprecated-worktime-extent :*
-- :doc get deprecated worktime extents, id's existing in version 67 but not in version 68
SELECT [db-67].arbetstidsID AS [67-id], [db-67].beteckning AS [67-term]
FROM TaxonomiDBSvenskVersion.dbo.ArbetstidTerm AS [db-67]
WHERE [db-67].språkID = 502
AND [db-67].arbetstidsID NOT IN
	(SELECT [db-68].arbetstidsID
	FROM TaxonomiDBSvensk.dbo.ArbetstidTerm AS [db-68]
	WHERE [db-68].språkID = 502)

-- :name get-new-worktime-extent :*
-- :doc get new worktime extents, id's existing in version 68 but not in version 67
SELECT [db-68].arbetstidsID AS [68-id], [db-68].beteckning AS [68-term]
FROM TaxonomiDBSvensk.dbo.ArbetstidTerm AS [db-68]
WHERE [db-68].språkID = 502
AND [db-68].arbetstidsID NOT IN
	(SELECT [db-67].arbetstidsID
	FROM TaxonomiDBSvenskVersion.dbo.ArbetstidTerm AS [db-67]
	WHERE [db-67].språkID = 502)

-- :name get-updated-worktime-extent-term :*
-- :doc get updated worktime extents where term/label differs between version 68 and version 67
SELECT [db-68].arbetstidsID  AS [68-id],
	[db-68].beteckning AS [68-term],
	[db-67].arbetstidsID AS [67-id],
	[db-67].beteckning [67-term]
FROM TaxonomiDBSvensk.dbo.ArbetstidTerm AS [db-68], TaxonomiDBSvenskVersion.dbo.ArbetstidTerm AS [db-67]
WHERE [db-68].arbetstidsID = [db-67].arbetstidsID
AND [db-68].språkID = 502
AND [db-67].språkID = 502
AND [db-67].beteckning != [db-68].beteckning

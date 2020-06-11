---------------------------------------------------- UNEMPLOYMENT FUNDS -------------------------------------------

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-unemployment-fund :*
-- :doc Get all unemployment funds
SELECT AKassa.AKassekod, AKassaTerm.*
FROM TaxonomiDBSvensk.dbo.AKassa AKassa,
     TaxonomiDBSvensk.dbo.AKassaTerm AKassaTerm
WHERE
     AKassaTerm.AKasseID = AKassa.AKasseID

---------------------------------------------------- SKILLS -------------------------------------------------------

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-skills :*
-- :doc Get all skills
SELECT Skill.*, SkillTerm.*
FROM TaxonomyDBVersion.dbo.Skill Skill, TaxonomyDBVersion.dbo.SkillTerm SkillTerm
WHERE
	Skill.versionID = SkillTerm.versionID
AND Skill.skillID = SkillTerm.skillID
AND Skill.countryID = SkillTerm.countryID
AND SkillTerm.versionID = 67
AND SkillTerm.languageID = 502


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-skill-headlines :*
-- :doc Get all skill headlines
SELECT SkillHeadlineTerm.*, SkillHeadline.*
FROM TaxonomyDBVersion.dbo.SkillHeadline SkillHeadline, TaxonomyDBVersion.dbo.SkillHeadlineTerm SkillHeadlineTerm
WHERE
	SkillHeadlineTerm.skillHeadlineID = SkillHeadline.skillHeadlineID
	AND SkillHeadlineTerm.languageID = 502
        AND SkillHeadlineTerm.versionID = 67
        AND SkillHeadline.versionID = 67

--------------------------------------------------- LANGUAGES -----------------------------------------------------

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-language :*
-- :doc Get all langages
SELECT Language.*, LanguageTerm.*
FROM TaxonomyDB.dbo.[Language] Language, TaxonomyDB.dbo.LanguageTerm LanguageTerm
WHERE
LanguageTerm.languageID = Language.languageID
and LanguageTerm.translationLanguageID = 502

--------------------------------------------------- LANGUAGE LEVELS ------------------------------------------------

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-language-level :*
-- :doc Get all langage-levels
SELECT LanguageLevel.*, LanguageLevelTerm.*
FROM TaxonomyDBVersion.dbo.LanguageLevel LanguageLevel, TaxonomyDBVersion.dbo.LanguageLevelTerm LanguageLevelTerm
WHERE LanguageLevelTerm.languageLevelID = LanguageLevel.languageLevelID
AND languageID = 502
AND LanguageLevel.versionID = 67
AND LanguageLevelTerm.versionID = 67

--------------------------------------------------- WORKTIME EXTENT -----------------------------------------------

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

------------------------------------------ GEOGRAPHIC PLACES --------------------------------------------

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
Country.countryCode AS [code],
Country.countryCodeAlpha3 AS [code3]
FROM TaxonomyDBVersion.dbo.Country Country,
TaxonomyDBVersion.dbo.CountryTerm CountryTerm
WHERE Country.versionID = 67
AND	CountryTerm.versionID = 67
AND Country.countryID = CountryTerm.countryID
AND languageID = 502


-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-EU-regions :*
-- :doc Get all EU regions except faulty Jura 228
SELECT EURegion.countryID AS [parent-id],
	EURegion.EURegionID AS [id],
	EURegionTerm.term AS [term],
	EURegion.NUTSCodeLevel3 AS [code],
        EURegion.nationalNUTSLevel3Code AS [national-code]
FROM TaxonomyDBVersion.dbo.EURegion EURegion,
    TaxonomyDBVersion.dbo.EURegionTerm EURegionTerm
WHERE EURegion.versionID = EURegionTerm.versionID
AND EURegion.EURegionID = EURegionTerm.EURegionID
AND EURegionTerm.languageID = 502
AND	EURegionTerm.versionID = 67
AND EURegion.versionID = 67
AND EURegion.EURegionID != 228

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-municipalities :*
-- :doc Get all municipalities
SELECT Municipality.EURegionID AS [parent-id],
Municipality.municipalityID AS [id],
MunicipalityTerm.term AS [term],
nationalNUTSLAU2Code  AS [national-code]
FROM TaxonomyDBVersion.dbo.Municipality Municipality,
TaxonomyDBVersion.dbo.MunicipalityTerm MunicipalityTerm
WHERE Municipality.versionID = MunicipalityTerm.versionID
AND Municipality.municipalityID = MunicipalityTerm.municipalityID
AND	MunicipalityTerm.versionID = 67
AND Municipality.versionID = 67

------------------------------------------ DRIVING LICENCE --------------------------------------------

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-driving-licence :*
-- :doc Get all driving licence categories
SELECT DrivingLicence.*, DrivingLicenceTerm.*
FROM TaxonomyDBVersion.dbo.DrivingLicence DrivingLicence,
    TaxonomyDBVersion.dbo.DrivingLicenceTerm DrivingLicenceTerm
WHERE DrivingLicenceTerm.drivingLicenceID = DrivingLicence.drivingLicenceID
AND languageID = 502
AND DrivingLicence.versionID = 67
AND DrivingLicenceTerm.versionID = 67

-- :name get-driving-licence-combination :*
-- :doc Get all driving licence combinations
SELECT Körkortskombination.*, Körkortskoppling.*
FROM TaxonomiDBSvensk.dbo.Körkortskombination Körkortskombination, TaxonomiDBSvensk.dbo.Körkortskoppling Körkortskoppling
WHERE Körkortskoppling.kombinationsID = Körkortskombination.kombinationsID

------------------------------------------ EMPLOYMENT DURATION --------------------------------------------

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

------------------------------------------ EMPLOYMENT TYPES --------------------------------------------

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

------------------------------------------ WAGE TYPES --------------------------------------------

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

------------------------------------------ OCCUPATIONS --------------------------------------------

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-occupation-name :*
-- :doc Get all occupation names ;
SELECT [db].occupationNameID AS [occupation-name-id],
	[db].occupationGroupID AS [parent-id-isco-4],
	[db].localeGroupID AS [parent-id-ssyk-4],
	[db-term].term AS [occupation-name-term]
FROM TaxonomyDBVersion.dbo.OccupationName AS [db], TaxonomyDBVersion.dbo.OccupationNameTerm AS [db-term]
WHERE [db].versionID = [db-term].versionID
AND	[db].occupationNameID = [db-term].occupationNameID
AND	[db].countryID = [db-term].countryID
AND [db].versionID = 67
AND [db-term].languageID = 502

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-ssyk-4 :*
-- :doc Get all ssyk, dont confuse this one with ISCO ;
SELECT [db].localeGroupID AS [ssyk-4-id],
	[db].localeCode AS [ssyk-4-code],
	[db].localeLevel3ID AS [parent-id-SSYK-3],
	[db].localeFieldID AS [parent-id-occupation-field],
	[db-term].term AS [ssyk-4-term],
	[db-term].description AS [ssyk-4-description]
FROM TaxonomyDBVersion.dbo.LocaleGroup AS [db], TaxonomyDBVersion.dbo.LocaleGroupTerm AS [db-term]
WHERE [db].versionID = [db-term].versionID
AND	[db].localeGroupID = [db-term].localeGroupID
AND [db].versionID = 67
AND [db-term].languageID = 502

-- :name get-ssyk-level-3 :*
-- :doc Get ssyk level 3 ;
SELECT [db].localeLevel3ID AS [ssyk-3-id],
	[db].localeCodeLevel3 AS [ssyk-3-code],
	[db].localeLevel2ID AS [parent-id-ssyk-2],
	[db-term].term AS [ssyk-3-term]
FROM TaxonomyDBVersion.dbo.LocaleLevel3 AS [db], TaxonomyDBVersion.dbo.LocaleLevel3Term AS [db-term]
WHERE [db].versionID = [db-term].versionID
AND [db].localeLevel3ID = [db-term].localeLevel3ID
AND [db-term].languageID = 502
AND [db-term].versionID = 67

-- :name get-ssyk-level-2 :*
-- :doc Get ssyk level 2 ;
SELECT [db].localeLevel2ID AS [ssyk-2-id],
	[db].localeCodeLevel2 AS [ssyk-2-code],
	[db].localeLevel1ID AS [parent-id-ssyk-1],
	[db-term].term AS [ssyk-2-term]
FROM TaxonomyDBVersion.dbo.LocaleLevel2 AS [db], TaxonomyDBVersion.dbo.LocaleLevel2Term AS [db-term]
WHERE [db].versionID = [db-term].versionID
AND [db].localeLevel2ID = [db-term].localeLevel2ID
AND [db-term].languageID = 502
AND [db-term].versionID = 67

-- :name get-ssyk-level-1 :*
-- :doc Get ssyk level 1 ;
SELECT [db].localeLevel1ID AS [ssyk-1-id],
	[db].localeCodeLevel1 AS [ssyk-1-code],
	[db-term].term AS [ssyk-1-term]
FROM TaxonomyDBVersion.dbo.LocaleLevel1 AS [db], TaxonomyDBVersion.dbo.LocaleLevel1Term AS [db-term]
WHERE [db].versionID = [db-term].versionID
AND [db].localeLevel1ID = [db-term].localeLevel1ID
AND [db-term].languageID = 502
AND [db-term].versionID = 67

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-occupation-field :*
-- :doc Get yrkesomraden occupation-field ;
SELECT [db].localeFieldID AS [occupation-field-id],
	[db-term].term AS [occupation-field-term],
	[occupation-field-description] =
    CASE WHEN [db-term].description like '%samråd%' THEN 'Militärt arbete' ELSE [db-term].description END
FROM TaxonomyDBVersion.dbo.LocaleField AS [db], TaxonomyDBVersion.dbo.LocaleFieldTerm AS [db-term]
WHERE
	[db].versionID = [db-term].versionID
AND [db].localeFieldID = [db-term].localeFieldID
AND [db].versionID = 67

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-name-field-relation :*
-- :doc Get relations between occupation-name and occupation-field
SELECT on2.occupationNameID as [occupation-name-id]
     , og.occupationFieldID as [occupation-field-id]
FROM TaxonomyDB.dbo.OccupationName on2
   , TaxonomyDB.dbo.OccupationGroup og
WHERE
     on2.occupationGroupID = og.occupationGroupID

-- :name get-isco-level-4 :*
-- :doc Get isco level 4 ;
SELECT [db].occupationGroupID AS [isco-4-id],
	[db].ISCO AS [isco-4-isco-code],
	[db].occupationFieldID AS [parent-id-isco-1],
	[db-term].term AS [isco-4-term],
	[db-term].description AS [isco-4-description]
FROM TaxonomyDBVersion.dbo.OccupationGroup AS [db], TaxonomyDBVersion.dbo.OccupationGroupTerm AS [db-term]
WHERE [db].versionID = [db-term].versionID
AND [db].occupationGroupID = [db-term].occupationGroupID
AND [db-term].languageID = 502
AND [db].versionID = 67

-- :name get-isco-level-1 :*
-- :doc Get isco level 1 ;
SELECT [db].occupationFieldID AS [isco-1-id],
	[db-term].term AS [isco-1-term],
	[db-term].description AS [isco-1-description]
FROM TaxonomyDBVersion.dbo.OccupationField AS [db], TaxonomyDBVersion.dbo.OccupationFieldTerm AS [db-term]
WHERE [db].versionID = [db-term].versionID
AND [db].occupationFieldID = [db-term].occupationFieldID
AND [db-term].languageID = 502
AND [db].versionID = 67

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-occupation-name-affinity :*
-- :doc get affinity relations between occupation names ;
SELECT [db-rate].percentage AS [percentage],
	[db-occupation-name].occupationNameID AS [affinity-to-occupation-name-id],
	[db-occupation-name].affinityID AS [affinity-from-occupation-name-id]
FROM TaxonomyDBVersion.dbo.AffinityRate AS [db-rate], TaxonomyDBVersion.dbo.OccupationNameAffinity AS [db-occupation-name]
WHERE [db-rate].versionID = [db-occupation-name].versionID
AND [db-rate].affinityRateID = [db-occupation-name].affinityRateID
AND [db-occupation-name].versionID = 67

-- A ":result" value of ":*" specifies a vector of records
-- (as hashmaps) will be returned
-- :name get-replaced-occupation-names-reference :*
-- :doc get occupation names that have been replaced by a newer occupation  ; except konsthantverkare
SELECT occupationNameID AS [deprecated-occupation-name-id],
	term AS [deprecated-occupation-name-term],
	occupationNameIDRef AS [replacing-occupation-name-id]
FROM TaxonomyDBVersion.dbo.OccupationNameReference
WHERE versionID = 67
AND term != 'Konsthantverkare';


-- :name get-popular-synonym-occupation :*
-- :doc ge popular synonyms ;
SELECT popularSynonymID AS [synonym-id], term AS [synonym-term]
FROM TaxonomyDBVersion.dbo.PopularSynonym
WHERE versionID = 67

-- :name get-popular-synonym-occupation-relation :*
-- :doc ge popular synonyms ;
SELECT OccupationNameSynonym.occupationNameID AS [occupation-name-id],
	PopularSynonym.popularSynonymID AS [synonym-id],
	PopularSynonym.term AS [synonym-term]
FROM TaxonomyDBVersion.dbo.OccupationNameSynonym OccupationNameSynonym, TaxonomyDBVersion.dbo.PopularSynonym PopularSynonym
WHERE
	PopularSynonym.versionID = OccupationNameSynonym.versionID
AND PopularSynonym.popularSynonymID = OccupationNameSynonym.popularSynonymID
AND PopularSynonym.versionID = 67

-- :name get-isco-4-skill-relation :*
-- :doc ge isco level 4 to skill relation ;
SELECT skillID AS [skill-id],
	occupationGroupID AS [isco-4-id]
FROM TaxonomyDBVersion.dbo.OccupationGroup_Skill WHERE versionID = 67;

-- :name get-ssyk-4-isco-4-relation :*
-- :doc get occupation group isco relation ;
SELECT occupationGroupID AS [isco-4-id],
	localeGroupID AS [ssyk-4-id]
FROM TaxonomyDBVersion.dbo.ISCOLocale WHERE versionID = 67;


------------------------------------------START SUN --------------------------------------------

-- SUN SHOULDN'T BE CONVERTED!
-- Editorial team has instructued us not to convert SUN at the moment (May 2019).
-- A new version of SUN is released during spring 2019.
-- The new version will be edited by editorial team and ready to be written to Datomic in September at the latest.

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
       SUNInriktning2Term.beteckning AS [term],
       eft.description AS [definition]
FROM TaxonomiDBSvenskVersion.dbo.SUNInriktning2 SUNInriktning2,
     TaxonomiDBSvenskVersion.dbo.SUNInriktning2Term SUNInriktning2Term ,
     [TaxonomyDBVersion].[dbo].[EducationFieldTerm] AS eft
WHERE SUNInriktning2.versionID = SUNInriktning2Term.versionID
  AND SUNInriktning2.SUNInriktning2ID = SUNInriktning2Term.SUNInriktning2ID
  AND eft.versionID = 67
  AND eft.term = SUNInriktning2Term.beteckning
UNION ALL
SELECT SUNInriktning2.SUNInriktning1ID AS [parent-id],
       SUNInriktning2.SUNInriktning2ID AS [id],
       SUNInriktning2.SUNKodInriktning2 AS [code],
       SUNInriktning2.versionID AS [version-id],
       SUNInriktning2Term.beteckning AS [term],
       NULL AS [definition]
FROM TaxonomiDBSvenskVersion.dbo.SUNInriktning2 SUNInriktning2,
     TaxonomiDBSvenskVersion.dbo.SUNInriktning2Term SUNInriktning2Term
WHERE SUNInriktning2.versionID = SUNInriktning2Term.versionID
  AND SUNInriktning2.SUNInriktning2ID = SUNInriktning2Term.SUNInriktning2ID
  AND (SUNInriktning2.SUNInriktning2ID = 2
       OR SUNInriktning2.SUNInriktning2ID = 26)



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




------------------------------------------START SUN LEVEL--------------------------------------------

-- SUN SHOULDN'T BE CONVERTED!
-- Editorial team has instructued us not to convert SUN at the moment (May 2019).
-- A new version of SUN is released during spring 2019.
-- The new version will be edited by editorial team and ready to be written to Datomic in September at the latest.

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


-- :name get-sun-2000-guide :*
-- :doc gets the guide relations between education level and field
SELECT  SUNNivå1ID as [education-level-1-id], SUNInriktning3ID as [education-field-3-id]
FROM TaxonomiDBSvenskVersion.dbo.SUNNivå1Inriktning3Guide;


---------------------------------------------------- START NACE/SNI------------------------------------------------

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



-------------------------------------------------------------------------------------------------
------------------- Version 68 ------------------------------------------------------------------
-------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------


----------------------------------------------------------- OCCUPATION NAME ----------------------------

-- 67 deprecated concepts (June 12)
-- :name get-deprecated-occupation-name :*
-- :doc get occupation names that have been deprecated after version 67  ;
SELECT [db-67].occupationNameID AS [occupation-name-id],
	[db-67].occupationGroupID AS [parent-id-isco-4],
	[db-67].localeGroupID AS [parent-id-ssyk-4],
	[db-67-term].term AS [occupation-name-term]
FROM TaxonomyDBVersion.dbo.OccupationName AS [db-67], TaxonomyDBVersion.dbo.OccupationNameTerm AS [db-67-term]
WHERE [db-67].versionID = [db-67-term].versionID
AND	[db-67].occupationNameID = [db-67-term].occupationNameID
AND	[db-67].countryID = [db-67-term].countryID
AND [db-67].versionID = 67
AND [db-67-term].languageID = 502
AND [db-67-term].occupationNameID NOT IN
	(SELECT [db-68].occupationNameID
	FROM TaxonomyDB.dbo.OccupationName AS [db-68])

-- 129 new concepts (June 12)
-- :name get-new-occupation-name :*
-- :doc get occupation names that has been added in version 68 ;
SELECT db68.occupationNameID AS [occupation-name-id],
	db68term.term AS [term],
	db68.occupationGroupID AS [parent-id-isco-4],
	db68.localeGroupID AS [parent-id-ssyk-4]
FROM TaxonomyDB.dbo.OccupationName AS db68,
	TaxonomyDB.dbo.OccupationNameTerm AS db68term
WHERE db68.occupationNameID = db68term.occupationNameID
AND db68term.languageID = 502
AND db68.countryID = db68term.countryID
AND db68.occupationNameID NOT IN
(SELECT db67.occupationNameID
FROM TaxonomyDBVersion.dbo.OccupationName AS db67
WHERE db67.versionID = 67)

-- 90 updated concept terms (June 12)
-- :name get-updated-occupation-name-term :*
-- :doc get occupation names that changed term in version 68;
SELECT db68.occupationNameID AS [occupation-name-id-68],
	db67.occupationNameID AS [occupation-name-id-67],
	db68term.term AS [term-68],
	db67term.term AS [term-67],
	db68.occupationGroupID AS [parent-id-isco-4-68],
	db67.occupationGroupID AS [parent-id-isco-4-67],
	db68.localeGroupID AS [parent-id-ssyk-4-68],
	db67.localeGroupID AS [parent-id-ssyk-4-67]
FROM TaxonomyDB.dbo.OccupationName AS db68,
	TaxonomyDB.dbo.OccupationNameTerm AS db68term,
	TaxonomyDBVersion.dbo.OccupationName AS db67,
	TaxonomyDBVersion.dbo.OccupationNameTerm AS db67term
WHERE db68.occupationNameID = db68term.occupationNameID
AND db68term.occupationNameID = db67term.occupationNameID
AND db67term.occupationNameID = db67.occupationNameID
AND db68term.languageID = 502
AND db67term.languageID = 502
AND db68.countryID = 199
AND db67.countryID = 199
AND db68term.countryID =199
AND db67term.countryID =199
AND db67.versionID = 67
AND db67term.versionID = 67
AND db68term.term != db67term.term COLLATE SQL_Latin1_General_CP1_CS_AS

-- 84 relations (June 12)
-- :name get-deprecated-occupation-name-relation-to-parent-isco :*
SELECT
	db67.occupationNameID AS [occupation-name-id-67],
	db67term.term AS [term-67],
	db67.occupationGroupID AS [parent-id-67],
	'isco-level-4' AS [parent-type]
FROM TaxonomyDBVersion.dbo.OccupationName AS db67,
	TaxonomyDBVersion.dbo.OccupationNameTerm AS db67term
WHERE db67.versionID = 67
AND db67term.versionID = 67
AND db67.occupationNameID = db67term.occupationNameID
AND NOT EXISTS
(SELECT occupationGroupID, occupationNameID
	FROM TaxonomyDB.dbo.OccupationName
	WHERE occupationGroupID = db67.occupationGroupID
	AND occupationNameID = db67.occupationNameID)

-- 146 relations (June 12)
-- :name get-new-occupation-name-relation-to-parent-isco :*
SELECT
	db68.occupationNameID AS [occupation-name-id-68],
	db68term.term AS [term-68],
	db68.occupationGroupID AS [parent-id-68],
	'isco-level-4' AS [parent-type]
FROM TaxonomyDB.dbo.OccupationName AS db68,
	TaxonomyDB.dbo.OccupationNameTerm AS db68term
WHERE db68.occupationNameID = db68term.occupationNameID
AND db68term.languageID = 502
AND NOT EXISTS
(SELECT occupationGroupID, occupationNameID
	FROM TaxonomyDBVersion.dbo.OccupationName
	WHERE occupationGroupID = db68.occupationGroupID
	AND occupationNameID = db68.occupationNameID
	AND versionID = 67)

-- 84 relations (June 12)
-- :name get-deprecated-occupation-name-relation-to-parent-ssyk :*
SELECT
	db67.occupationNameID AS [occupation-name-id-67],
	db67term.term AS [term-67],
	db67.localeGroupID AS [parent-id-67],
    'ssyk-level-4' AS [parent-type]
FROM TaxonomyDBVersion.dbo.OccupationName AS db67,
	TaxonomyDBVersion.dbo.OccupationNameTerm AS db67term
WHERE db67.versionID = 67
AND db67term.versionID = 67
AND db67.occupationNameID = db67term.occupationNameID
AND NOT EXISTS
(SELECT localeGroupID, occupationNameID
	FROM TaxonomyDB.dbo.OccupationName
	WHERE localeGroupID = db67.localeGroupID
	AND occupationNameID = db67.occupationNameID)

-- 146 relations (June 12)
-- :name get-new-occupation-name-relation-to-parent-ssyk :*
SELECT
	db68.occupationNameID AS [occupation-name-id-68],
	db68term.term AS [term-68],
	db68.localeGroupID AS [parent-id-68],
	'ssyk-level-4' AS [parent-type]
FROM TaxonomyDB.dbo.OccupationName AS db68,
	TaxonomyDB.dbo.OccupationNameTerm AS db68term
WHERE db68.occupationNameID = db68term.occupationNameID
AND db68term.languageID = 502
AND NOT EXISTS
(SELECT localeGroupID, occupationNameID
	FROM TaxonomyDBVersion.dbo.OccupationName
	WHERE localeGroupID = db68.localeGroupID
	AND occupationNameID = db68.occupationNameID
	AND versionID = 67)

-- 157 replaced concepts (June 12)
-- :name get-replaced-occupation-name :*
-- :doc get replaced occupation names in version 68;
SELECT occupationNameID AS [replaced-id],
    term AS [replaced-term],
    occupationNameIDRef AS [replacing-id]
FROM TaxonomyDB.dbo.OccupationNameReference
WHERE countryID = 199
AND modificationDate > (
SELECT created
FROM TaxonomyDBVersion.dbo.Version
WHERE versionID = 67
)


----------------------------------- SSYK -------------------------------------------------


-- 0 new concepts (October 9)
-- :name get-new-ssyk-level-4 :*
-- :doc get ssyk level 4 that has been added in version 68 ;
SELECT db68.localeGroupID AS [ssyk-4-id],
db68term.term AS [term]
FROM TaxonomyDB.dbo.LocaleGroup AS db68,
TaxonomyDB.dbo.LocaleGroupTerm AS db68term
WHERE db68.localeGroupID = db68term.localeGroupID
AND db68term.languageID = 502
AND db68.localeGroupID NOT IN
(SELECT db67.localeGroupID
FROM TaxonomyDBVersion.dbo.LocaleGroup AS db67
WHERE db67.versionID = 67)


-- 0 new concepts (October 9)
-- :name get-updated-ssyk-level-4 :*
-- :doc get ssyk level 4 that has been changed in version 68 ;
SELECT  LocaleGroupTerm68.term as term68, LocaleGroupTerm67.term as term67, LocaleGroupTerm68.localeGroupID as id
FROM TaxonomyDB.dbo.LocaleGroup AS LocaleGroup68,
TaxonomyDB.dbo.LocaleGroupTerm AS LocaleGroupTerm68,
TaxonomyDBVersion.dbo.LocaleGroup AS LocaleGroup67 ,
TaxonomyDBVersion.dbo.LocaleGroupTerm AS LocaleGroupTerm67
WHERE
LocaleGroupTerm68.localeGroupID = LocaleGroup68.localeGroupID
AND LocaleGroupTerm67.localeGroupID = LocaleGroup67.localeGroupID
AND LocaleGroup68.localeGroupID = LocaleGroup67.localeGroupID
AND LocaleGroup67.versionID = 67
AND LocaleGroupTerm68.term != LocaleGroupTerm67.term COLLATE SQL_Latin1_General_CP1_CS_AS

-- 0 new concepts (October 9)
-- :name get-updated-ssyk-level-3 :*
-- :doc get ssyk level 3 that has been changed in version 68 ;
SELECT  [term-68].term as term68, [term-67].term as term67, [term-68].localeLevel3ID as id, [term-67].versionID
FROM
TaxonomyDB.dbo.LocaleLevel3Term AS [term-68],
TaxonomyDBVersion.dbo.LocaleLevel3Term AS [term-67]
WHERE
[term-68].localeLevel3ID = [term-67].localeLevel3ID
AND [term-68].term != [term-67].term COLLATE SQL_Latin1_General_CP1_CS_AS
AND [term-67].versionID = 67


-- 0 new concepts (October 9)
-- :name get-updated-ssyk-level-2 :*
-- :doc get ssyk level 2 that has been changed in version 68 ;
SELECT  [term-68].term as term68, [term-67].term as term67, [term-68].localeLevel2ID as id, [term-67].versionID
FROM
TaxonomyDB.dbo.LocaleLevel2Term AS [term-68],
TaxonomyDBVersion.dbo.LocaleLevel2Term AS [term-67]
WHERE
[term-68].localeLevel2ID = [term-67].localeLevel2ID
AND [term-68].term != [term-67].term COLLATE SQL_Latin1_General_CP1_CS_AS
AND [term-67].versionID = 67



-- 0 new concepts (October 9)
-- :name get-updated-ssyk-level-1 :*
-- :doc get ssyk level 1 that has been changed in version 68 ;
SELECT  [term-68].term as term68, [term-67].term as term67, [term-68].localeLevel1ID as id, [term-67].versionID
FROM
TaxonomyDB.dbo.LocaleLevel1Term AS [term-68],
TaxonomyDBVersion.dbo.LocaleLevel1Term AS [term-67]
WHERE
[term-68].localeLevel1ID = [term-67].localeLevel1ID
AND [term-68].term != [term-67].term COLLATE SQL_Latin1_General_CP1_CS_AS
AND [term-67].versionID = 67


----------------------------------- COLLECTIONS! ------------------------------------------

-- 3 (april 30 2020)
-- :name get-ais-occupation-collection :*
-- :doc get ais occupation collection ;
SELECT
      [collectionID] AS [collection-id], [name] AS [collection-name]
      FROM [TaxonomyDBVersion].[dbo].[OccupationCollection]
      WHERE versionID = 67



-- 376 (april 30 2020)
-- :name get-ais-occupation-collection-relations :*
-- :doc get ais occupation collection ;
SELECT
      [collectionID] as [collection-id],
            [occupationNameID] as [occupation-name-id]
              FROM [TaxonomyDBVersion].[dbo].[CollectionOccupation]
                WHERE versionID = 67



-- 2 (June 12)
-- (Obs! There are no collections migrated from 67 - these are the only migration scripts for collections)
-- :name get-occupation-collections :*
-- :doc get occupation collection ;
SELECT collectionID AS [collection-id], name AS [collection-name]
FROM TaxonomyDB.dbo.OccupationCollection
WHERE collectionID = 5
OR collectionID = 6

-- 466 (June 12)
-- :name get-occupation-collection-relations :*
-- :doc get relations between occupation collections and occupations ;
SELECT [collection-relations].collectionID AS [collection-id],
	[collection-relations].occupationNameID AS [occupation-name-id],
	[db-collections].name AS [collection-name]
FROM TaxonomyDB.dbo.CollectionOccupation AS [collection-relations],
	TaxonomyDB.dbo.OccupationCollection AS [db-collections]
WHERE ([db-collections].collectionID = 5
	OR [db-collections].collectionID = 6)
AND [db-collections].collectionID = [collection-relations].collectionID

------------------------------- OCCUPATION FIELDS --------------------------------------------------------------

-- 0 deprecated occupation fields (June 12)
-- :name get-deprecated-occupation-field :*
-- :doc Get deprecated occupation field in version 68 ;
SELECT [db-67].localeFieldID AS [occupation-field-id-67],
	[db-term-67].term AS [occupation-field-term-67],
	[occupation-field-description-67] =
    CASE WHEN [db-term-67].description like '%samråd%' THEN 'Militärt arbete' ELSE [db-term-67].description END
FROM TaxonomyDBVersion.dbo.LocaleField AS [db-67], TaxonomyDBVersion.dbo.LocaleFieldTerm AS [db-term-67]
WHERE
	[db-67].versionID = [db-term-67].versionID
AND [db-67].localeFieldID = [db-term-67].localeFieldID
AND [db-67].versionID = 67
AND [db-67].localeFieldID NOT IN
(SELECT LocaleField.localeFieldID
FROM TaxonomyDB.dbo.LocaleField)

-- 0 new occupation fields (June 12)
-- :name get-new-occupation-field :*
-- :doc Get new occupation field in version 68 ;
SELECT [db-68].localeFieldID AS [occupation-field-id-68],
	[db-term-68].term AS [occupation-field-term-68],
	[occupation-field-description-68] =
    CASE WHEN [db-term-68].description like '%samråd%' THEN 'Militärt arbete' ELSE [db-term-68].description END
FROM TaxonomyDB.dbo.LocaleField AS [db-68], TaxonomyDB.dbo.LocaleFieldTerm AS [db-term-68]
WHERE [db-68].localeFieldID = [db-term-68].localeFieldID
AND [db-68].localeFieldID NOT IN
(SELECT LocaleField.localeFieldID
FROM TaxonomyDBVersion.dbo.LocaleField
WHERE LocaleField.versionID = 67)

-- 21 updated occupation fields (June 12)
-- :name get-updated-occupation-field :*
-- :doc Get updated occupation field in version 68 ;
SELECT [db-68].localeFieldID AS [occupation-field-id-68],
	[db-67].localeFieldID AS [occupation-field-id-67],
	[db-term-68].term AS [occupation-field-term-68],
	[db-term-67].term AS [occupation-field-term-67],
	[occupation-field-description-68] =
    CASE WHEN [db-term-68].description like '%samråd%' THEN 'Militärt arbete' ELSE [db-term-68].description END,
	[occupation-field-description-67] =
    CASE WHEN [db-term-67].description like '%samråd%' THEN 'Militärt arbete' ELSE [db-term-67].description END
FROM TaxonomyDB.dbo.LocaleField AS [db-68],
	TaxonomyDB.dbo.LocaleFieldTerm AS [db-term-68],
	TaxonomyDBVersion.dbo.LocaleField AS [db-67],
	TaxonomyDBVersion.dbo.LocaleFieldTerm AS [db-term-67]
WHERE [db-68].localeFieldID = [db-term-68].localeFieldID
AND [db-term-68].localeFieldID = [db-term-67].localeFieldID
AND [db-term-67].localeFieldID = [db-67].localeFieldID
AND [db-term-67].versionID = 67
AND [db-67].versionID = 67
AND [db-term-67].languageID = 502
AND [db-term-68].languageID = 502
AND ([db-term-68].term != [db-term-67].term COLLATE SQL_Latin1_General_CP1_CS_AS
OR [db-term-68].description NOT LIKE [db-term-67].description COLLATE SQL_Latin1_General_CP1_CS_AS)

-- 0 deprecated occupation-field-to-ssyk-relations (June 12)
-- :name get-deprecated-occupation-field-relation-to-ssyk-4 :*
-- :doc Get deprecated relations between ssyk level 4 and occupation field ;
SELECT [db-67].localeGroupID AS [ssyk-4-id-67],
	[db-67].localeFieldID AS [parent-id-occupation-field-67],
	[db-term-67].term AS [ssyk-4-term-67]
FROM TaxonomyDBVersion.dbo.LocaleGroup AS [db-67],
	TaxonomyDBVersion.dbo.LocaleGroupTerm AS [db-term-67]
WHERE [db-67].versionID = [db-term-67].versionID
AND	[db-67].localeGroupID = [db-term-67].localeGroupID
AND [db-67].versionID = 67
AND [db-term-67].languageID = 502
AND [db-67].localeFieldID NOT IN
	(SELECT [db-68].localeFieldID AS [parent-id-occupation-field-68]
	FROM TaxonomyDB.dbo.LocaleGroup AS [db-68])

-- 4 new (June 12)
-- :name get-new-occupation-field-relation-to-ssyk-4 :*
-- :doc Get new relations between ssyk level 4 and occupation field ;
SELECT [db-68].localeGroupID AS [ssyk-4-id-68],
	[db-68].localeFieldID AS [parent-id-occupation-field-68],
	[db-term-68].term AS [ssyk-4-term-68]
FROM TaxonomyDB.dbo.LocaleGroup AS [db-68],
	TaxonomyDB.dbo.LocaleGroupTerm AS [db-term-68]
WHERE [db-68].localeGroupID = [db-term-68].localeGroupID
AND [db-term-68].languageID = 502
AND NOT EXISTS
	(SELECT [db-67].localeGroupID, [db-67].localeFieldID
	FROM TaxonomyDBVersion.dbo.LocaleGroup AS [db-67]
	WHERE [db-67].localeGroupID = [db-68].localeGroupID
	AND [db-67].localeFieldID = [db-68].localeFieldID
	AND [db-67].versionID = 67)

-- 4 deprecated (June 12)
-- :name get-deprecated-occupation-field-relation-to-ssyk-4 :*
SELECT [db-67].localeGroupID AS [ssyk-4-id-67],
	[db-67].localeFieldID AS [parent-id-occupation-field-67],
	[db-term-67].term AS [ssyk-4-term-67]
FROM TaxonomyDBVersion.dbo.LocaleGroup AS [db-67],
	TaxonomyDBVersion.dbo.LocaleGroupTerm AS [db-term-67]
WHERE [db-67].localeGroupID = [db-term-67].localeGroupID
AND [db-term-67].languageID = 502
AND [db-67].versionID = 67
AND [db-term-67].versionID = 67
AND NOT EXISTS
	(SELECT [db-68].localeGroupID, [db-68].localeFieldID
	FROM TaxonomyDB.dbo.LocaleGroup AS [db-68]
	WHERE [db-67].localeGroupID = [db-68].localeGroupID
	AND [db-67].localeFieldID = [db-68].localeFieldID)


-- 3426 (april 30 2020)
-- :name get-occupation-field-occupation-name-relation :*
SELECT   oft.occupationFieldID as [occupation-field-id], co.occupationNameID as [occupation-name-id]
FROM [TaxonomyDB].[dbo].[OccupationCollection] as oc,
     [TaxonomyDB].[dbo].[OccupationFieldTerm] as [oft],
     [TaxonomyDB].[dbo].[CollectionOccupation] as [co]
  where oft.languageID = 502
  and oc.name = oft.term
  and oc.collectionID = co.collectionID
  order by occupationNameID

--------------------------------- POPULAR SYNONYMS -------------------------------------------------------

-- 4 deprecated (June 12)
-- :name get-deprecated-synonyms :*
-- :doc get deprecated popular synonyms, id's existing in version 67 but not version 68
SELECT db67.popularSynonymID AS [synonym-id], db67.term AS [synonym-term]
FROM TaxonomyDBVersion.dbo.PopularSynonym AS db67
WHERE db67.versionID = 67
AND db67.popularSynonymID NOT IN
	(SELECT db68.popularSynonymID
	FROM TaxonomyDB.dbo.PopularSynonym AS db68)

-- 83 deprecated (June 12)
-- :name get-new-synonyms :*
-- :doc get new popular synonyms, id's existing in version 68 but not version 67
SELECT db68.popularSynonymID AS [synonym-id], db68.term AS [synonym-term]
FROM TaxonomyDB.dbo.PopularSynonym AS db68
WHERE db68.popularSynonymID NOT IN
	(SELECT db67.popularSynonymID
	FROM TaxonomyDBVersion.dbo.PopularSynonym AS db67
	WHERE db67.versionID = 67)

-- 1 deprecated (June 12)
-- :name get-updated-synonym-terms :*
-- :doc get updated popular synonyms where terms differ between version 67 and version 68
SELECT
	db68.popularSynonymID AS [synonym-id-68],
	db67.popularSynonymID AS [synonym-id-67],
	db68.term AS [synonym-term-68],
	db67.term AS [synonym-term-67]
FROM TaxonomyDB.dbo.PopularSynonym AS db68,
	TaxonomyDBVersion.dbo.PopularSynonym AS db67
WHERE db68.popularSynonymID = db67.popularSynonymID
AND db67.versionID = 67
AND db68.term != db67.term COLLATE SQL_Latin1_General_CP1_CS_AS

-- 26 (June 12)
-- :name get-deprecated-synonym-relation-to-occupation :*
-- :doc get popular synonyms where relation to occupation no longer exist in version 68 ;
SELECT [occupation-synonym-67].occupationNameID AS [occupation-name-id-67],
	[synonym-67].popularSynonymID AS [synonym-id-67],
	[synonym-67].term AS [synonym-term-67]
FROM TaxonomyDBVersion.dbo.OccupationNameSynonym AS [occupation-synonym-67],
    TaxonomyDBVersion.dbo.PopularSynonym AS [synonym-67]
WHERE [occupation-synonym-67].popularSynonymID = [synonym-67].popularSynonymID
AND [occupation-synonym-67].versionID = 67
AND [synonym-67].versionID = 67
AND NOT EXISTS
	(SELECT
		[occupation-synonym-68].occupationNameID,
		[synonym-68].popularSynonymID
	FROM TaxonomyDB.dbo.OccupationNameSynonym AS [occupation-synonym-68],
    TaxonomyDB.dbo.PopularSynonym AS [synonym-68]
	WHERE [occupation-synonym-68].occupationNameID = [occupation-synonym-67].occupationNameID
	AND [synonym-68].popularSynonymID = [synonym-67].popularSynonymID)

-- 141 (June 12)
-- :name get-new-synonym-relation-to-occupation :*
-- :doc get popular synonyms where relation to occupation does not exist in version 67 ;
SELECT [occupation-synonym-68].occupationNameID AS [occupation-name-id-68],
	[synonym-68].popularSynonymID AS [synonym-id-68],
	[synonym-68].term AS [synonym-term-68]
FROM TaxonomyDB.dbo.OccupationNameSynonym AS [occupation-synonym-68],
    TaxonomyDB.dbo.PopularSynonym AS [synonym-68]
WHERE [occupation-synonym-68].popularSynonymID = [synonym-68].popularSynonymID
AND NOT EXISTS
	(SELECT
		[occupation-synonym-67].occupationNameID,
		[synonym-67].popularSynonymID
	FROM TaxonomyDBVersion.dbo.OccupationNameSynonym AS [occupation-synonym-67],
    TaxonomyDBVersion.dbo.PopularSynonym AS [synonym-67]
	WHERE [occupation-synonym-68].occupationNameID = [occupation-synonym-67].occupationNameID
	AND [synonym-68].popularSynonymID = [synonym-67].popularSynonymID
	AND [occupation-synonym-67].versionID = 67
	AND [synonym-67].versionID = 67)


--------------------------------- RELATIONS BETWEEN ISCO, SSYK & SKILLS --------------------------------------------

-- 223 deprecated (June 12)
-- :name get-deprecated-isco-4-skill-relation :*
-- :doc Get deprecated relations between ISCO level 4 and skills ;
SELECT skillID AS [skill-id-67],
	occupationGroupID AS [isco-4-id-67]
  FROM TaxonomyDBVersion.dbo.OccupationGroup_Skill AS db67
WHERE versionID = 67
AND NOT EXISTS
(SELECT skillID, occupationGroupID
	FROM TaxonomyDB.dbo.OccupationGroup_Skill
	WHERE skillID = db67.skillID
	AND occupationGroupID = db67.occupationGroupID)

-- 497 (June 12) --
-- :name get-new-isco-4-skill-relation :*
-- :doc Get new relations between ISCO level 4 and skills ;
SELECT skillID AS [skill-id-68],
	occupationGroupID AS [isco-4-id-68]
  FROM TaxonomyDB.dbo.OccupationGroup_Skill AS db68
WHERE NOT EXISTS
(SELECT skillID, occupationGroupID
	FROM TaxonomyDBVersion.dbo.OccupationGroup_Skill
	WHERE versionID = 67
	AND skillID = db68.skillID
	AND occupationGroupID = db68.occupationGroupID)



-- :name get-ssyk-4-skill-relation-swedish-v67 :*
-- :doc get the swedish skills related to ssyk v67
SELECT lgs.skillID as [skill-id],
lgs.localeGroupID as [ssyk-4-id]
FROM [TaxonomyDBVersion].[dbo].[LocaleGroup_Skill] as lgs
WHERE lgs.versionID = 67

-- :name get-ssyk-4-skill-relation-swedish-current-db :*
-- :doc get the swedish skills related to ssyk from the current db
SELECT lgs.skillID as [skill-id],
lgs.localeGroupID as [ssyk-4-id]
FROM [TaxonomyDB].[dbo].[LocaleGroup_Skill] as lgs


-- :name get-ssyk-4-skill-relation-inherited-v67 :*
-- :doc get ssyk skill relations inherited from isco v67
SELECT il.[localeGroupID] as [ssyk-4-id],  ogs.skillID as [skill-id]
FROM [TaxonomyDBVersion].[dbo].[ISCOLocale] as il,
[TaxonomyDBVersion].[dbo].OccupationGroup_Skill as ogs
where il.versionID = 67
and ogs.versionID = 67
and il.occupationGroupID = ogs.occupationGroupID

-- :name get-ssyk-4-skill-relation-inherited-current-db
-- :doc get ssyk skill relations inherited from isco
SELECT il.[localeGroupID] as [ssyk-4-id], ogs.skillID as [skill-id]
FROM [TaxonomyDB].[dbo].[ISCOLocale] as il,
[TaxonomyDB].[dbo].OccupationGroup_Skill as ogs
where il.occupationGroupID = ogs.occupationGroupID

-- :name get-ssyk-4-relation-restricted-v67
SELECT [skillID] as [skill-id]
,[localeGroupID] as [ssyk-4-id]
FROM [TaxonomyDBVersion].[dbo].[SkillRestriction]
where versionID = 67

-- :name get-ssyk-4-relation-restricted-current-db
SELECT [skillID] as [skill-id]
,[localeGroupID] as [ssyk-4-id]
FROM [TaxonomyDB].[dbo].[SkillRestriction]



-- 1 (June 12) --
-- :name get-deprecated-ssyk-4-skill-relation :*
-- :doc Get deprecated relations between SSYK level 4 and skills ;
SELECT [db67].skillid       AS [skill-id],
       [db67].localegroupid AS [ssyk-4-id]
       FROM   taxonomydbversion.dbo.localegroup_skill AS [db67]
       WHERE  [db67].versionid = 67
       AND NOT EXISTS (SELECT [db].skillid       AS [skill-id],
                       [db].localegroupid AS [ssyk-4-id]
                       FROM   taxonomydb.dbo.localegroup_skill AS [db]
                       WHERE  skillid = db67.skillid
                       AND localegroupid = db67.localegroupid)
UNION ALL
       SELECT ogs67.skillid      AS [skill-id],
       il67.localegroupid AS [ssyk-4-id]
       FROM   taxonomydbversion.dbo.iscolocale AS il67,
              taxonomydbversion.dbo.occupationgroup_skill AS ogs67
       WHERE  ogs67.occupationgroupid = il67.occupationgroupid
       AND ogs67.versionid = 67
       AND il67.versionid = 67
       AND NOT EXISTS (SELECT ogs.skillid      AS [skill-id],
                       il.localegroupid AS [ssyk-4-id]
                      FROM   taxonomydb.dbo.iscolocale AS il,
                             taxonomydb.dbo.occupationgroup_skill AS ogs
                      WHERE  ogs.occupationgroupid = il.occupationgroupid)
EXCEPT
       SELECT sr.skillid       AS [skill-id],
              sr.localegroupid AS [ssyk-4-id]
       FROM   taxonomydbversion.dbo.skillrestriction AS sr
       WHERE  sr.versionid = 67
       AND NOT EXISTS (SELECT sr.skillid       AS [skill-id],
                              sr.localegroupid AS [ssyk-4-id]
                       FROM   taxonomydb.dbo.skillrestriction AS sr)





-- 0 (June 12) --
-- :name get-new-ssyk-4-skill-relation :*
-- :doc Get new relations between SSYK level 4 and skills ;
SELECT skillID AS [skill-id-68],
	localeGroupID AS [ssyk-4-id-68]
FROM TaxonomyDB.dbo.LocaleGroup_Skill AS db68
WHERE NOT EXISTS
(SELECT skillID, localeGroupID
	FROM TaxonomyDBVersion.dbo.LocaleGroup_Skill
	WHERE skillID = db68.skillID
	AND localeGroupID = db68.localeGroupID
	AND versionID = 67)

-- 0 (June 12) --
-- :name get-deprecated-ssyk-isco-relation :*
-- :doc Get deprecated relations between SSYK level 4 and ISCO level 4 ;
SELECT occupationGroupID AS [isco-4-id-67],
	localeGroupID AS [ssyk-4-id-67]
FROM TaxonomyDBVersion.dbo.ISCOLocale AS db67
WHERE versionID = 67
AND NOT EXISTS
(SELECT occupationGroupID, localeGroupID
	FROM TaxonomyDB.dbo.ISCOLocale
	WHERE occupationGroupID = db67.occupationGroupID
	AND localeGroupID = db67.localeGroupID)

-- 0 (June 12) --
-- :name get-new-ssyk-isco-relation :*
-- :doc Get new relations between SSYK level 4 and ISCO level 4 ;
SELECT occupationGroupID AS [isco-4-id-68],
	localeGroupID AS [ssyk-4-id-68]
FROM TaxonomyDB.dbo.ISCOLocale AS db68
WHERE NOT EXISTS
(SELECT occupationGroupID, localeGroupID
	FROM TaxonomyDBVersion.dbo.ISCOLocale
	WHERE occupationGroupID = db68.occupationGroupID
	AND localeGroupID = db68.localeGroupID
	AND versionID = 67)

---------------DRIVING LICENCE---(No differences between versions!!!)-------------------------------------

-- 0 (June 12)
-- :name get-deprecated-driving-licence :*
-- :doc get deprecated driving licences, id's existing in version 67 but not version 68
SELECT [db-67-term].drivingLicenceID AS [id-67],
	[db-67-term].term AS [term-67],
	[db-67].displaySortOrder AS [sort-67],
	[db-67].drivingLicenceCode AS [d-licence-code-67]
FROM TaxonomyDBVersion.dbo.DrivingLicenceTerm AS [db-67-term], TaxonomyDBVersion.dbo.DrivingLicence AS [db-67]
WHERE [db-67-term].versionID = 67
AND [db-67-term].languageID = 502
AND [db-67-term].drivingLicenceID NOT IN
	(SELECT [db-68-term].drivingLicenceID
	FROM TaxonomyDB.dbo.DrivingLicenceTerm AS [db-68-term]
	WHERE [db-68-term].languageID = 502)

-- 0 (June 12)
-- :name get-new-driving-licence :*
-- :doc get new driving licences, id's existing in version 68 but not in version 67
SELECT [db-68-term].drivingLicenceID AS [id-68-term],
	[db-68-term].term AS [term-68-term],
	[db-68].displaySortOrder AS [sort-68],
	[db-68].drivingLicenceCode AS [d-licence-code-68]
FROM TaxonomyDB.dbo.DrivingLicenceTerm AS [db-68-term], TaxonomyDB.dbo.DrivingLicence AS [db-68]
WHERE [db-68-term].languageID = 502
AND [db-68-term].drivingLicenceID NOT IN
	(SELECT [db-67-term].drivingLicenceID
	FROM TaxonomyDBVersion.dbo.DrivingLicenceTerm AS [db-67-term]
	WHERE [db-67-term].versionID = 67
	AND [db-67-term].languageID = 502)

-- 0 (June 12)
-- :name get-updated-driving-licence :*
-- :doc get updated driving licences where some value differs between version 68 and version 67
SELECT [db-68-term].drivingLicenceID AS [id-68],
	[db-67-term].drivingLicenceID AS [id-67],
	[db-68-term].term AS [term-68],
	[db-67-term].term AS [term-67],
	[db-68].displaySortOrder AS [sort-68],
	[db-67].displaySortOrder AS [sort-67],
	[db-68].drivingLicenceCode AS [d-licence-code-68],
	[db-67].drivingLicenceCode AS [d-licence-code-67],
	[db-68-term].description AS [description-68],
	[db-67-term].description AS [description-67]
FROM TaxonomyDB.dbo.DrivingLicenceTerm AS [db-68-term],
	TaxonomyDBVersion.dbo.DrivingLicenceTerm AS [db-67-term],
	TaxonomyDB.dbo.DrivingLicence AS [db-68],
	TaxonomyDBVersion.dbo.DrivingLicence AS [db-67]
WHERE [db-68-term].drivingLicenceID = [db-67-term].drivingLicenceID
AND [db-67-term].drivingLicenceID = [db-67].drivingLicenceID
AND [db-67].drivingLicenceID = [db-68].drivingLicenceID
AND [db-68-term].drivingLicenceID = [db-68].drivingLicenceID
AND [db-68-term].languageID = 502
AND [db-67-term].languageID = 502
AND [db-67-term].versionID = 67
AND [db-67].versionID = 67
AND ([db-67-term].term != [db-68-term].term
OR [db-67].displaySortOrder != [db-68].displaySortOrder
OR [db-67].drivingLicenceCode != [db-68].drivingLicenceCode COLLATE SQL_Latin1_General_CP1_CS_AS
OR [db-67-term].description NOT LIKE [db-68-term].description)

------------------- Driver licence combinations is already from 68. ------------------------------------

-----------------------EMPLOYMENT DURATION -------------------------------------------------------------

-- 0 (June 12)
-- :name get-deprecated-employment-duration :*
-- :doc get deprecated employment durations, id's existing in version 67 but not in version 68
SELECT [db-67-term].anställningsvaraktighetsID AS [id-67-term],
    [db-67-term].beteckning AS [term-67],
    [db-67].sortering AS [sortering-67],
    [db-67].EURESKod AS [eures-67]
FROM TaxonomiDBSvenskVersion.dbo.AnställningsvaraktighetTerm AS [db-67-term], TaxonomiDBSvenskVersion.dbo.Anställningsvaraktighet AS [db-67]
WHERE [db-67-term].språkID = 502
AND [db-67-term].anställningsvaraktighetsID = [db-67].anställningsvaraktighetsID
AND [db-67-term].anställningsvaraktighetsID NOT IN
	(SELECT [db-68-term].anställningsvaraktighetsID
	FROM TaxonomiDBSvensk.dbo.AnställningsvaraktighetTerm AS [db-68-term]
	WHERE [db-68-term].språkID = 502)

-- 1 (June 12)
-- :name get-new-employment-duration :*
-- :doc get new employment durations, id's existing in version 68 but not in version 67
SELECT [db-68-term].anställningsvaraktighetsID AS [id-68],
	[db-68-term].beteckning AS [term-68],
	[db-68].sortering AS [sortering-68],
	[db-68].EURESKod AS [eures-68]
FROM TaxonomiDBSvensk.dbo.AnställningsvaraktighetTerm AS [db-68-term], TaxonomiDBSvensk.dbo.Anställningsvaraktighet AS [db-68]
WHERE [db-68-term].språkID = 502
AND [db-68-term].anställningsvaraktighetsID = [db-68].anställningsvaraktighetsID
AND [db-68-term].anställningsvaraktighetsID NOT IN
	(SELECT [db-67-term].anställningsvaraktighetsID
	FROM TaxonomiDBSvenskVersion.dbo.AnställningsvaraktighetTerm AS [db-67-term]
	WHERE [db-67-term].språkID = 502)

-- 5 (June 12)
-- :name get-updated-employment-duration :*
-- :doc get updated employment durations where some value differs between version 68 and version 67
SELECT [db-68-term].anställningsvaraktighetsID  AS [id-68],
	[db-68-term].beteckning AS [term-68],
	[db-68].sortering AS [sortering-68],
	[db-68].EURESKod AS [eures-68],
	[db-67-term].anställningsvaraktighetsID AS [id-67],
	[db-67-term].beteckning [term-67],
	[db-67].sortering AS [sortering-67],
	[db-67].EURESKod AS [eures-67]
FROM TaxonomiDBSvensk.dbo.AnställningsvaraktighetTerm AS [db-68-term],
	TaxonomiDBSvenskVersion.dbo.AnställningsvaraktighetTerm AS [db-67-term],
	TaxonomiDBSvensk.dbo.Anställningsvaraktighet AS [db-68],
	TaxonomiDBSvenskVersion.dbo.Anställningsvaraktighet AS [db-67]
WHERE [db-68-term].anställningsvaraktighetsID = [db-67-term].anställningsvaraktighetsID
AND [db-67-term].anställningsvaraktighetsID = [db-67].anställningsvaraktighetsID
AND [db-67].anställningsvaraktighetsID = [db-68].anställningsvaraktighetsID
AND [db-68].anställningsvaraktighetsID = [db-68-term].anställningsvaraktighetsID
AND [db-68-term].språkID = 502
AND [db-67-term].språkID = 502
AND ([db-67-term].beteckning != [db-68-term].beteckning COLLATE SQL_Latin1_General_CP1_CS_AS
OR [db-67].sortering != [db-68].sortering
OR [db-67].EURESKod != [db-68].EURESKod COLLATE SQL_Latin1_General_CP1_CS_AS)

----------------------------EMPLOYMENT TYPE---(No difference between versions!!!)-----------------------------------

-- 0 (June 12)
-- :name get-deprecated-employment-type :*
-- :doc get deprecated employment types, id's existing in version 67 but not in version 68
SELECT [db-67-term].AnstallningTypJobbID AS [id-67],
    [db-67-term].beteckning AS [term-67],
    [db-67].iSortering AS [sortering-67]
FROM TaxonomiDBSvenskVersion.dbo.AnstallningTypJobbTerm AS [db-67-term],
	TaxonomiDBSvenskVersion.dbo.AnstallningTypJobb AS [db-67]
WHERE [db-67-term].språkID = 502
AND [db-67-term].AnstallningTypJobbID = [db-67].AnstallningTypJobbID
AND [db-67-term].AnstallningTypJobbID NOT IN
	(SELECT [db-68-term].AnstallningTypJobbID
	FROM TaxonomiDBSvensk.dbo.AnstallningTypJobbTerm AS [db-68-term]
	WHERE [db-68-term].språkID = 502)

-- 0 (June 12)
-- :name get-new-employment-type :*
-- :doc get new employment types, id's existing in version 68 but not in version 67
SELECT [db-68-term].AnstallningTypJobbID AS [id-68],
    [db-68-term].beteckning AS [term-68],
    [db-68].iSortering AS [sortering-68]
FROM TaxonomiDBSvensk.dbo.AnstallningTypJobbTerm AS [db-68-term],
	TaxonomiDBSvensk.dbo.AnstallningTypJobb AS [db-68]
WHERE [db-68-term].språkID = 502
AND [db-68-term].AnstallningTypJobbID = [db-68].AnstallningTypJobbID
AND [db-68-term].AnstallningTypJobbID NOT IN
	(SELECT [db-67-term].AnstallningTypJobbID
	FROM TaxonomiDBSvenskVersion.dbo.AnstallningTypJobbTerm AS [db-67-term]
	WHERE [db-67-term].språkID = 502)

-- 0 (June 12)
-- :name get-updated-employment-type-term :*
-- :doc get updated employment types where term/label differs between version 68 and version 67
SELECT [db-68-term].AnstallningTypJobbID  AS [id-68],
	[db-68-term].beteckning AS [term-68],
	[db-68].iSortering AS [sortering-68],
	[db-67-term].AnstallningTypJobbID AS [id-67],
	[db-67-term].beteckning [term-67],
	[db-67].iSortering AS [sortering-67]
FROM TaxonomiDBSvensk.dbo.AnstallningTypJobbTerm AS [db-68-term],
	TaxonomiDBSvenskVersion.dbo.AnstallningTypJobbTerm AS [db-67-term],
	TaxonomiDBSvensk.dbo.AnstallningTypJobb AS [db-68],
	TaxonomiDBSvenskVersion.dbo.AnstallningTypJobb AS [db-67]
WHERE [db-68-term].AnstallningTypJobbID = [db-67-term].AnstallningTypJobbID
AND [db-67-term].AnstallningTypJobbID = [db-67].AnstallningTypJobbID
AND [db-67].AnstallningTypJobbID = [db-68].AnstallningTypJobbID
AND [db-68].AnstallningTypJobbID = [db-68-term].AnstallningTypJobbID
AND [db-68-term].språkID = 502
AND [db-67-term].språkID = 502
AND ([db-67-term].beteckning != [db-68-term].beteckning COLLATE SQL_Latin1_General_CP1_CS_AS
OR [db-67].iSortering != [db-68].iSortering)

-----------------------------------------GEOGRAPHIC PLACES----------------------------------------------------------

-------------------------CONTINENTS---(No difference!)----------------------------------------------------

-- 0 (June 12)
-- :name get-deprecated-continent :*
-- :doc get deprecated continent, id's existing in version 67 but not in version 68
SELECT [db-67].continentID AS [id-67], [db-67].term AS [term-67]
FROM TaxonomyDBVersion.dbo.ContinentTerm AS [db-67]
WHERE [db-67].languageID = 502
AND [db-67].versionID = 67
AND [db-67].continentID NOT IN
	(SELECT [db-68].continentID
	FROM TaxonomyDB.dbo.ContinentTerm AS [db-68]
	WHERE [db-68].languageID = 502)

-- 0 (June 12)
-- :name get-new-continent :*
-- :doc get new continent, id's existing in version 68 but not in version 67
SELECT [db-68].continentID AS [id-68], [db-68].term AS [term-68]
FROM TaxonomyDB.dbo.ContinentTerm AS [db-68]
WHERE [db-68].languageID = 502
AND [db-68].continentID NOT IN
	(SELECT [db-67].continentID
	FROM TaxonomyDBVersion.dbo.ContinentTerm AS [db-67]
	WHERE [db-67].languageID = 502
	AND [db-67].versionID = 67)

-- 0 (June 12)
-- :name get-updated-continent-term :*
-- :doc get updated continent where term/label differs between version 68 and version 67
SELECT [db-68].continentID AS [id-68],
	[db-68].term AS [term-68],
	[db-67].continentID AS [id-67],
	[db-67].term [term-67]
FROM TaxonomyDB.dbo.ContinentTerm AS [db-68], TaxonomyDBVersion.dbo.ContinentTerm AS [db-67]
WHERE [db-68].continentID = [db-67].continentID
AND [db-67].versionID = 67
AND [db-68].languageID = 502
AND [db-67].languageID = 502
AND [db-67].term != [db-68].term COLLATE SQL_Latin1_General_CP1_CS_AS

----------------------------------------- COUNTRIES --------------------------------------------------

-- 0 (June 12)
-- :name get-deprecated-country :*
-- :doc get deprecated countries, id's existing in version 67 but not in version 68
SELECT [db-67-term].countryID AS [id-67], [db-67-term].term AS [term-67], [db-67].countryCode AS [country-code-67]
FROM TaxonomyDBVersion.dbo.CountryTerm AS [db-67-term], TaxonomyDBVersion.dbo.Country AS [db-67]
WHERE [db-67-term].languageID = 502
AND [db-67-term].versionID = 67
AND [db-67-term].countryID NOT IN
	(SELECT [db-68-term].countryID
	FROM TaxonomyDB.dbo.CountryTerm AS [db-68-term]
	WHERE [db-68-term].languageID = 502)

-- 0 (June 12)
-- :name get-new-country :*
-- :doc get new countries, id's existing in version 68 but not in version 67
SELECT [db-68-term].countryID AS [id-68], [db-68-term].term AS [term-68], [db-68].countryCode AS [country-code-68]
FROM TaxonomyDB.dbo.CountryTerm AS [db-68-term], TaxonomyDB.dbo.Country AS [db-68]
WHERE [db-68-term].languageID = 502
AND [db-68-term].countryID NOT IN
	(SELECT [db-67-term].countryID
	FROM TaxonomyDB.dbo.CountryTerm AS [db-67-term]
	WHERE [db-67-term].languageID = 502)

-- 2 (June 12)
-- :name get-updated-country :*
-- :doc get updated countries where term/label differs between version 68 and version 67
SELECT [db-68-term].countryID  AS [id-68],
	[db-68-term].term AS [term-68],
	[db-68].countryCode AS [country-code-68],
	[db-67-term].countryID AS [id-67],
	[db-67-term].term [term-67],
	[db-67].countryCode AS [country-code-67]
FROM TaxonomyDB.dbo.CountryTerm AS [db-68-term],
	TaxonomyDBVersion.dbo.CountryTerm AS [db-67-term],
	TaxonomyDB.dbo.Country AS [db-68],
	TaxonomyDBVersion.dbo.Country AS [db-67]
WHERE [db-68-term].countryID = [db-67-term].countryID
AND [db-67-term].countryID = [db-67].countryID
AND [db-67].countryID = [db-68].countryID
AND [db-68].countryID = [db-68-term].countryID
AND [db-67-term].versionID = 67
AND [db-67].versionID = 67
AND [db-68-term].languageID = 502
AND [db-67-term].languageID = 502
AND ([db-67-term].term != [db-68-term].term COLLATE SQL_Latin1_General_CP1_CS_AS
OR [db-67].countryCode != [db-68].countryCode COLLATE SQL_Latin1_General_CP1_CS_AS)

-- 1 (June 12)
-- :name get-deprecated-country-relation-to-continent :*
-- :doc get deprecated relations between countries and continents
SELECT [db-67].countryID AS [country-id-67],
	[db-67].continentID AS [parent-continent-id-67]
FROM TaxonomyDBVersion.dbo.Country AS [db-67]
WHERE [db-67].versionID = 67
AND NOT EXISTS
	(SELECT
		countryID, continentID
	FROM TaxonomyDB.dbo.Country
	WHERE countryID = [db-67].countryID
	AND continentID = [db-67].continentID)

-- 1 (June 12)
-- :name get-new-country-relation-to-continent :*
-- :doc get new relations between countries and continents
SELECT [db-68].countryID AS [country-id-68],
	[db-68].continentID AS [parent-continent-id-68]
FROM TaxonomyDB.dbo.Country AS [db-68]
WHERE NOT EXISTS
	(SELECT
		countryID, continentID
	FROM TaxonomyDBVersion.dbo.Country
	WHERE countryID = [db-68].countryID
	AND continentID = [db-68].continentID
	AND versionID = 67)

------------------------------------REGIONS---(No difference!)--------------------------------------------------

-- 0 (June 12)
-- :name get-deprecated-region :*
-- :doc get deprecated regions, id's existing in version 67 but not in version 68
SELECT [db-67-term].EURegionID AS [id-67], [db-67-term].term AS [term-67], [db-67].NUTSCodeLevel3 AS [nuts-67]
FROM TaxonomyDBVersion.dbo.EURegionTerm AS [db-67-term], TaxonomyDBVersion.dbo.EURegion AS [db-67]
WHERE [db-67-term].languageID = 502
AND [db-67-term].versionID = 67
AND [db-67-term].EURegionID NOT IN
	(SELECT [db-68-term].EURegionID
	FROM TaxonomyDB.dbo.EURegionTerm AS [db-68-term]
	WHERE [db-68-term].languageID = 502)

-- 0 (June 12)
-- :name get-new-region :*
-- :doc get new regions, id's existing in version 68 but not in version 67
SELECT [db-68-term].EURegionID AS [id-68], [db-68-term].term AS [term-68], [db-68].NUTSCodeLevel3 AS [nuts-68]
FROM TaxonomyDB.dbo.EURegionTerm AS [db-68-term], TaxonomyDB.dbo.EURegion AS [db-68]
WHERE [db-68-term].EURegionID = 502
AND [db-68-term].EURegionID NOT IN
	(SELECT [db-67-term].EURegionID
	FROM TaxonomyDB.dbo.EURegionTerm AS [db-67-term]
	WHERE [db-67-term].languageID = 502)

-- 0 (June 12)
-- :name get-updated-region-term :*
-- :doc get updated regions where term/label differs between version 68 and version 67
SELECT [db-68-term].EURegionID  AS [id-68],
	[db-68-term].term AS [term-68],
	[db-68].NUTSCodeLevel3 AS [nuts-68],
	[db-67-term].EURegionID AS [id-67],
	[db-67-term].term [term-67],
	[db-67].NUTSCodeLevel3 AS [nuts-67],
	[db-67].CountryID AS [country-id-67],
    [db-68].CountryID AS [country-id-68]
FROM TaxonomyDB.dbo.EURegionTerm AS [db-68-term],
	TaxonomyDBVersion.dbo.EURegionTerm AS [db-67-term],
	TaxonomyDB.dbo.EURegion AS [db-68],
	TaxonomyDBVersion.dbo.EURegion AS [db-67]
WHERE [db-68-term].EURegionID = [db-67-term].EURegionID
AND [db-67-term].EURegionID = [db-67].EURegionID
AND [db-67].EURegionID = [db-68].EURegionID
AND [db-68].EURegionID = [db-68-term].EURegionID
AND [db-67-term].versionID = 67
AND [db-67].versionID = 67
AND [db-68-term].languageID = 502
AND [db-67-term].languageID = 502
AND ([db-67-term].term != [db-68-term].term COLLATE SQL_Latin1_General_CP1_CS_AS
OR [db-67].NUTSCodeLevel3 != [db-68].NUTSCodeLevel3 COLLATE SQL_Latin1_General_CP1_CS_AS)

-- 0 (June 12)
-- :name get-deprecated-region-relation-to-parent :*
-- :doc get deprecated relations between regions and countries
SELECT [db-67].EURegionID AS [region-67],
	[db-67].countryID AS [parent-id-67]
FROM TaxonomyDBVersion.dbo.EURegion AS [db-67]
WHERE [db-67].versionID = 67
AND NOT EXISTS
	(SELECT
		countryID, EURegionID
	FROM TaxonomyDB.dbo.EURegion
	WHERE countryID = [db-67].countryID
	AND EURegionID = [db-67].EURegionID)

-- 0 (June 12)
-- :name get-new-region-relation-to-parent :*
-- :doc get new relations between regions and countries
SELECT [db-68].EURegionID AS [region-68],
	[db-68].countryID AS [parent-id-68]
FROM TaxonomyDB.dbo.EURegion AS [db-68]
WHERE NOT EXISTS
	(SELECT
		countryID, EURegionID
	FROM TaxonomyDBVersion.dbo.EURegion
	WHERE countryID = [db-68].countryID
	AND EURegionID = [db-68].EURegionID
	AND versionID = 67)

----------------------------MUNICIPALITIES--(No difference between versions!!!)------------------------------------

-- 0 (June 12)
-- :name get-deprecated-municipality :*
-- :doc get deprecated municipalities, id's existing in version 67 but not in version 68
SELECT [db-67].municipalityID AS [id-67], [db-67].term AS [term-67]
FROM TaxonomyDBVersion.dbo.MunicipalityTerm AS [db-67]
WHERE [db-67].languageID = 502
AND [db-67].versionID = 67
AND [db-67].municipalityID NOT IN
	(SELECT [db-68].municipalityID
	FROM TaxonomyDB.dbo.MunicipalityTerm AS [db-68]
	WHERE [db-68].languageID = 502)

-- 0 (June 12)
-- :name get-new-municipality :*
-- :doc get new municipalities, id's existing in version 68 but not in version 67
SELECT [db-68].municipalityID AS [id-68], [db-68].term AS [term-68]
FROM TaxonomyDB.dbo.MunicipalityTerm AS [db-68]
WHERE [db-68].languageID = 502
AND [db-68].municipalityID NOT IN
	(SELECT [db-67].municipalityID
	FROM TaxonomyDBVersion.dbo.MunicipalityTerm AS [db-67]
	WHERE [db-67].languageID = 502
	AND [db-67].versionID = 67)

-- 0 (June 12)
-- :name get-updated-municipality-term :*
-- :doc get updated municipalities where term/label differs between version 68 and version 67
SELECT [db-68-term].municipalityID AS [id-68],
	[db-68-term].term AS [term-68],
	[db-67-term].municipalityID AS [id-67],
	[db-67-term].term [term-67],
	[db-67].EURegionID AS [region-id-67],
	[db-68].EURegionID AS [region-id-68]
FROM TaxonomyDB.dbo.MunicipalityTerm AS [db-68-term],
	TaxonomyDBVersion.dbo.MunicipalityTerm AS [db-67-term],
	TaxonomyDB.dbo.Municipality AS [db-68],
	TaxonomyDBVersion.dbo.Municipality AS [db-67]
WHERE [db-68-term].municipalityID = [db-67-term].municipalityID
AND [db-67-term].municipalityID = [db-67].municipalityID
AND [db-67].municipalityID = [db-68].municipalityID
AND [db-67-term].versionID = 67
AND [db-67].versionID = 67
AND [db-68-term].languageID = 502
AND [db-67-term].languageID = 502
AND [db-67-term].term != [db-68-term].term COLLATE SQL_Latin1_General_CP1_CS_AS

-- 0 (June 12)
-- :name get-new-municipality-relation-to-parent :*
-- :doc get new relations between municipalities and regions
SELECT [db-68].municipalityID AS [municipality-68],
	[db-68].EURegionID AS [parent-id-68]
FROM TaxonomyDB.dbo.Municipality AS [db-68]
WHERE NOT EXISTS
	(SELECT
		municipalityID, EURegionID
	FROM TaxonomyDBVersion.dbo.Municipality
	WHERE municipalityID = [db-68].municipalityID
	AND EURegionID = [db-68].EURegionID
	AND versionID = 67)

-- 0 (June 12)
-- :name get-deprecated-municipality-relation-to-parent :*
-- :doc get deprecated relations between municipalities and regions
SELECT [db-67].municipalityID AS [municipality-67],
	[db-67].EURegionID AS [parent-id-67]
FROM TaxonomyDBVersion.dbo.Municipality AS [db-67]
WHERE versionID = 67
AND NOT EXISTS
	(SELECT
		municipalityID, EURegionID
	FROM TaxonomyDB.dbo.Municipality
	WHERE municipalityID = [db-67].municipalityID
	AND EURegionID = [db-67].EURegionID)

-------------------------------LANGUAGE--(No difference between versions!!!)-----------------------------------------

-- 0 (June 12)
-- :name get-deprecated-language :*
-- :doc get deprecated languages, id's existing in version 67 but not in version 68
SELECT [db-67].languageID AS [id-67], [db-67].term AS [term-67]
FROM TaxonomyDBVersion.dbo.LanguageTerm AS [db-67]
WHERE [db-67].translationLanguageID = 502
AND [db-67].versionID = 67
AND [db-67].languageID NOT IN
	(SELECT [db-68].languageID
	FROM TaxonomyDB.dbo.LanguageTerm AS [db-68]
	WHERE [db-68].translationLanguageID = 502)

-- 0 (June 12)
-- :name get-new-language :*
-- :doc get new languages, id's existing in version 68 but not in version 67
SELECT [db-68].languageID AS [id-68], [db-68].term AS [term-68]
FROM TaxonomyDB.dbo.LanguageTerm AS [db-68]
WHERE [db-68].translationLanguageID = 502
AND [db-68].languageID NOT IN
	(SELECT [db-67].languageID
	FROM TaxonomyDBVersion.dbo.LanguageTerm AS [db-67]
	WHERE [db-67].translationLanguageID = 502
	AND [db-67].versionID = 67)

-- 0 (June 12)
-- :name get-updated-language-term :*
-- :doc get updated languages where term/label differs between version 68 and version 67
SELECT [db-68].languageID  AS [id-68],
	[db-68].term AS [term-68],
	[db-67].languageID AS [id-67],
	[db-67].term [term-67]
FROM TaxonomyDB.dbo.LanguageTerm AS [db-68], TaxonomyDBVersion.dbo.LanguageTerm AS [db-67]
WHERE [db-68].languageID = [db-67].languageID
AND [db-67].versionID = 67
AND [db-68].translationLanguageID = 502
AND [db-67].translationLanguageID = 502
AND [db-67].term != [db-68].term


------------------------------------LANGUAGE LEVEL-- (No differences between versions!!!)-----------------------------

-- 0 (June 12)
-- :name get-deprecated-language-level :*
-- :doc get deprecated language levels, id's existing in version 67 but not in version 68
SELECT [db-67].languageLevelID AS [id-67], [db-67].term AS [term-67]
FROM TaxonomyDBVersion.dbo.LanguageLevelTerm AS [db-67]
WHERE [db-67].languageID = 502
AND [db-67].versionID = 67
AND [db-67].languageLevelID NOT IN
	(SELECT [db-68].languageLevelID
	FROM TaxonomyDB.dbo.LanguageLevelTerm AS [db-68]
	WHERE [db-68].languageID = 502)

-- 0 (June 12)
-- :name get-new-language-level :*
-- :doc get new language levels, id's existing in version 68 but not in version 67
SELECT [db-68].languageLevelID AS [id-68], [db-68].term AS [term-68]
FROM TaxonomyDB.dbo.LanguageLevelTerm AS [db-68]
WHERE [db-68].languageID = 502
AND [db-68].languageLevelID NOT IN
	(SELECT [db-67].languageLevelID
	FROM TaxonomyDBVersion.dbo.LanguageLevelTerm AS [db-67]
	WHERE [db-67].languageID = 502
	AND [db-67].versionID = 67)

-- 0 (June 12)
-- :name get-updated-language-level-term :*
-- :doc get updated language levels where term/label differs between version 68 and version 67
SELECT [db-68].languageLevelID AS [id-68],
	[db-68].term AS [term-68],
	[db-67].languageLevelID AS [id-67],
	[db-67].term [term-67]
FROM TaxonomyDB.dbo.LanguageLevelTerm AS [db-68], TaxonomyDBVersion.dbo.LanguageLevelTerm AS [db-67]
WHERE [db-68].languageLevelID = [db-67].languageLevelID
AND [db-67].versionID = 67
AND [db-68].languageID = 502
AND [db-67].languageID = 502
AND [db-67].term != [db-68].term COLLATE SQL_Latin1_General_CP1_CS_AS


------------------------------WAGE TYPE-- (No differences between versions!!!)--------------------------------------

-- 0 (June 12)
-- :name get-deprecated-wage-type :*
-- :doc get deprecated wage types, id's existing in version 67 but not in version 68
SELECT [db-67].löneformsID AS [id-67], [db-67].beteckning AS [term-67]
FROM TaxonomiDBSvenskVersion.dbo.LöneformTerm AS [db-67]
WHERE [db-67].språkID = 502
AND [db-67].löneformsID NOT IN
	(SELECT [db-68].löneformsID
	FROM TaxonomiDBSvensk.dbo.LöneformTerm AS [db-68]
	WHERE [db-68].språkID = 502)

-- 0 (June 12)
-- :name get-new-wage-type :*
-- :doc get new wage types, id's existing in version 68 but not in version 67
SELECT [db-68].löneformsID AS [id-68], [db-68].beteckning AS [term-68]
FROM TaxonomiDBSvensk.dbo.LöneformTerm AS [db-68]
WHERE [db-68].språkID = 502
AND [db-68].löneformsID NOT IN
	(SELECT [db-67].löneformsID
	FROM TaxonomiDBSvenskVersion.dbo.LöneformTerm AS [db-67]
	WHERE [db-67].språkID = 502)

-- 0 (June 12)
-- :name get-updated-wage-type-term :*
-- :doc get updated wage types where term/label differs between version 68 and version 67
SELECT [db-68].löneformsID  AS [id-68],
	[db-68].beteckning AS [term-68],
	[db-67].löneformsID AS [id-67],
	[db-67].beteckning [term-67]
FROM TaxonomiDBSvensk.dbo.LöneformTerm AS [db-68], TaxonomiDBSvenskVersion.dbo.LöneformTerm AS [db-67]
WHERE [db-68].löneformsID = [db-67].löneformsID
AND [db-68].språkID = 502
AND [db-67].språkID = 502
AND [db-67].beteckning != [db-68].beteckning COLLATE SQL_Latin1_General_CP1_CS_AS

----------------------------- WORKTIME EXTENT ------------------------------------------------------------

-- 1 (June 12)
-- :name get-deprecated-worktime-extent :*
-- :doc get deprecated worktime extents, id's existing in version 67 but not in version 68
SELECT [db-67-term].arbetstidsID AS [id-67], [db-67-term].beteckning AS [term-67], [db-67].sortering AS [sort-67]
FROM TaxonomiDBSvenskVersion.dbo.ArbetstidTerm AS [db-67-term], TaxonomiDBSvenskVersion.dbo.Arbetstid AS [db-67]
WHERE [db-67-term].språkID = 502
AND [db-67-term].arbetstidsID = [db-67].arbetstidsID
AND [db-67-term].arbetstidsID NOT IN
	(SELECT [db-68-term].arbetstidsID
	FROM TaxonomiDBSvensk.dbo.ArbetstidTerm AS [db-68-term]
	WHERE [db-68-term].språkID = 502)

-- 0 (June 12)
-- :name get-new-worktime-extent :*
-- :doc get new worktime extents, id's existing in version 68 but not in version 67
SELECT [db-68-term].arbetstidsID AS [id-68], [db-68-term].beteckning AS [term-68], [db-68].sortering AS [sort-68]
FROM TaxonomiDBSvensk.dbo.ArbetstidTerm AS [db-68-term], TaxonomiDBSvensk.dbo.Arbetstid AS [db-68]
WHERE [db-68-term].språkID = 502
AND [db-68-term].arbetstidsID = [db-68].arbetstidsID
AND [db-68-term].arbetstidsID NOT IN
	(SELECT [db-67-term].arbetstidsID
	FROM TaxonomiDBSvenskVersion.dbo.ArbetstidTerm AS [db-67-term]
	WHERE [db-67-term].språkID = 502)

-- 0 (June 12)
-- :name get-updated-worktime-extent-term :*
-- :doc get updated worktime extents where term/label differs between version 68 and version 67
SELECT [db-68-term].arbetstidsID  AS [id-68],
	[db-68-term].beteckning AS [term-68],
	[db-68].sortering AS [sort-68],
	[db-67-term].arbetstidsID AS [id-67],
	[db-67-term].beteckning [term-67],
	[db-67].sortering AS [sort-67]
FROM TaxonomiDBSvensk.dbo.ArbetstidTerm AS [db-68-term],
	TaxonomiDBSvenskVersion.dbo.ArbetstidTerm AS [db-67-term],
	TaxonomiDBSvensk.dbo.Arbetstid AS [db-68],
	TaxonomiDBSvenskVersion.dbo.Arbetstid AS [db-67]
WHERE [db-68-term].arbetstidsID = [db-67-term].arbetstidsID
AND [db-67-term].arbetstidsID = [db-67].arbetstidsID
AND [db-67].arbetstidsID = [db-68].arbetstidsID
AND [db-68].arbetstidsID = [db-68-term].arbetstidsID
AND [db-68-term].språkID = 502
AND [db-67-term].språkID = 502
AND ([db-67-term].beteckning != [db-68-term].beteckning COLLATE SQL_Latin1_General_CP1_CS_AS
OR [db-67].sortering != [db-68].sortering COLLATE SQL_Latin1_General_CP1_CS_AS)

------------------------------------------- SKILLS ----------------------------------------------------------------

-- 242 (June 12)
-- :name get-new-skill :*
-- :doc get new skills, id's existing in version 68 but not in version 67
SELECT [db-68].skillID AS [id-68], [db-68].term AS [term-68], skill.skillHeadlineID as [skill-headline]
FROM TaxonomyDB.dbo.SkillTerm AS [db-68], TaxonomyDB.dbo.Skill as skill
WHERE [db-68].languageID = 502
AND [db-68].skillID = skill.skillID
AND [db-68].skillID NOT IN
(SELECT [db-67].skillID
FROM TaxonomyDBVersion.dbo.SkillTerm AS [db-67]
WHERE [db-67].versionID = 67
AND [db-67].languageID = 502)

-- 120 (June 12)
-- :name get-deprecated-skill :*
-- :doc get deprecated skills, id's existing in version 67 but not version 68
SELECT [db-67].skillID AS [id-67], [db-67].term AS [67-term]
FROM TaxonomyDBVersion.dbo.SkillTerm AS [db-67]
WHERE [db-67].versionID = 67
AND [db-67].languageID = 502
AND [db-67].skillID NOT IN
(SELECT [db-68].skillID
FROM TaxonomyDB.dbo.SkillTerm AS [db-68]
WHERE [db-68].languageID = 502)

-- 166 (June 12)
-- :name get-updated-skill :*
-- :doc get updated skills where term/label differs between version 68 and version 67
SELECT
	Skill68.skillID AS [skill-id-68],
    Skill67.skillID AS [skill-id-67],
    SkillTerm68.term AS [skill-term-68],
    SkillTerm67.term AS [skill-term-67]
FROM TaxonomyDBVersion.dbo.Skill AS Skill67,
	TaxonomyDBVersion.dbo.SkillTerm AS SkillTerm67,
	TaxonomyDB.dbo.Skill AS Skill68,
	TaxonomyDB.dbo.SkillTerm AS SkillTerm68
WHERE Skill67.versionID = 67
AND   SkillTerm67.versionID = 67
AND   Skill67.skillID = SkillTerm67.skillID
AND   Skill68.skillID = SkillTerm68.skillID
AND   Skill68.skillID = Skill67.skillID
AND   SkillTerm68.languageID = 502
AND   SkillTerm67.languageID = 502
AND   SkillTerm68.term != SkillTerm67.term COLLATE SQL_Latin1_General_CP1_CS_AS

-- 128 (June 12)
-- :name get-deprecated-skill-relation-to-headline :*
-- :doc get deprecated relations between skills and headlines in version 68
SELECT [db-67].skillID AS [skill-id-67],
	[db-67].skillHeadlineID AS [parent-headline-id-67]
FROM TaxonomyDBVersion.dbo.Skill AS [db-67]
WHERE versionID = 67
AND NOT EXISTS
	(SELECT
		skillID, skillHeadlineID
	FROM TaxonomyDB.dbo.Skill
	WHERE skillID = [db-67].skillID
	AND skillHeadlineID = [db-67].skillHeadlineID)

-- 250 (June 12)
-- :name get-new-skill-relation-to-headline :*
-- :doc get new relations between skills and headlines in version 68
SELECT [db-68].skillID AS [skill-id-68],
	[db-68].skillHeadlineID AS [parent-headline-id-68]
FROM TaxonomyDB.dbo.Skill AS [db-68]
WHERE NOT EXISTS
	(SELECT
		skillID, skillHeadlineID
	FROM TaxonomyDBVersion.dbo.Skill
	WHERE skillID = [db-68].skillID
	AND skillHeadlineID = [db-68].skillHeadlineID
	AND versionID = 67)

-- 7 (June 12)
-- :name get-replaced-skill :*
-- :doc get skills that has been replaced by another skill
SELECT
        skillID AS [deprecated-id],
        term AS [deprecated-term],
        skillIDRef AS [replacing-id]
FROM TaxonomyDB.dbo.SkillReference
WHERE modificationDate > (
      SELECT created
      FROM TaxonomyDBVersion.dbo.Version
      WHERE versionID = 67
)
AND
skillID IN (
        SELECT skillID FROM TaxonomyDBVersion.dbo.Skill
        WHERE versionID = 67
)


------------------------------------------- SKILL-HEADLINE-----------------------------------------------------

-- 0 (June 12)
-- :name get-new-skill-headline :*
-- :doc get new skill headlines, id's existing in version 68 but not in version 67
SELECT [db-68].skillHeadlineID AS [id-68], [db-68-term].term AS [term-68]
FROM TaxonomyDB.dbo.SkillHeadlineTerm AS [db-68-term], TaxonomyDB.dbo.SkillHeadline as [db-68]
WHERE [db-68-term].languageID = 502
AND [db-68].skillHeadlineID = [db-68-term].skillHeadlineID
AND [db-68].skillHeadlineID NOT IN
(SELECT [db-67].skillHeadlineID
FROM TaxonomyDBVersion.dbo.SkillHeadline AS [db-67]
WHERE [db-67].versionID = 67)

-- 0 (June 12)
-- :name get-deprecated-skill-headline :*
-- :doc get deprecated skill headlines, id's existing in version 67 but not in version 68
SELECT [db-67].skillHeadlineID AS [id-67], [db-67-term].term AS [term-67]
FROM TaxonomyDBVersion.dbo.SkillHeadlineTerm AS [db-67-term], TaxonomyDBVersion.dbo.SkillHeadline as [db-67]
WHERE [db-67-term].languageID = 502
AND [db-67].skillHeadlineID = [db-67-term].skillHeadlineID
AND [db-67].versionID = 67
AND [db-67].skillHeadlineID NOT IN
(SELECT [db-68].skillHeadlineID
FROM TaxonomyDB.dbo.SkillHeadline AS [db-68])

-- 0 (June 12)
-- :name get-updated-skill-headline-term :*
-- :doc get updated skill headlines, terms differing between version 67 and 68
SELECT
	Skill68.skillHeadlineID AS [headline-id-68],
    Skill67.skillHeadlineID AS [headline-id-67],
    SkillTerm68.term AS [headline-term-68],
    SkillTerm67.term AS [headline-term-67]
FROM TaxonomyDBVersion.dbo.SkillHeadline AS Skill67,
	TaxonomyDBVersion.dbo.SkillHeadlineTerm AS SkillTerm67,
	TaxonomyDB.dbo.SkillHeadline AS Skill68,
	TaxonomyDB.dbo.SkillHeadlineTerm AS SkillTerm68
WHERE Skill67.versionID = 67
AND   SkillTerm67.versionID = 67
AND   Skill67.skillHeadlineID = SkillTerm67.skillHeadlineID
AND   Skill68.skillHeadlineID = SkillTerm68.skillHeadlineID
AND   Skill68.skillHeadlineID = Skill67.skillHeadlineID
AND   SkillTerm68.languageID = 502
AND   SkillTerm67.languageID = 502
AND   SkillTerm68.term != SkillTerm67.term COLLATE SQL_Latin1_General_CP1_CS_AS

--------------------------------- NACE/SNI (No differences between versions!!!) ------------------------------------

-- 0 (June 12)
-- :name get-new-SNI-level-1 :*
SELECT [db-68].*, [db-68-term].*
FROM TaxonomyDB.dbo.NaceLevel1 AS [db-68],
	TaxonomyDB.dbo.NaceLevel1Term AS [db-68-term]
WHERE [db-68-term].languageID = 502
AND [db-68-term].naceLevel1ID NOT IN
	(SELECT [db-67-term].naceLevel1ID
	FROM TaxonomyDBVersion.dbo.NaceLevel1Term AS [db-67-term]
	WHERE [db-67-term].languageID = 502
	AND [db-67-term].versionID = 67)

-- 0 (June 12)
-- :name get-deleted-SNI-level-1 :*
SELECT [db-67].*, [db-67-term].*
FROM TaxonomyDBVersion.dbo.NaceLevel1 AS [db-67],
	TaxonomyDBVersion.dbo.NaceLevel1Term AS [db-67-term]
WHERE [db-67-term].languageID = 502
AND [db-67-term].versionID = 67
AND [db-67-term].naceLevel1ID NOT IN
	(SELECT [db-68-term].naceLevel1ID
	FROM TaxonomyDB.dbo.NaceLevel1Term AS [db-68-term]
	WHERE [db-68-term].languageID = 502)

-- 0 (June 12)
-- :name get-updated-SNI-level-1 :*
SELECT [db-67].*, [db-67-term].*, [db-68].*, [db-68-term].*
FROM TaxonomyDBVersion.dbo.NaceLevel1 AS [db-67],
	TaxonomyDBVersion.dbo.NaceLevel1Term AS [db-67-term],
	TaxonomyDB.dbo.NaceLevel1 AS [db-68],
	TaxonomyDB.dbo.NaceLevel1Term AS [db-68-term]
WHERE [db-68-term].naceLevel1ID = [db-67-term].naceLevel1ID
AND [db-67-term].naceLevel1ID = [db-67].naceLevel1ID
AND [db-67].naceLevel1ID = [db-68].naceLevel1ID
AND [db-67-term].languageID = 502
AND [db-68-term].languageID = 502
AND [db-67-term].versionID = 67
AND [db-67].versionID = 67
AND ([db-67-term].term != [db-68-term].term COLLATE SQL_Latin1_General_CP1_CS_AS
OR [db-67].naceLevel1Code != [db-68].naceLevel1Code)

-- 0 (June 12)
-- :name get-new-SNI-level-2 :*
SELECT [db-68].*, [db-68-term].*
FROM TaxonomyDB.dbo.NaceLevel2 AS [db-68],
	TaxonomyDB.dbo.NaceLevel2Term AS [db-68-term]
WHERE [db-68-term].languageID = 502
AND [db-68-term].naceLevel2ID NOT IN
	(SELECT [db-67-term].naceLevel2ID
	FROM TaxonomyDBVersion.dbo.NaceLevel2Term AS [db-67-term]
	WHERE [db-67-term].languageID = 502
	AND [db-67-term].versionID = 67)

-- 0 (June 12)
-- :name get-deleted-SNI-level-2 :*
SELECT [db-67].*, [db-67-term].*
FROM TaxonomyDBVersion.dbo.NaceLevel2 AS [db-67],
	TaxonomyDBVersion.dbo.NaceLevel2Term AS [db-67-term]
WHERE [db-67-term].languageID = 502
AND [db-67-term].versionID = 67
AND [db-67-term].naceLevel2ID NOT IN
	(SELECT [db-68-term].naceLevel2ID
	FROM TaxonomyDB.dbo.NaceLevel2Term AS [db-68-term]
	WHERE [db-68-term].languageID = 502)

-- 0 (June 12)
-- :name get-updated-SNI-level-2 :*
SELECT [db-67].*, [db-67-term].*, [db-68].*, [db-68-term].*
FROM TaxonomyDBVersion.dbo.NaceLevel2 AS [db-67],
	TaxonomyDBVersion.dbo.NaceLevel2Term AS [db-67-term],
	TaxonomyDB.dbo.NaceLevel2 AS [db-68],
	TaxonomyDB.dbo.NaceLevel2Term AS [db-68-term]
WHERE [db-68-term].naceLevel2ID = [db-67-term].naceLevel2ID
AND [db-67-term].naceLevel2ID = [db-67].naceLevel2ID
AND [db-67].naceLevel2ID = [db-68].naceLevel2ID
AND [db-67-term].languageID = 502
AND [db-68-term].languageID = 502
AND [db-67-term].versionID = 67
AND [db-67].versionID = 67
AND ([db-67-term].term != [db-68-term].term COLLATE SQL_Latin1_General_CP1_CS_AS
OR [db-67].naceLevel2Code != [db-68].naceLevel2Code)

-- 0 (June 12)
-- :name get-deprecated-SNI-level-2-relation-to-SNI-1 :*
SELECT [db-67].naceLevel2ID,
	[db-67].naceLevel1ID
FROM TaxonomyDBVersion.dbo.NaceLevel2 AS [db-67]
WHERE versionID = 67
AND NOT EXISTS
	(SELECT
		naceLevel2ID, naceLevel1ID
	FROM TaxonomyDB.dbo.NaceLevel2
	WHERE naceLevel2ID = [db-67].naceLevel2ID
	AND naceLevel1ID = [db-67].naceLevel1ID)

-- 0 (June 12)
-- :name get-new-SNI-level-2-relation-to-SNI-1 :*
SELECT [db-68].naceLevel2ID,
	[db-68].naceLevel1ID
FROM TaxonomyDB.dbo.NaceLevel2 AS [db-68]
WHERE NOT EXISTS
	(SELECT
		naceLevel2ID, naceLevel1ID
	FROM TaxonomyDBVersion.dbo.NaceLevel2
	WHERE naceLevel2ID = [db-68].naceLevel2ID
	AND naceLevel1ID = [db-68].naceLevel1ID
	AND versionID = 67)

-- :name get-occupation-experience-years :*
SELECT *
FROM TaxonomyDB.dbo.OccupationExperienceYearTerm
WHERE languageID = 502

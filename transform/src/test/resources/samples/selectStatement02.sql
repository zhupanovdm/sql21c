SELECT
  1 subTyp,
  substring(org.Наименование, 1, 50) org_name,
  org.Ссылка org_1C,
  org.Код,
  substring(agr._Description, 1, 50) agr_desc
FROM
  [ Справочник.Организации ] org,
  [ _Reference86 ] agr
WHERE
  org.Ссылка = agr._Fld16817RRef
  AND org.Поле1 <> 0x8EE51FEA085C40A544D45EFE8CD153DF
  AND org.Код IN (
    SELECT
      OrgCode
    FROM
      # Org1C
  )
  AND agr._Fld16816RRef = @ firmID
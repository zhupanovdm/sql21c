select

3,            -- тип документа - продажи
1  subTyp,     -- частные продажи
org._Code  org_no, --- код
substring (org._Description, 1 , 50)  org_name, --org_name, –-- наименование

cust._Code  cust_1C, -- код
cust._Fld3367 cust_Nav, -- код erp
substring (cust._Fld3356, 1 , 100 ) Cust_name, -- Наименование полное
cust._Fld3361 inn, -- ИНН
cust._Fld3362  kpp, -- КПП

loc._Code         loc_1C,--, -- код
substring (loc._Description, 1, 50)  loc_desc,-- наименование

--sh._Fld16821RRef, -- ссылка на контрагента
REPLACE(sh._Number ,' ','')  doc_no, -- номер
dateadd ( year, -2000, CONVERT(DATETIME, CONVERT(VARCHAR, sh._Date_Time, 112))) doc_date, --- дата и время
--case when sh._Fld5117 = 0x01
--         then  1 else 0 end Includ_vat, -- флаг сумма включает НДС
sh._Fld16841 summ_doc ,  -- сумма документа

substring(isnull(it._Fld3885  , '') , 1 , 6 ) as item_nav, -- код erp

it._Code     item_1c, -- код 1C

substring (it._Description , 1, 150 ) , -- наименование


sl._Fld16877  as qty ,-- количество

 case when sh._Fld16828 <>  0x01
           THEN  sl._Fld16879 + sl._Fld16881
        ELSE sl._Fld16879 END  as summ_line,  -- сумма

sl._Fld16881 as summ_line_nds , -- сумма НДС
convert( varchar(20), sh._Fld16817RRef )

ID_loc_1c



 from [_Document539]  sh ,
      [_Document539_VT16871]  sl,
      _Reference129 cust,
      _Reference153 it,
      _Reference266 loc,
      _Reference167 org ,
      [_Reference86] agr


 where
  -- sh._Number =  case when @docno = ''  then sh._Number else @docno end
      -- (sh._Number  =  @docno    or @docno = '')  and
   sh._Date_Time between @1cfrom and @1cto
  and  sh._Posted  = 0x01
  and  sh._Marked = 0x00
  and sl._Document539_IDRRef  = sh._IDRRef
  and cust._IDRRef  =  sh._Fld16821RRef
  and it._IDRRef  = sl._Fld16873RRef
  and loc._IDRRef  = sh._Fld16817RRef
  and org._IDRRef  = sh._Fld16816RRef
  and sh._Fld16822RRef = agr._IDRRef

	and sh._Fld38453 <> '' --or sh._Date_Time < dateadd(year, 2000, convert(varchar,'20190701', 112) + ' 00:00:00'))  --OSV Если пусто, значит документ не выгружался из erp. Скорее всего был создан вручную.

  and agr._Fld2576RRef <> 0x8EE51FEA085C40A544D45EFE8CD153DF -- убираем договора комиссии
  and loc._Code in  (select  LocCode from #Loc1C  )   -- продажи только с оптового склада
  and sh._Fld16816RRef =  @firmID
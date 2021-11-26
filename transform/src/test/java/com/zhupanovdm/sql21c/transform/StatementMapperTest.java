package com.zhupanovdm.sql21c.transform;

import net.sf.jsqlparser.statement.select.Select;
import org.junit.Test;

import static com.zhupanovdm.sql21c.transform.ParserUtils.fixIncorrectParams;
import static com.zhupanovdm.sql21c.transform.TestUtils.*;
import static com.zhupanovdm.sql21c.transform.TestUtils.statement;
import static org.assertj.core.api.Assertions.assertThat;

public class StatementMapperTest {
    @Test
    public void sample() {
        SqlSelectStatementParser parser = new SqlSelectStatementParser(fixIncorrectParams(resource("samples/selectStatement01.sql")));
        SelectStatementVisitor visitor = new SelectStatementVisitor();
        Select stmt = parser.parse(visitor);

        EntityMapRepo repo = new EntityMapRepo().load(resourcePath("samples/tableMapping02.json"));
        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(visitor.getModel());

        assertThat(statement(stmt)).isEqualTo(statement(
                "SELECT\n" +
                        "  3,\n" +
                        "  1 subTyp,\n" +
                        "  org._Code org_no,\n" +
                        "  substring(org._Description, 1, 50) org_name,\n" +
                        "  cust._Code cust_1C,\n" +
                        "  cust._Fld3367 cust_Nav,\n" +
                        "  substring(cust._Fld3356, 1, 100) Cust_name,\n" +
                        "  cust._Fld3361 inn,\n" +
                        "  cust._Fld3362 kpp,\n" +
                        "  loc.Код loc_1C,\n" +
                        "  substring(loc.Наименование, 1, 50) loc_desc,\n" +
                        "  REPLACE(sh._Number, ' ', '') doc_no,\n" +
                        "  dateadd(year, -2000, CONVERT(DATETIME, CONVERT(VARCHAR, sh._Date_Time, 112))) doc_date,\n" +
                        "  sh._Fld16841 summ_doc,\n" +
                        "  substring(isnull(it._Fld3885, ''), 1, 6) AS item_nav,\n" +
                        "  it._Code item_1c,\n" +
                        "  substring(it._Description, 1, 150),\n" +
                        "  sl._Fld16877 AS qty,\n" +
                        "  CASE\n" +
                        "    WHEN sh._Fld16828 <> 0x01 THEN sl._Fld16879 + sl._Fld16881\n" +
                        "    ELSE sl._Fld16879\n" +
                        "  END AS summ_line,\n" +
                        "  sl._Fld16881 AS summ_line_nds,\n" +
                        "  convert(varchar(20), sh._Fld16817RRef) ID_loc_1c\n" +
                        "FROM\n" +
                        "  [_Document539] sh,\n" +
                        "  [_Document539_VT16871] sl,\n" +
                        "  _Reference129 cust,\n" +
                        "  _Reference153 it,\n" +
                        "  [Справочник.Организации] loc,\n" +
                        "  _Reference167 org,\n" +
                        "  [_Reference86] agr\n" +
                        "WHERE\n" +
                        "  sh._Date_Time BETWEEN @_1cfrom AND @_1cto\n" +
                        "  AND sh._Posted = 0x01\n" +
                        "  AND sh._Marked = 0x00\n" +
                        "  AND sl._Document539_IDRRef = sh._IDRRef\n" +
                        "  AND cust._IDRRef = sh._Fld16821RRef\n" +
                        "  AND it._IDRRef = sl._Fld16873RRef\n" +
                        "  AND loc.Ссылка = sh._Fld16817RRef\n" +
                        "  AND org._IDRRef = sh._Fld16816RRef\n" +
                        "  AND sh._Fld16822RRef = agr._IDRRef\n" +
                        "  AND sh._Fld38453 <> ''\n" +
                        "  AND agr._Fld2576RRef <> 0x8EE51FEA085C40A544D45EFE8CD153DF\n" +
                        "  AND loc.Код IN (SELECT LocCode FROM #Loc1C)\n" +
                        "  AND sh._Fld16816RRef = @ firmID"));
    }

    @Test
    public void mapDataSourceTwice() {
        SqlSelectStatementParser parser = new SqlSelectStatementParser(fixIncorrectParams(
                "SELECT\n" +
                "  t1.name1,\n" +
                "  t2.name3,\n" +
                "  t3.name3\n" +
                "FROM\n" +
                "  table1 t1\n" +
                "  INNER JOIN table2 t2 ON t1.name2 = t2.name4\n" +
                "  INNER JOIN table2 t3 ON t1.name2 = t3.name4"));

        SelectStatementVisitor visitor = new SelectStatementVisitor();
        Select stmt = parser.parse(visitor);

        EntityMapRepo repo = new EntityMapRepo().load(resourcePath("samples/tableMapping01.json"));
        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(visitor.getModel());

        assertThat(statement(stmt)).isEqualTo(statement(
                "SELECT\n" +
                        "  t1.field1,\n" +
                        "  t2.field3,\n" +
                        "  t3.field3\n" +
                        "FROM\n" +
                        "  entity1 t1\n" +
                        "  INNER JOIN entity2 t2 ON t1.field2 = t2.field4\n" +
                        "  INNER JOIN entity2 t3 ON t1.field2 = t3.field4"));
    }

    @Test
    public void reverse() {
        SqlSelectStatementParser parser = new SqlSelectStatementParser(fixIncorrectParams(resource("samples/selectStatement02.sql")));
        SelectStatementVisitor visitor = new SelectStatementVisitor();
        Select stmt = parser.parse(visitor);

        EntityMapRepo repo = new EntityMapRepo().load(resourcePath("samples/reverseMapping.json"));
        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(visitor.getModel());

        assertThat(statement(stmt)).isEqualTo(statement(
                "SELECT\n" +
                        "  1 subTyp,\n" +
                        "  substring(org._Description, 1, 50) org_name,\n" +
                        "  org._IDRRef org_1C,\n" +
                        "  org._Code,\n" +
                        "  substring(agr._Description, 1, 50) agr_desc\n" +
                        "FROM\n" +
                        "  _Reference266 org,\n" +
                        "  [_Reference86] agr\n" +
                        "WHERE\n" +
                        "  org._IDRRef = agr._Fld16817RRef\n" +
                        "  AND org._Fld16841 <> 0x8EE51FEA085C40A544D45EFE8CD153DF\n" +
                        "  AND org._Code IN (SELECT OrgCode FROM #Org1C)\n" +
                        "  AND agr._Fld16816RRef = @firmID"));
    }

    @Test
    public void unknownFields() {
        SqlSelectStatementParser parser = new SqlSelectStatementParser("SELECT _IDRRef FROM dbo._acc39 WHERE _code IN ('62.01', '62.02')");
        SelectStatementVisitor visitor = new SelectStatementVisitor();
        Select stmt = parser.parse(visitor);

        EntityMapRepo repo = new EntityMapRepo().load(resourcePath("samples/tableMapping03.json"));
        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(visitor.getModel());

        assertThat(statement(stmt)).isEqualTo(statement(
                "SELECT Ссылка FROM dbo.[ПланСчетов.Хозрасчетный] WHERE Код IN ('62.01', '62.02')"));
    }

    @Test
    public void nullTableMapping() {
        SqlSelectStatementParser parser = new SqlSelectStatementParser("SELECT acc._IDRRef FROM dbo._acc39 acc WHERE _code IN ('62.01', '62.02')");
        SelectStatementVisitor visitor = new SelectStatementVisitor();

        Select stmt = parser.parse(visitor);

        EntityMapRepo repo = new EntityMapRepo().load(resourcePath("samples/nullMapping.json"));
        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(visitor.getModel());

        assertThat(statement(stmt)).isEqualTo(statement(
                "SELECT acc.Ссылка FROM dbo._acc39 acc WHERE Код IN ('62.01', '62.02')"));
    }

    @Test
    public void nullFieldMapping() {
        SqlSelectStatementParser parser = new SqlSelectStatementParser("SELECT acc._IDRRef FROM dbo._acc40 acc WHERE _code IN ('62.01', '62.02')");
        SelectStatementVisitor visitor = new SelectStatementVisitor();

        Select stmt = parser.parse(visitor);

        EntityMapRepo repo = new EntityMapRepo().load(resourcePath("samples/nullMapping.json"));
        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(visitor.getModel());

        assertThat(statement(stmt)).isEqualTo(statement(
                "SELECT acc._IDRRef FROM dbo.[ПланСчетов.Хозрасчетный] acc WHERE _code IN ('62.01', '62.02')"));
    }

    @Test
    public void subSelectFieldMapping() {
        SqlSelectStatementParser parser = new SqlSelectStatementParser("SELECT (SELECT TOP 1 _IDRRef FROM dbo._acc39 WHERE _code IN ('62.01', '62.02'))");
        SelectStatementVisitor visitor = new SelectStatementVisitor();
        Select stmt = parser.parse(visitor);

        EntityMapRepo repo = new EntityMapRepo().load(resourcePath("samples/tableMapping03.json"));
        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(visitor.getModel());

        assertThat(statement(stmt)).isEqualTo(statement(
                "SELECT (SELECT TOP 1  Ссылка FROM dbo.[ПланСчетов.Хозрасчетный] WHERE Код IN ('62.01', '62.02'))"));
    }

    @Test
    public void subSelectDataSourceMapping() {
        SqlSelectStatementParser parser = new SqlSelectStatementParser("SELECT 1 FROM (SELECT TOP 1 _IDRRef FROM dbo._acc39 WHERE _code IN ('62.01', '62.02'))");
        SelectStatementVisitor visitor = new SelectStatementVisitor();
        Select stmt = parser.parse(visitor);

        EntityMapRepo repo = new EntityMapRepo().load(resourcePath("samples/tableMapping03.json"));
        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(visitor.getModel());

        assertThat(statement(stmt)).isEqualTo(statement(
                "SELECT 1 FROM (SELECT TOP 1  Ссылка FROM dbo.[ПланСчетов.Хозрасчетный] WHERE Код IN ('62.01', '62.02'))"));
    }

    @Test
    public void subSelectNestedFieldsMapping() {
        SqlSelectStatementParser parser = new SqlSelectStatementParser(fixIncorrectParams(
                "SELECT\n" +
                        "  (SELECT 1 FROM table2 t2 WHERE t2.name3 = t1.name1) a\n" +
                        "FROM\n" +
                        "  table1 t1"));

        SelectStatementVisitor visitor = new SelectStatementVisitor();
        Select stmt = parser.parse(visitor);

        EntityMapRepo repo = new EntityMapRepo().load(resourcePath("samples/tableMapping01.json"));
        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(visitor.getModel());

        assertThat(statement(stmt)).isEqualTo(statement("SELECT (SELECT 1 FROM entity2 t2 WHERE t2.field3 = t1.field1) a FROM entity1 t1"));
    }
}
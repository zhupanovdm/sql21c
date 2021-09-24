package com.zhupanovdm.sql21c.transform;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.Test;

import static com.zhupanovdm.sql21c.transform.ParserUtils.fixIncorrectParams;
import static com.zhupanovdm.sql21c.transform.TestUtils.resource;
import static com.zhupanovdm.sql21c.transform.TestUtils.resourcePath;

public class StatementMapperTest {
    @Test
    public void sample() {
        SqlSelectStatementParser parser = new SqlSelectStatementParser(fixIncorrectParams(resource("samples/selectStatement01.sql")));
        SelectEntityExtractor extractor = new SelectEntityExtractor();

        Select stmt = parser.parse(extractor);

        EntityMapRepo repo = new EntityMapRepo().load(resourcePath("samples/tableMapping02.json"));
        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(extractor);

        // TODO: assertion
        System.out.println(SqlFormatter.format(stmt.toString()));
    }

    @Test
    public void reverse() {
        SqlSelectStatementParser parser = new SqlSelectStatementParser(fixIncorrectParams(resource("samples/selectStatement02.sql")));
        SelectEntityExtractor extractor = new SelectEntityExtractor();

        Select stmt = parser.parse(extractor);

        EntityMapRepo repo = new EntityMapRepo().load(resourcePath("samples/reverseMapping.json"));
        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(extractor);

        // TODO: assertion
        System.out.println(SqlFormatter.format(stmt.toString()));
    }

    @Test
    public void unknownFields() {
        SqlSelectStatementParser parser = new SqlSelectStatementParser("SELECT _IDRRef FROM dbo._acc39 WHERE _code IN ('62.01', '62.02')");
        SelectEntityExtractor extractor = new SelectEntityExtractor();

        Select parse = parser.parse(extractor);

        EntityMapRepo repo = new EntityMapRepo().load(resourcePath("samples/tableMapping03.json"));
        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(extractor);

        // TODO: assertion
        System.out.println(SqlFormatter.format(parse.toString()));
    }
}
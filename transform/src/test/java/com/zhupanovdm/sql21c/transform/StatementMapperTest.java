package com.zhupanovdm.sql21c.transform;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.Test;

public class StatementMapperTest {

    @Test
    public void sample() {
        String resource = ParserUtils.replaceIncorrectParams(TestUtils.resource("samples/selectStatement01.sql"));
        SelectParser selectParser = new SelectParser(resource);
        SelectEntityExtractor extractor = new SelectEntityExtractor();

        Select parse = selectParser.parse(extractor);

        EntityMapRepo repo = new EntityMapRepo();
        repo.load(TestUtils.resourcePath("samples/tableMapping02.json"));

        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(extractor);

        System.out.println(SqlFormatter.format(parse.toString()));
    }

    @Test
    public void reverse() {
        String resource = ParserUtils.replaceIncorrectParams(TestUtils.resource("samples/selectStatement02.sql"));
        SelectParser selectParser = new SelectParser(resource);
        SelectEntityExtractor extractor = new SelectEntityExtractor();

        Select parse = selectParser.parse(extractor);

        EntityMapRepo repo = new EntityMapRepo();
        repo.load(TestUtils.resourcePath("samples/reverseMapping.json"));

        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(extractor);

        System.out.println(SqlFormatter.format(parse.toString()));
    }

    @Test
    public void unknownFields() {
        SelectParser selectParser = new SelectParser("SELECT _IDRRef FROM dbo._acc39 WHERE _code IN ('62.01', '62.02')");
        SelectEntityExtractor extractor = new SelectEntityExtractor();

        Select parse = selectParser.parse(extractor);

        EntityMapRepo repo = new EntityMapRepo();
        repo.load(TestUtils.resourcePath("samples/tableMapping03.json"));

        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(extractor);

        System.out.println(SqlFormatter.format(parse.toString()));
    }

}
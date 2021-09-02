package org.zhupanovdm.sql21c;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.Test;

import static org.zhupanovdm.sql21c.TestUtils.resource;
import static org.zhupanovdm.sql21c.TestUtils.resourcePath;

public class StatementMapperTest {

    @Test
    public void test() {
        String resource = ParserUtils.replaceIncorrectParams(resource("samples/selectStatement01.sql"));
        SelectParser selectParser = new SelectParser(resource);
        SelectEntityExtractor extractor = new SelectEntityExtractor();

        Select parse = selectParser.parse(extractor);

        EntityMapRepo repo = new EntityMapRepo();
        repo.load(resourcePath("samples/tableMapping02.json"));

        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(extractor);

        System.out.println(SqlFormatter.format(parse.toString()));
    }

}
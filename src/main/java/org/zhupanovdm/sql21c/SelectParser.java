package org.zhupanovdm.sql21c;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectVisitor;

public class SelectParser {
    private final CCJSqlParser parser;

    public SelectParser(String selectStatement) {
        parser = CCJSqlParserUtil.newParser(selectStatement)
                .withSquareBracketQuotation(true);
    }

    public Select parse(SelectVisitor visitor) {
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parseStatement(parser);
        } catch (JSQLParserException e) {
            throw new IllegalArgumentException("Probably is not sql statement or malformed", e);
        }

        if (statement instanceof Select) {
            SelectBody select = ((Select) statement).getSelectBody();
            select.accept(visitor);
            return (Select) statement;
        }

        throw new IllegalArgumentException("Is not SELECT statement");
    }

}

package org.zhupanovdm.sql21c;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.List;

public class Application {

    public static void main(String[] args) {

//        try {
//            Statement parse = CCJSqlParserUtil.parse("SELECT * FROM [tab].[tab1]",
//                    parser -> parser.withSquareBracketQuotation(true));



//            String s = "INSERT INTO contacts\n" +
//                    "(contact_id, last_name, first_name)\n" +
//                    "SELECT employee_id, last_name, first_name\n" +
//                    "FROM employees\n" +
//                    "WHERE employee_id <= 100";

//            String s = "SELECT * FROM Customers as c123 WHERE City = @City AND PostalCode = @PostalCode";

//            String s = "CREATE PROCEDURE SelectAllCustomers\n" +
//                    "AS\n" +
//                    "SELECT * FROM Customers\n" +
//                    "GO;";

//            CCJSqlParser parser = CCJSqlParserUtil.newParser(s)
//                    .withSquareBracketQuotation(true)
//                    .withAllowComplexParsing(false);
//            Statements stmt = CCJSqlParserUtil.parseStatements(parser);
//
//            //Select selectStatement = (Select) statement;
//            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
//
//            for (Statement st : stmt.getStatements()) {
//                System.out.println(st.getClass());
//
//                //List<String> tableList = tablesNamesFinder.getTableList(st);
//                //System.out.println(tableList);
//                System.out.println(st);
//            }
//
//            //System.out.println(stmt);
//
//        } catch (JSQLParserException e) {
//            e.printStackTrace();
//        }

    }

}

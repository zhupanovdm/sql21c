package org.zhupanovdm.sql21c;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.values.ValuesStatement;
import org.zhupanovdm.sql21c.model.DataAttribute;
import org.zhupanovdm.sql21c.model.DataSource;

import java.util.*;

public class SelectParser {

    private final CCJSqlParser parser;

    private final Map<String, DataSource> references = new HashMap<>();
    private final Set<DataAttribute> attributes = new HashSet<>();
    private final Set<String> attributesNoSource = new HashSet<>();

    public SelectParser(String selectStatement) {
        parser = CCJSqlParserUtil.newParser(selectStatement)
                .withSquareBracketQuotation(true);
        parse();
    }

    private void parse() {
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parseStatement(parser);
        } catch (JSQLParserException e) {
            throw new IllegalArgumentException("Probably is not sql statement or malformed", e);
        }

        if (statement instanceof Select) {
            SelectBody select = ((Select) statement).getSelectBody();
            select.accept(new PlainSelectVisitor() {
                @Override
                public void visit(PlainSelect plainSelect) {
                    DataSource dataSource;
                    Table table = plainSelect.getFromItem(Table.class);
                    if (table != null) {
                        dataSource = DataSource.fromTable(table);
                        references.put(dataSource.getReference(), dataSource);
                    }

                    if (plainSelect.getJoins() != null) {
                        for (Join join : plainSelect.getJoins()) {
                            dataSource = DataSource.fromTable(join.getRightItem(Table.class));
                            references.put(dataSource.getReference(), dataSource);
                            if (join.getOnExpression() != null) {
                                findAttributesInExpression(join.getOnExpression());
                            }
                        }
                    }

                    if (plainSelect.getSelectItems() != null) {
                        for (SelectItem item : plainSelect.getSelectItems()) {
                            findAttributesInExpression(((SelectExpressionItem) item).getExpression());
                        }
                    }

                    if (plainSelect.getWhere() != null) {
                        findAttributesInExpression(plainSelect.getWhere());
                    }

                    GroupByElement groupBy = plainSelect.getGroupBy();
                    if (groupBy != null) {
                        ExpressionList expressionList = groupBy.getGroupByExpressionList();
                        if (expressionList != null) {
                            for (Expression expression : expressionList.getExpressions()) {
                                findAttributesInExpression(expression);
                            }
                        }
                    }

                }
            });
            return;
        }

        throw new IllegalArgumentException("Is not SELECT statement");
    }

    private void findAttributesInExpression(Expression expression) {
        if (expression instanceof Column) {
            Column column = (Column) expression;
            String name = column.getColumnName();
            Table table = column.getTable();
            if (table == null) {
                attributesNoSource.add(name);
            } else {
                DataSource source = findSource(table);
                if (source == null) {
                    throw new IllegalStateException("Cannot find source for field: " + column.getFullyQualifiedName());
                }
                attributes.add(new DataAttribute(source, name));
            }

        } else if (expression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) expression;
            findAttributesInExpression(binaryExpression.getLeftExpression());
            findAttributesInExpression(binaryExpression.getRightExpression());

        } else if (expression instanceof Parenthesis) {
            findAttributesInExpression(((Parenthesis) expression).getExpression());

        } else if (expression instanceof Function) {
            Function function = (Function) expression;
            if (function.getParameters() != null) {
                for (Expression ex : function.getParameters().getExpressions()) {
                    findAttributesInExpression(ex);
                }
            }

        }
    }

    private DataSource findSource(Table table) {
        return references.get(table.getAlias() == null ? table.getName() : table.getAlias().getName());
    }

    public Collection<DataSource> getAllDataSources() {
        return references.values();
    }

    public Collection<DataAttribute> getAllAttributes() {
        return Collections.unmodifiableCollection(attributes);
    }

    public Collection<String> getAttributesNoSource() {
        return Collections.unmodifiableCollection(attributesNoSource);
    }

    private abstract static class PlainSelectVisitor implements SelectVisitor {
        @Override
        public void visit(SetOperationList setOperationList) {
        }

        @Override
        public void visit(WithItem withItem) {
        }

        @Override
        public void visit(ValuesStatement valuesStatement) {
        }
    }

}

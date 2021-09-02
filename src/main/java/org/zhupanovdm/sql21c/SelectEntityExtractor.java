package org.zhupanovdm.sql21c;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.values.ValuesStatement;
import org.zhupanovdm.sql21c.model.db.StatementAttribute;
import org.zhupanovdm.sql21c.model.db.StatementDataSource;

import java.util.*;

public class SelectEntityExtractor implements SelectVisitor, StatementModel {
    private final Map<String, StatementDataSource> references = new HashMap<>();
    private final Set<String> unknownStatementFields = new HashSet<>();

    @Override
    public void visit(PlainSelect plainSelect) {
        StatementDataSource statementDataSource;
        Table table = plainSelect.getFromItem(Table.class);
        if (table != null) {
            statementDataSource = new StatementDataSource(table);
            references.put(statementDataSource.getReference(), statementDataSource);
        }

        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                statementDataSource = new StatementDataSource(join.getRightItem(Table.class));
                references.put(statementDataSource.getReference(), statementDataSource);
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

    @Override
    public void visit(SetOperationList setOpList) {
    }

    @Override
    public void visit(WithItem withItem) {
    }

    @Override
    public void visit(ValuesStatement aThis) {
    }

    private void findAttributesInExpression(Expression expression) {
        if (expression instanceof Column) {
            Column column = (Column) expression;
            String name = column.getColumnName();
            Table table = column.getTable();
            if (table == null) {
                unknownStatementFields.add(name);
            } else {
                StatementDataSource statementDataSource = findStatementDataSource(table);
                if (statementDataSource == null) {
                    throw new IllegalStateException("Cannot find entity for field: " + column.getFullyQualifiedName());
                }
                new StatementAttribute(statementDataSource, column);
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

    private StatementDataSource findStatementDataSource(Table table) {
        return references.get(new StatementDataSource(table).getReference());
    }

    @Override
    public Collection<StatementDataSource> getDataSources() {
        return references.values();
    }

    @Override
    public Collection<String> getUnknownStatementFields() {
        return Collections.unmodifiableCollection(unknownStatementFields);
    }

}

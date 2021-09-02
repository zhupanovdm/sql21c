package org.zhupanovdm.sql21c.transform;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.values.ValuesStatement;
import org.zhupanovdm.sql21c.transform.model.db.StatementAttribute;
import org.zhupanovdm.sql21c.transform.model.db.StatementDataSource;

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

        findAttributesInExpression(plainSelect.getWhere());

        if (plainSelect.getGroupBy() != null) {
            ExpressionList expressionList = plainSelect.getGroupBy().getGroupByExpressionList();
            if (expressionList != null) {
                for (Expression expression : expressionList.getExpressions()) {
                    findAttributesInExpression(expression);
                }
            }
        }

        if (plainSelect.getOrderByElements() != null) {
            for (OrderByElement element : plainSelect.getOrderByElements()) {
                findAttributesInExpression(element.getExpression());
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
        if (expression == null) {
            return;
        }

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

        } else if (expression instanceof CaseExpression) {
            CaseExpression caseExpression = (CaseExpression) expression;
            for (WhenClause whenClause : caseExpression.getWhenClauses()) {
                findAttributesInExpression(whenClause.getWhenExpression());
                findAttributesInExpression(whenClause.getThenExpression());
            }
            findAttributesInExpression(caseExpression.getElseExpression());

        } else if (expression instanceof InExpression) {
            InExpression inExpression = (InExpression) expression;
            findAttributesInExpression(inExpression.getLeftExpression());
            findAttributesInExpression(inExpression.getRightExpression());

        } else if (expression instanceof Between) {
            Between inExpression = (Between) expression;
            findAttributesInExpression(inExpression.getLeftExpression());
            findAttributesInExpression(inExpression.getBetweenExpressionStart());
            findAttributesInExpression(inExpression.getBetweenExpressionEnd());

        } else if (expression instanceof IsNullExpression) {
            findAttributesInExpression(((IsNullExpression) expression).getLeftExpression());

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

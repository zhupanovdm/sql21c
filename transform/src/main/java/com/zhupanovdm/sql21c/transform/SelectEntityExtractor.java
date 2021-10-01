package com.zhupanovdm.sql21c.transform;

import com.zhupanovdm.sql21c.transform.model.db.StatementAttribute;
import com.zhupanovdm.sql21c.transform.model.db.StatementDataSource;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.values.ValuesStatement;

import java.util.*;

public class SelectEntityExtractor implements SelectVisitor, StatementModel {
    private final List<StatementDataSource> sources = new LinkedList<>();
    private final Map<String, StatementDataSource> sourceAliases = new HashMap<>();
    private final Set<StatementAttribute> unknownFields = new HashSet<>();

    @Override
    public void visit(PlainSelect plainSelect) {
        addStatementDataSource(plainSelect.getFromItem(Table.class));

        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                addStatementDataSource(join.getRightItem(Table.class));
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

        findAttributesInExpression(plainSelect.getHaving());

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
            Table table = column.getTable();
            if (table == null) {
                unknownFields.add(new StatementAttribute(column));
            } else {
                StatementDataSource statementDataSource = findStatementDataSource(table);
                if (statementDataSource == null) {
                    throw new IllegalStateException("Cannot find entity for field: " + column.getFullyQualifiedName());
                }
                StatementAttribute attribute = new StatementAttribute(statementDataSource, column);
                statementDataSource.addAttribute(attribute);
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

        } else if (expression instanceof SignedExpression) {
            SignedExpression signedExpression = (SignedExpression) expression;
            findAttributesInExpression(signedExpression.getExpression());

        }
    }

    private void addStatementDataSource(Table table) {
        if (table == null) {
            return;
        }

        StatementDataSource sds = new StatementDataSource(table);
        if (sds.getAlias() != null) {
            sourceAliases.put(sds.getAlias(), sds);
        }
        sources.add(sds);
    }

    private StatementDataSource findStatementDataSource(Table table) {
        return sources.stream()
                .filter(sds -> table.getName().equals(sds.getName()))
                .findFirst().orElseGet(() -> sourceAliases.get(table.getName()));
    }

    @Override
    public Collection<StatementDataSource> getDataSources() {
        return Collections.unmodifiableList(sources);
    }

    @Override
    public Set<StatementAttribute> getUnknownFields() {
        return Collections.unmodifiableSet(unknownFields);
    }
}

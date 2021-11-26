package com.zhupanovdm.sql21c.transform;

import lombok.Getter;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.values.ValuesStatement;

public class SelectStatementVisitor implements SelectVisitor {
    @Getter
    private final SelectStatementModel model;

    public SelectStatementVisitor() {
        this(new SelectStatementModel());
    }

    public SelectStatementVisitor(SelectStatementModel model) {
        this.model = model;
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        model.addFromItem(plainSelect.getFromItem());

        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                model.addFromItem(join.getRightItem());
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
            model.addColumn((Column) expression);

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

        } else if (expression instanceof VariableAssignment) {
            findAttributesInExpression(((VariableAssignment) expression).getExpression());

        } else if (expression instanceof CastExpression) {
            findAttributesInExpression(((CastExpression) expression).getLeftExpression());

        } else if (expression instanceof SubSelect) {
            model.addDataSource((SubSelect) expression);
        }
    }
}

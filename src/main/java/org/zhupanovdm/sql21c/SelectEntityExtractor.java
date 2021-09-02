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
import org.zhupanovdm.sql21c.model.EntityAttribute;
import org.zhupanovdm.sql21c.model.Entity;

import java.util.*;

public class SelectEntityExtractor implements SelectVisitor {
    private final Map<String, Entity> references = new HashMap<>();
    private final Set<String> unknownAttributes = new HashSet<>();

    @Override
    public void visit(PlainSelect plainSelect) {
        Entity entity;
        Table table = plainSelect.getFromItem(Table.class);
        if (table != null) {
            entity = new Entity(table);
            references.put(entity.getReference(), entity);
        }

        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                entity = new Entity(join.getRightItem(Table.class));
                references.put(entity.getReference(), entity);
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
                unknownAttributes.add(name);
            } else {
                Entity entity = findEntity(table);
                if (entity == null) {
                    throw new IllegalStateException("Cannot find entity for field: " + column.getFullyQualifiedName());
                }
                new EntityAttribute(entity, column);
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

    private Entity findEntity(Table table) {
        return references.get(new Entity(table).getReference());
    }

    public Collection<Entity> getAllEntities() {
        return references.values();
    }

    public Collection<String> getUnknownAttributes() {
        return Collections.unmodifiableCollection(unknownAttributes);
    }

}

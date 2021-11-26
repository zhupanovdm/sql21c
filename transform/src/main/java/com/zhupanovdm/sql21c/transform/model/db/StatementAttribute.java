package com.zhupanovdm.sql21c.transform.model.db;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.sf.jsqlparser.schema.Column;

@EqualsAndHashCode(of = "attribute")
public class StatementAttribute {
    private final StatementDataSource statementDataSource;
    @Getter
    private final Column attribute;

    public StatementAttribute(StatementDataSource statementDataSource, Column attribute) {
        this.statementDataSource = statementDataSource;
        this.attribute = attribute;
    }

    public StatementAttribute(Column attribute) {
        this(null, attribute);
    }

    public String getName() {
        return attribute.getColumnName();
    }

    public void setName(String name) {
        attribute.setColumnName(name);
    }

    public String getFullyQualifiedName() {
        return attribute.getFullyQualifiedName();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DataAttribute(");
        if (statementDataSource != null) {
            sb.append(statementDataSource);
            sb.append('.');
        }
        sb.append(getName());
        sb.append(')');
        return sb.toString();
    }
}

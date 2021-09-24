package com.zhupanovdm.sql21c.transform.model.db;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.sf.jsqlparser.schema.Table;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode(of = "table")
public class StatementDataSource {
    @Getter
    private final Table table;

    private final List<StatementAttribute> attributes = new LinkedList<>();

    public StatementDataSource(Table table) {
        this.table = table;
    }

    public String getAlias() {
        return table.getAlias() == null ? null : table.getAlias().getName();
    }

    public String getName() {
        return table.getName();
    }

    public List<StatementAttribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public void addAttribute(StatementAttribute attribute) {
        attributes.add(attribute);
    }

    public void setName(String name) {
        table.setName(name);
    }

    @Override
    public String toString() {
        return "DataSource(" + table.getName() + (getAlias() == null ? "" : " as " + getAlias()) + ")";
    }

}

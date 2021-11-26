package com.zhupanovdm.sql21c.transform.model.db;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.sf.jsqlparser.schema.Table;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode(of = "table", callSuper = false)
public class StatementTable extends StatementDataSource {
    private final Table table;

    public StatementTable(Table table) {
        this.table = table;
    }

    public String getName() {
        return table.getName();
    }

    public void setName(String name) {
        table.setName(name);
    }

    @Override
    public String getAlias() {
        return table.getAlias() == null ? null : table.getAlias().getName();
    }

    @Override
    public String toString() {
        return "StatementTable(" + table.getName() + (getAlias() == null ? "" : " as " + getAlias()) + ")";
    }
}

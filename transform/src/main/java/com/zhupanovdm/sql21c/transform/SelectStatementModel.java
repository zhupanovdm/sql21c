package com.zhupanovdm.sql21c.transform;

import com.zhupanovdm.sql21c.transform.model.db.StatementAttribute;
import com.zhupanovdm.sql21c.transform.model.db.StatementDataSource;
import com.zhupanovdm.sql21c.transform.model.db.StatementSubSelect;
import com.zhupanovdm.sql21c.transform.model.db.StatementTable;
import lombok.Data;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.*;

@Data
public class SelectStatementModel {
    private final SelectStatementModel parent;
    private final List<StatementTable> tables = new LinkedList<>();
    private final List<StatementSubSelect> subSelects = new LinkedList<>();
    private final Map<String, StatementDataSource> sourceAliases = new HashMap<>();
    private final Set<StatementAttribute> unknownAttributes = new HashSet<>();

    public SelectStatementModel() {
        this(null);
    }

    public SelectStatementModel(SelectStatementModel parent) {
        this.parent = parent;
    }

    public void addColumn(Column column) {
        Table table = column.getTable();
        if (table == null) {
            unknownAttributes.add(new StatementAttribute(column));
        } else {
            Optional<StatementTable> t = tables.stream().filter(sds -> table.getName().equals(sds.getName())).findFirst();
            StatementDataSource ds = t.isEmpty() ? sourceAliases.get(table.getName()) : t.get();
            if (ds == null) {
                if (parent != null) {
                    parent.addColumn(column);
                    return;
                }
                throw new IllegalStateException("Cannot find entity for field: " + column.getFullyQualifiedName());
            }
            ds.addAttribute(new StatementAttribute(ds, column));
        }
    }

    public StatementDataSource addDataSource(SubSelect src) {
        StatementSubSelect subSelect = new StatementSubSelect(src, this);
        subSelects.add(subSelect);
        return subSelect;
    }

    public StatementDataSource addDataSource(Table src) {
        StatementTable table = new StatementTable(src);
        tables.add(table);
        return table;
    }

    public void addFromItem(FromItem src) {
        StatementDataSource dataSource = null;
        if (src instanceof SubSelect) {
            dataSource = addDataSource((SubSelect) src);
        } else if (src instanceof Table) {
            dataSource = addDataSource((Table) src);
        }
        if (dataSource !=null && dataSource.getAlias() != null) {
            sourceAliases.put(dataSource.getAlias(), dataSource);
        }
    }

    public Collection<StatementSubSelect> getSubSelects() {
        return Collections.unmodifiableList(subSelects);
    }
    public Collection<StatementTable> getTables() {
        return Collections.unmodifiableList(tables);
    }
    public Set<StatementAttribute> getUnknownAttributes() {
        return Collections.unmodifiableSet(unknownAttributes);
    }
}

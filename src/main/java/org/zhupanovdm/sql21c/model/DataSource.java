package org.zhupanovdm.sql21c.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.sf.jsqlparser.schema.Table;

@EqualsAndHashCode
@AllArgsConstructor
public class DataSource {

    @Getter
    private String name;
    @Getter
    private String alias;

    public static DataSource fromTable(Table table) {
        String name = table.getName();
        return new DataSource(name, table.getAlias() == null ? null : table.getAlias().getName());
    }

    public String getReference() {
        return alias == null ? name : alias;
    }

    @Override
    public String toString() {
        return "DataSource(" + name + (alias == null ? "" : " as " + alias) + ")";
    }

}

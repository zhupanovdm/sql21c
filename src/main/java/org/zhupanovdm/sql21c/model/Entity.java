package org.zhupanovdm.sql21c.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.sf.jsqlparser.schema.Table;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode(of = "table")
public class Entity {
    @Getter
    private final Table table;

    private final List<EntityAttribute> attributes = new LinkedList<>();

    public Entity(Table table) {
        this.table = table;
    }

    public String getAlias() {
        return table.getAlias() == null ? null : table.getAlias().getName();
    }

    public String getReference() {
        return getAlias() == null ? getName() : getAlias();
    }

    public String getName() {
        return table.getName();
    }

    public List<EntityAttribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public void addAttribute(EntityAttribute attribute) {
        attributes.add(attribute);
    }

    @Override
    public String toString() {
        return "DataSource(" + table.getName() + (getAlias() == null ? "" : " as " + getAlias()) + ")";
    }

}

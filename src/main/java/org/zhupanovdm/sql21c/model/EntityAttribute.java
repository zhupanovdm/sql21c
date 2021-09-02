package org.zhupanovdm.sql21c.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.sf.jsqlparser.schema.Column;

@EqualsAndHashCode
public class EntityAttribute {
    @Getter
    private final Entity entity;
    @Getter
    private final Column attribute;

    public EntityAttribute(Entity entity, Column attribute) {
        this.entity = entity;
        this.attribute = attribute;

        entity.addAttribute(this);
    }

    public String getName() {
        return attribute.getColumnName();
    }

    public String getFullyQualifiedName() {
        return attribute.getFullyQualifiedName();
    }

    @Override
    public String toString() {
        return "DataAttribute(" + entity.getReference() + "." + getName() + ")";
    }

}
